package cn.xiaozi0721.bpk;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPK implements ModInitializer {
    public static final String MOD_ID = "bpk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ConfigHandler.load();
        LOGGER.info("Hello From BPK Mod!");
    }
}
