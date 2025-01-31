package cn.xiaozi0721.bpk.proxy;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonProxy {
    public void postInit() {
        ConfigHandler.syncField();
    }

    public void init() {
    }

}
