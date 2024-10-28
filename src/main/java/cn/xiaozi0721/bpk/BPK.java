package cn.xiaozi0721.bpk;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber
@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, guiFactory = "cn.xiaozi0721.bpk.config.ConfigGuiFactory")
public class BPK {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
    }
}
