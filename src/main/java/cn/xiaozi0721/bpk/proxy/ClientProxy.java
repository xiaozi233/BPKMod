package cn.xiaozi0721.bpk.proxy;

import cn.xiaozi0721.bpk.keys.KeyInputHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy{
    @Override
    public void init() {
        KeyInputHandler.init();
    }
}
