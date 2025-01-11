package cn.xiaozi0721.bpk.keys;

import cn.xiaozi0721.bpk.config.ConfigHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyInputHandler {
    public static KeyBinding NEW_TOUCH = new KeyBinding("key.bpk.newTouch", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE,"key.category.bpk");

    public static void init() {
        ClientRegistry.registerKeyBinding(NEW_TOUCH);
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (NEW_TOUCH.isPressed()) {
            ConfigHandler.toggleNewTouch();
        }
    }
}
