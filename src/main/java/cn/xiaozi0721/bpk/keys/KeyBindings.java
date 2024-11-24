package cn.xiaozi0721.bpk.keys;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public class KeyBindings {
    public static KeyBinding NEW_TOUCH = new KeyBinding("key.bpk.newtouch", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE,"key.category.bpk");

    public static void init() {
        ClientRegistry.registerKeyBinding(NEW_TOUCH);
    }
}
