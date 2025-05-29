package dabusmc.minepacker.backend.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OSUtils {

    public static boolean executableInPath(String executableName) {
        try {
            return java.util.stream.Stream
                    .of(System.getenv("PATH").split(java.util.regex.Pattern.quote(File.pathSeparator)))
                    .map(path -> path.replace("\"", "")).map(Paths::get)
                    .anyMatch(path -> Files.exists(path.resolve(executableName))
                            && Files.isExecutable(path.resolve(executableName)));
        } catch (Exception e) {
            return false;
        }
    }

}
