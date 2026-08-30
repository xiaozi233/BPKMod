package cn.xiaozi0721.bpk.config;

import cn.xiaozi0721.bpk.BPK;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("bpk.json");

    public static GeneralConfig generalConfig = new GeneralConfig();

    public static class GeneralConfig {
        public boolean sprintBackward = false;
        public double inertiaThreshold = 0.0001D;
        public boolean isBESneak = false;
        public boolean strafeAccelerateAllowed = false;
        public boolean ignoreCollidedHorizontally = true;
        public boolean climbableTrapdoor = true;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                generalConfig = GSON.fromJson(Files.readString(CONFIG_PATH), GeneralConfig.class);
                if (generalConfig == null) {
                    generalConfig = new GeneralConfig();
                }
            } catch (Exception e) {
                BPK.LOGGER.error("Failed to read config, using defaults", e);
                generalConfig = new GeneralConfig();
            }
        }
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(generalConfig));
        } catch (IOException e) {
            BPK.LOGGER.error("Failed to save config", e);
        }
    }
}
