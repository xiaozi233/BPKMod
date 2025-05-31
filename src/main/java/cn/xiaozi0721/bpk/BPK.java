package cn.xiaozi0721.bpk;

import cn.xiaozi0721.bpk.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = BPK.DEPENDENCIES, guiFactory = BPK.CONFIG_GUI)
public class BPK {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    static final String DEPENDENCIES = "required-after:mixinbooter;";
    static final String CONFIG_GUI = "cn.xiaozi0721."+ Tags.MOD_ID + ".config.ConfigGuiFactory";
    private static final String CLIENT_PROXY = "cn.xiaozi0721." + Tags.MOD_ID + ".proxy.ClientProxy";
    private static final String COMMON_PROXY = "cn.xiaozi0721." + Tags.MOD_ID + ".proxy.CommonProxy";

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }
}
