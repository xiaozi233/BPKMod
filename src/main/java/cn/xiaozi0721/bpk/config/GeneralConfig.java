package cn.xiaozi0721.bpk.config;

import cn.xiaozi0721.bpk.BPK;
import net.minecraftforge.common.config.Config;

@Config(modid = BPK.MOD_ID, category = "BPK Mod")
public class GeneralConfig {
    @Config.Comment({"Simulate new touch strafe.", "If enabled, the strafe angle will be changed to 11.48(arccos(0.98))"})
    public static boolean isNewTouch = false;
}
