package me.andrew.eyething;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyBinding TOGGLE_MOD = new KeyBinding("key.eyething.toggle_mod", GLFW.GLFW_KEY_UNKNOWN, "key.categories.eyething");
    public static final KeyBinding OPEN_CONFIG = new KeyBinding("key.eyething.open_config", GLFW.GLFW_KEY_UNKNOWN, "key.categories.eyething");
    public static final KeyBinding INCREASE_DISTANCE = new KeyBinding("key.eyething.increase_distance", GLFW.GLFW_KEY_UNKNOWN, "key.categories.eyething");
    public static final KeyBinding DECREASE_DISTANCE = new KeyBinding("key.eyething.decrease_distance", GLFW.GLFW_KEY_UNKNOWN, "key.categories.eyething");

    public static void init() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_MOD);
        KeyBindingHelper.registerKeyBinding(OPEN_CONFIG);
        KeyBindingHelper.registerKeyBinding(INCREASE_DISTANCE);
        KeyBindingHelper.registerKeyBinding(DECREASE_DISTANCE);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_MOD.wasPressed()) {
                setEnabled(!EyeThingMod.enabled);
            }

            while (OPEN_CONFIG.wasPressed()) {
                MinecraftClient.getInstance().setScreen(AutoConfig.getConfigScreen(ModConfig.class, null).get());
            }

            while (INCREASE_DISTANCE.wasPressed()) {
                int maxDistance = EyeThingMod.maxDistance;
                if (maxDistance == 256) {
                    setMaxDistance(maxDistance);
                } else {
                    setMaxDistance(maxDistance + (Integer.highestOneBit(maxDistance) >> 1));
                }
            }

            while (DECREASE_DISTANCE.wasPressed()) {
                int maxDistance = EyeThingMod.maxDistance;
                if (maxDistance == 2) {
                    setMaxDistance(maxDistance);
                } else if (maxDistance == Integer.highestOneBit(maxDistance)) {
                    setMaxDistance(maxDistance - (Integer.highestOneBit(maxDistance) >> 2));
                } else {
                    setMaxDistance(maxDistance - (Integer.highestOneBit(maxDistance) >> 1));
                }
            }
        });
    }

    public static void setEnabled(boolean enabled) {
        EyeThingMod.enabled = enabled;
        sendOverlayMessage(Text.literal((enabled ? "Enabled" : "Disabled") + " Mod"));
    }

    public static void setMaxDistance(int maxDistance) {
        EyeThingMod.maxDistance = maxDistance;
        sendOverlayMessage(Text.literal("Set max distance to " + maxDistance));
    }

    public static void sendOverlayMessage(Text text) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        player.sendMessage(text, true);
    }
}
