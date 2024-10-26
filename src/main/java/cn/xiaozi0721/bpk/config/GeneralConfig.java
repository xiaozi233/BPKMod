package cn.xiaozi0721.bpk.config;

import cn.xiaozi0721.bpk.BPK;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = BPK.MOD_ID, category = "BPK Mod")
public class GeneralConfig {
    @Mod.EventBusSubscriber(modid = BPK.MOD_ID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(BPK.MOD_ID)) {
                ConfigManager.sync(BPK.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }

    @Config.Comment({"Simulate new touch strafe.", "If enabled, the strafe angle will be changed to 11.48(arccos(0.98))"})
    public static boolean isNewTouch = false;

    @Config.Comment({"If enabled, you could sprint even if you are backward."})
    public static boolean isSprintBackward = false;
}
