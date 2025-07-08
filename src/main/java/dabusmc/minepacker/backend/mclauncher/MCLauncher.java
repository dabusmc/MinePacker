package dabusmc.minepacker.backend.mclauncher;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.authorisation.microsoft.MicrosoftAccount;
import dabusmc.minepacker.backend.data.projects.instances.MinecraftInstance;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.logging.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MCLauncher {

    public static final List<String> IGNORED_ARGUMENTS = Arrays.asList("--clientId", "${clientid}", "--xuid",
            "${auth_xuid}");

    public static Process launch(MicrosoftAccount account, MinecraftInstance instance, String nativesTempDir,
                                 String lwjglNativesTempDir, String username) throws Exception {
        return launch(account, instance, null, nativesTempDir, lwjglNativesTempDir, username);
    }

    private static Process launch(MicrosoftAccount account, MinecraftInstance instance, String props, String nativesDir,
                                  String lwjglNativesTempDir, String username) throws Exception {
        File nativesDirFile = new File(nativesDir);

        List<String> arguments = getArguments(account, instance, props, nativesDirFile.getAbsolutePath(),
                lwjglNativesTempDir, username);

        Logger.info("MCLauncher", "Launching Minecraft with the following arguments (user related stuff has been removed): "
                + censorArguments(arguments, account, props, username));

        ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        processBuilder.directory(new File(instance.FileSystem.getBaseDirectory()));
        processBuilder.redirectErrorStream(true);
        processBuilder.environment().remove("_JAVA_OPTIONS");

        // Inject user-defined environment variables mixed with any other environment variables
//        Map<String, String> env = App.settings.environmentVariables;
//        if (!env.isEmpty()) {
//            LogManager.info("Injecting environment variables: " + env);
//            processBuilder.environment().putAll(env);
//        }

        return processBuilder.start();
    }

    private static List<String> getArguments(MicrosoftAccount account, MinecraftInstance instance, String props,
                                             String nativesDir, String lwjglNativesTempDir, String username) {

        StringBuilder cpb = new StringBuilder();
        boolean hasCustomJarMods = false;

        int maximumMemory = 4096;
        int permGen = 256; // NOTE: Would be 128 on 32-bit OSs
        String javaArguments = "-XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M";
        String javaPath = instance.FileSystem.getMinecraftRuntimesDirectory();

//        File jarMods = instance.getJarModsDirectory();
//        File[] jarModFiles = jarMods.listFiles();
//        if (jarMods.exists() && jarModFiles != null) {
//            for (File file : jarModFiles) {
//                hasCustomJarMods = true;
//                cpb.append(file.getAbsolutePath());
//                cpb.append(File.pathSeparator);
//            }
//        }

        instance.Libraries
                .forEach(library -> {
                    if (cpb.indexOf(library.Path) == -1) {
                        cpb.append(library.Path);
                        cpb.append(File.pathSeparator);
                    }
                });

        File binFolder = new File(instance.FileSystem.getBinDirectory());
        File[] libraryFiles = binFolder.listFiles();
        if (binFolder.exists() && libraryFiles != null) {
            for (File file : libraryFiles) {
                if (!file.getName().equalsIgnoreCase("minecraft.jar")
                        && !file.getName().equalsIgnoreCase("modpack.jar")
                        && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))) {
                    Logger.message("MCLauncher","Added in custom library " + file.getName());

                    cpb.append(file);
                    cpb.append(File.pathSeparator);
                }
            }
        }

        // Add minecraft client jar last
        cpb.append(instance.FileSystem.getClientJarFilePath());

        List<String> arguments = new ArrayList<>();

        String path = javaPath + File.separator + "bin" + File.separator + "java";
        if (MinePackerRuntime.Instance.getOS() == MinePackerRuntime.OS.Windows &&
                (Files.exists(Paths.get(path + "w")) || Files.exists(Paths.get(path + "w.exe")))) {
            path += "w";
        }
        arguments.add(path);
        arguments.add("-Xmx" + maximumMemory + "M");

        arguments.add("-Duser.language=en");
        arguments.add("-Duser.country=US");

        arguments.add("-Dlog4j.configurationFile=" + instance.FileSystem.getClientLoggingFilePath());

