package cn.xiaozi0721.bpk.config;

import cn.xiaozi0721.bpk.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
@Config(modid = Tags.MOD_ID, category = "BPK Mod")
@Mod.EventBusSubscriber
public class ConfigHandler {

    @Config.Name("General")
    public static GeneralConfig generalConfig;

    public static class GeneralConfig{
        @Config.Comment({"If enabled, the strafe angle will be changed to 11.48 degree(arc-cos(0.98))"})
        @Config.LangKey("config.bpk.general.isNewTouch")
        public static boolean isNewTouch = false;

        @Config.Comment({
                "You have to enabled \"isNewTouch\" first",
                "change your strafe direction by pitch"
        })
        @Config.LangKey("config.bpk.general.byPitch")
        public static boolean byPitch = false;

        @Config.Comment({"If enabled, you could sprint even if you are backward"})
        @Config.LangKey("config.bpk.general.sprintBackward")
        public static boolean sprintBackward = false;

        @Config.Comment("If disabled, sprint in air will not be delayed")
        @Config.LangKey("config.bpk.general.sprintDelay")
        public static boolean sprintDelay = true;

        @Config.Comment({
                "Stops the player from moving when their momentum is deemed negligible",
                "JE 1.8.9: 0.005",
                "JE 1.12.2: 0.003",
                "BE doesn't have such mechanic, if you want to simulate BE, try 0.0001 or less"
        })
        @Config.RangeDouble(min = 0, max = 0.005)
        @Config.LangKey("config.bpk.general.inertiaThreshold")
        public static double inertiaThreshold = 0.0001D;

        @Config.Comment("Toggle sneak to BE mode")
        @Config.LangKey("config.bpk.general.beSneak")
        public static boolean beSneak = false;

        @Config.Comment({
                "Toggle 45-Strafe Accelerate",
                "Priority is lower than NewTouch"
        })
        @Config.LangKey("config.bpk.general.45strafe")
        public static boolean strafeAccelerateAllowed = false;
    }

    public static void toggleNewTouch(){
        GeneralConfig.isNewTouch = !GeneralConfig.isNewTouch;
        ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
    }

    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Tags.MOD_ID)) {
                ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
