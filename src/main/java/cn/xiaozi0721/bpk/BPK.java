package cn.xiaozi0721.bpk;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = BPK.MOD_ID)
public class BPK {
    public static final String MOD_ID = "bpk";

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Hello world!");
    }
}
