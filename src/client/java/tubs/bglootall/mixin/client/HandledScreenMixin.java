package tubs.bglootall.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tubs.bglootall.ContainerActions;
import tubs.bglootall.Keybinds;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;

        // --- Loot All
        if (Keybinds.lootAllKey.matchesKey(keyCode, scanCode)) {
            if (ContainerActions.isSupportedContainer(screen)) {
                ContainerActions.lootAll(screen, false);
                cir.setReturnValue(true);
            }
        }

        // --- Loot Some
        if (Keybinds.lootSomeKey.matchesKey(keyCode, scanCode)) {
            if (ContainerActions.isSupportedContainer(screen)) {
                ContainerActions.lootAll(screen, true);
                cir.setReturnValue(true);
            }
        }

        // --- Deposit All
        if (Keybinds.depositAllKey.matchesKey(keyCode, scanCode)) {
            if (ContainerActions.isSupportedContainer(screen)) {
                ContainerActions.depositAll(screen);
                cir.setReturnValue(true);
            }
        }

        // --- Deposit Matching
        if (Keybinds.depositMatchingKey.matchesKey(keyCode, scanCode)) {
            if (ContainerActions.isSupportedContainer(screen)) {
                ContainerActions.depositMatching(screen);
                cir.setReturnValue(true);
            }
        }
    }
}

