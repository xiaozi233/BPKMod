package cn.xiaozi0721.bpk.config;

import cn.xiaozi0721.bpk.BPK;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class ConfigHandler {
    public static BPKConfig generalConfig;

    public static void load() {
        try {
            generalConfig = AutoConfig.register(BPKConfig.class, JanksonConfigSerializer::new).getConfig();
        } catch (Exception e) {
            BPK.LOGGER.error("Failed to load config, using defaults", e);
            generalConfig = new BPKConfig();
        }
    }
}
