package cn.xiaozi0721.bpk.keys;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@SuppressWarnings("unused")

public class KeyInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.NEW_TOUCH.isPressed()) {
            ConfigHandler.toggleNewTouch();
        }
    }
}
