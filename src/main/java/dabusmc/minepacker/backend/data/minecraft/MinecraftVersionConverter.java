package dabusmc.minepacker.backend.data.minecraft;

import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.logging.Logger;

import java.util.HashMap;

public class MinecraftVersionConverter {

    private static HashMap<String, MinecraftVersion> s_MinecraftVersions;
    private static HashMap<String, Mod.Loader> s_ModLoaders;

    /**
     * Initialize the Version Convertor
     */
    public static void init() {
        s_MinecraftVersions = new HashMap<>();
        s_ModLoaders = new HashMap<>();

        for(MinecraftVersion version : MinecraftVersion.values()) {
            s_MinecraftVersions.put(version.toString(), version);
        }

        for(Mod.Loader loader : Mod.Loader.values()) {
            s_ModLoaders.put(loader.toString(), loader);
        }
    }

    /**
     * Convert a string into a specific {@link MinecraftVersion}
     * @param versionText The string to convert
     * @return {@link MinecraftVersion#MC_1_21_5} if the string is unrecognised, otherwise the associated {@link MinecraftVersion}
     */
    public static MinecraftVersion getVersion(String versionText) {
        if(!s_MinecraftVersions.containsKey(versionText)) {
            Logger.error("MinecraftVersionConverter", "Unrecognised version '" + versionText + "', returning version 1.21.5");
            return MinecraftVersion.MC_1_21_5;
        }

        return s_MinecraftVersions.get(versionText);
    }
    /**
     * Convert a string into a specific {@link Mod.Loader}
     * @param loaderText The string to convert
     * @return {@link Mod.Loader#Vanilla} if the string is unrecognised, otherwise the associated {@link Mod.Loader}
     */
    public static Mod.Loader getLoader(String loaderText) {
        if(!s_ModLoaders.containsKey(loaderText)) {
            Logger.error("MinecraftVersionConverter", "Unrecognised loader '" + loaderText + "', returning Vanilla");
            return Mod.Loader.Vanilla;
        }

        return s_ModLoaders.get(loaderText);
    }

}