//        if (hasCustomJarMods) {
//            System.out.println("OH NOES! Avert your eyes!");
//            arguments.add("-Dfml.ignorePatchDiscrepancies=true");
//            arguments.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
//            System.out.println("Okay you can look again, you saw NOTHING!");
//        }

//        if (OS.isMac()) {
//            arguments.add("-Dapple.laf.useScreenMenuBar=true");
//            arguments.add("-Xdock:name=\"" + instance.getName() + "\"");
//
//            if (new File(instance.getAssetsDir(), "icons/minecraft.icns").exists()) {
//                arguments.add(
//                        "-Xdock:icon=" + new File(instance.getAssetsDir(), "icons/minecraft.icns").getAbsolutePath());
//            }
//        }

        if (!javaArguments.isEmpty()) {
            for (String arg : javaArguments.split(" ")) {
                if (!arg.isEmpty()) {
                    arguments.add(arg);
                }
            }
        }

        arguments.add(instance.MainClass);

        String classpath = cpb.toString();
        for (String argument : instance.Arguments.getAsSuperList()) {
            if (IGNORED_ARGUMENTS.contains(argument)) {
                continue;
            }

            arguments.add(replaceArgument(argument, instance, account, props, nativesDir, classpath, username));
        }

        if (MinePackerRuntime.Instance.getOS() == MinePackerRuntime.OS.Windows && !arguments
                .contains("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump")) {
            arguments.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
        }

        // if there's no -Djava.library.path already, then add it (for older versions)
        if (!arguments.stream().anyMatch(arg -> arg.startsWith("-Djava.library.path="))) {
            arguments.add("-Djava.library.path=" + nativesDir);
        }

        // if lwjglNativesTempDir isn't null we need to pass the lwjgl librarypath
        if (lwjglNativesTempDir != null) {
            arguments.add("-Dorg.lwjgl.librarypath=" + lwjglNativesTempDir);
        }

        // if there's no classpath already, then add it (for older versions)
        if (!arguments.contains("-cp")) {
            arguments.add("-cp");
            arguments.add(cpb.toString());
        }

        return arguments;
    }

    private static String replaceArgument(String incomingArgument, MinecraftInstance instance, MicrosoftAccount account,
                                          String props, String nativesDir, String classpath, String username) {
        String argument = incomingArgument;

        argument = argument.replace("${auth_player_name}", username);
        argument = argument.replace("${profile_name}", PackerFile.convertNameToFileName(instance.InstanceProject.getName()));
        argument = argument.replace("${user_properties}", Optional.ofNullable(props).orElse("[]"));
        argument = argument.replace("${version_name}", instance.InstanceProject.getMinecraftVersion().toString());
        argument = argument.replace("${game_directory}", instance.FileSystem.getBaseDirectory());
        argument = argument.replace("${game_assets}", instance.FileSystem.getVirtualDirectory());
        argument = argument.replace("${assets_root}", instance.FileSystem.getAssetsDirectory());
        argument = argument.replace("${assets_index_name}", "24"); // TODO: Keep track of this when creating instance
        argument = argument.replace("${auth_uuid}", account.getRealUUID().toString());
        argument = argument.replace("${auth_access_token}", account.AccessToken.AccessToken);
        argument = argument.replace("${version_type}", "release"); // TODO: Keep track of which versions are what type
        argument = argument.replace("${launcher_name}", "MinePacker");
        argument = argument.replace("${launcher_version}", "0.0.1");
        argument = argument.replace("${natives_directory}", nativesDir);
        argument = argument.replace("${user_type}", account.getUserType());
        argument = argument.replace("${auth_session}", account.AccessToken.AccessToken);
        argument = argument.replace("${library_directory}", instance.FileSystem.getLibrariesDirectory());
        argument = argument.replace("${classpath}", classpath);
        argument = argument.replace("${classpath_separator}", File.pathSeparator);

        return argument;
    }

    private static String censorArguments(List<String> arguments, MicrosoftAccount account, String props,
                                          String username) {
        String argsString = arguments.toString();

        if (props != null) {
            argsString = argsString.replace(props, "REDACTED");
        }
        argsString = argsString.replace(account.AccessToken.AccessToken, "REDACTED");

        return argsString;
    }

}
