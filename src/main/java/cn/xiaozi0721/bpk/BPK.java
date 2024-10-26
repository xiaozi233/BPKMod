package cn.xiaozi0721.bpk;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.IOException;

@Mod.EventBusSubscriber
@Mod(modid = BPK.MOD_ID, guiFactory = "cn.xiaozi0721.bpk.config.ConfigGuiFactory")
public class BPK {
    public static final String MOD_ID = "bpk";

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Hello world!");
    }
}
