package cn.xiaozi0721.bpk.config;

import cn.xiaozi0721.bpk.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
@Config(modid = Tags.MOD_ID, category = "BPK Mod")
@Mod.EventBusSubscriber
public class ConfigHandler {
    @Name("General")
    public static GeneralConfig generalConfig;

    public static class GeneralConfig{
        @Comment({"If enabled, the strafe angle will be changed to 11.48 degree(arc-cos(0.98))"})
        @LangKey("config.bpk.general.isNewTouch")
        public static boolean isNewTouch = false;

        @Comment({
                "You have to enabled \"isNewTouch\" first",
                "change your strafe direction by pitch"
        })
        @LangKey("config.bpk.general.byPitch")
        public static boolean byPitch = false;

        @Comment({"If enabled, you could sprint even if you are backward"})
        @LangKey("config.bpk.general.sprintBackward")
        public static boolean sprintBackward = false;

        @Comment("If disabled, sprint in air will not be delayed")
        @LangKey("config.bpk.general.sprintDelayInAir")
        public static boolean sprintDelayInAir = true;

        @Comment("If disabled, sprint on ground will not be delayed")
        @LangKey("config.bpk.general.sprintDelayOnGround")
            public static boolean sprintDelayOnGround = false;

        @Comment({
                "Stops the player from moving when their momentum is deemed negligible",
                "JE 1.8.9: 0.005",
                "JE 1.12.2: 0.003",
                "BE doesn't have such mechanic, if you want to simulate BE, try 0.0001 or less"
        })
        @RangeDouble(min = 0, max = 0.005)
        @LangKey("config.bpk.general.inertiaThreshold")
        public static double inertiaThreshold = 0.0001D;

        @Comment("Toggle sneak to BE mode")
        @LangKey("config.bpk.general.beSneak")
        public static boolean beSneak = false;

        @Ignore
        public static float sneakHeight;

        @Comment("Toggle 45-Strafe Accelerate")
        @LangKey("config.bpk.general.45strafe")
        public static boolean strafeAccelerateAllowed = false;

        @Comment("Ignore Collided Horizontally")
        @LangKey("config.bpk.general.ignoreCollidedHorizontally")
        public static boolean ignoreCollidedHorizontally = true;
    }

    public static void toggleNewTouch(){
        GeneralConfig.isNewTouch = !GeneralConfig.isNewTouch;
        GeneralConfig.strafeAccelerateAllowed = GeneralConfig.isNewTouch;
        ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
    }

    public static void syncField(){
        GeneralConfig.sneakHeight = GeneralConfig.beSneak ? 1.5F : 1.65F;
    }

    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Tags.MOD_ID)) {
                ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
                syncField();
            }
        }
    }
}
