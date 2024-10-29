package cn.xiaozi0721.bpk.config;

import cn.xiaozi0721.bpk.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Tags.MOD_ID, category = "BPK Mod")
@Config.LangKey("config.bpk")
public class GeneralConfig {
    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Tags.MOD_ID)) {
                ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }

    @Config.Comment({"If enabled, the strafe angle will be changed to 11.48 degree(arccos(0.98))"})
    public static boolean isNewTouch = false;

    @Config.Comment({"If enabled, you could sprint even if you are backward"})

    public static boolean sprintBackward = false;

    @Config.Comment("If disabled, sprint in air will not be delayed")
    public static boolean sprintDelay = true;

    @Config.Comment({
                    "Stops the player from moving when their momentum is deemed negligible",
                    "JE 1.8.9: 0.005",
                    "JE 1.12.2: 0.003",
                    "BE doesn't have such mechanic, if you want to simulate BE, try 0.0001 or less"})
    @Config.RangeDouble(min =0, max = 0.005)
    public static double inertiaThreshold = 0.0001D;

}
