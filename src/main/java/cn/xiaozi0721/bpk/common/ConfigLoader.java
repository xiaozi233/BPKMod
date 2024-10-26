package cn.xiaozi0721.bpk.common;

import net.minecraft.block.BlockContainer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

public class ConfigLoader {
    private static Configuration config;

    private static Logger logger;

    public static Boolean isNewTouch;

    public ConfigLoader(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();
        load();
    }

    public static void load()
    {
        logger.info("Started loading config. ");
        String comment;

        comment = "Simulate new touch strafe.\nIf enabled, the strafe angle will be changed to 11.48(arccos(0.98))";
        isNewTouch = config.get(Configuration.CATEGORY_GENERAL, "isNewTouch", false, comment).getBoolean();

        config.save();
        logger.info("Finished loading config. ");
    }

    public static Logger logger()
    {
        return logger;
    }
}
