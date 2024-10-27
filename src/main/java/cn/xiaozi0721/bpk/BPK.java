package cn.xiaozi0721.bpk;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod.EventBusSubscriber
@Mod(modid = BPK.MOD_ID, dependencies = "after:aquaacrobatics",guiFactory = "cn.xiaozi0721.bpk.config.ConfigGuiFactory")
public class BPK {
    public static final String MOD_ID = "bpk";

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Hello world!");
    }
}
