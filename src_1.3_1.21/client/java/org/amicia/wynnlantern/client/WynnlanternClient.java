package org.amicia.wynnlantern.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class WynnlanternClient implements ClientModInitializer {

    // Keybindings for pulling out the lantern and torch
    private static KeyBinding lanternKeyBinding;
    private static KeyBinding torchKeyBinding;

    // Flags to keep track of lantern and torch state
    private boolean isLanternEquipped = false;
    private boolean isTorchEquipped = false;

    @Override
    public void onInitializeClient() {
        // Register the keybindings
        lanternKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Lantern", // Translation key
                InputUtil.Type.KEYSYM, // Key type
                GLFW.GLFW_KEY_L, // Default key ("L" for Lantern)
                "WynnLantern" // Keybinding category
        ));

        torchKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Torch", // Translation key
                InputUtil.Type.KEYSYM, // Key type
                -1, // No default key (unbound)
                "WynnLantern" // Keybinding category
        ));

        // Register the tick event to check for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (lanternKeyBinding.wasPressed()) {
                handleLanternToggle(client);
            }
            if (torchKeyBinding.wasPressed()) {
                handleTorchToggle(client);
            }
        });
    }

    private void handleLanternToggle(MinecraftClient client) {
        if (client.player != null) {
            if (isLanternEquipped) {
                // Remove the lantern from the offhand
                client.player.setStackInHand(Hand.OFF_HAND, Items.AIR.getDefaultStack());
                isLanternEquipped = false;
            } else {
                // Remove the torch if it's equipped
                if (isTorchEquipped) {
                    client.player.setStackInHand(Hand.OFF_HAND, Items.AIR.getDefaultStack());
                    isTorchEquipped = false;
                }
                // Equip the lantern in the offhand
                client.player.setStackInHand(Hand.OFF_HAND, Items.LANTERN.getDefaultStack());
                isLanternEquipped = true;
            }
        }
    }

    private void handleTorchToggle(MinecraftClient client) {
        if (client.player != null) {
            if (isTorchEquipped) {
                // Remove the torch from the offhand
                client.player.setStackInHand(Hand.OFF_HAND, Items.AIR.getDefaultStack());
                isTorchEquipped = false;
            } else {
                // Remove the lantern if it's equipped
                if (isLanternEquipped) {
                    client.player.setStackInHand(Hand.OFF_HAND, Items.AIR.getDefaultStack());
                    isLanternEquipped = false;
                }
                // Equip the torch in the offhand
                client.player.setStackInHand(Hand.OFF_HAND, Items.TORCH.getDefaultStack());
                isTorchEquipped = true;
            }
        }
    }
}