package org.amicia.wynnlantern.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class WynnlanternClient implements ClientModInitializer {

    private static KeyBinding lanternKeyBinding;
    private static KeyBinding torchKeyBinding;

    private boolean isLanternEquipped = false;
    private boolean isTorchEquipped = false;

    @Override
    public void onInitializeClient() {
        // Keybindings
        lanternKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Lantern",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "WynnLantern"
        ));

        torchKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Torch",
                InputUtil.Type.KEYSYM,
                -1,
                "WynnLantern"
        ));

        // Tick event for key presses
        ClientTickEvents.END_CLIENT_TICK.register(this::handleKeyPresses);

        // Block placement interception
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hand == Hand.OFF_HAND && (isLanternEquipped || isTorchEquipped)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // Item use interception
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (hand == Hand.OFF_HAND && (isLanternEquipped || isTorchEquipped)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }

    private void handleKeyPresses(MinecraftClient client) {
        if (client.player == null) return;

        if (lanternKeyBinding.wasPressed()) toggleLantern(client);
        if (torchKeyBinding.wasPressed()) toggleTorch(client);

        protectOffhand(client);
    }

    private void toggleLantern(MinecraftClient client) {
        if (isLanternEquipped) {
            client.player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            isLanternEquipped = false;
        } else {
            if (isTorchEquipped) {
                client.player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
                isTorchEquipped = false;
            }
            client.player.setStackInHand(Hand.OFF_HAND, Items.LANTERN.getDefaultStack());
            isLanternEquipped = true;
        }
    }

    private void toggleTorch(MinecraftClient client) {
        if (isTorchEquipped) {
            client.player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            isTorchEquipped = false;
        } else {
            if (isLanternEquipped) {
                client.player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
                isLanternEquipped = false;
            }
            client.player.setStackInHand(Hand.OFF_HAND, Items.TORCH.getDefaultStack());
            isTorchEquipped = true;
        }
    }


    private void protectOffhand(MinecraftClient client) {
        if (client.player == null) return;

        ItemStack offhand = client.player.getOffHandStack();

        if (isLanternEquipped && offhand.getItem() != Items.LANTERN) {
            client.player.setStackInHand(Hand.OFF_HAND, Items.LANTERN.getDefaultStack());
        } else if (isTorchEquipped && offhand.getItem() != Items.TORCH) {
            client.player.setStackInHand(Hand.OFF_HAND, Items.TORCH.getDefaultStack());
        }
    }
}
