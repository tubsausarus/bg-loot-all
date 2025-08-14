package tubs.bglootall;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import tubs.bglootall.mixin.client.HandledScreenAccessor;

import static tubs.bglootall.Constants.*;
public class DepositAllHelper {
    private static void depositAll(HandledScreen<?> screen) {
        var client = MinecraftClient.getInstance();
        var handler = screen.getScreenHandler();
        if (client.interactionManager == null || client.player == null) return;

        // Player inventory slots are always *after* container slots
        int containerSize = handler.slots.size() - client.player.getInventory().size();

        for (int i = containerSize; i < handler.slots.size(); i++) {
            var slot = handler.slots.get(i);
            if (!slot.hasStack()) continue;

            var stack = slot.getStack();
            // Optional: ignore hotbar (last 9 slots)
            // if (i >= handler.slots.size() - 9) continue;

            client.interactionManager.clickSlot(
                    handler.syncId,
                    slot.id,
                    0,
                    SlotActionType.QUICK_MOVE,
                    client.player
            );
        }
    }

    /**
     * Adds a Deposit All button to a handled screen above the player inventory.
     */
    public static void addDepositAllButton(HandledScreenAccessor accessor) {
        // TODO: stick this right above the player inventory
        int x = accessor.getX() + accessor.getBackgroundWidth() - BUTTON_SIDE_MARGIN - BUTTON_WIDTH;
        int y = accessor.getY() + getPlayerInventoryY((HandledScreen<?>) accessor) + BUTTON_BOTTOM_MARGIN + BUTTON_TOP_MARGIN;

        Screens.getButtons((Screen) accessor).add(new CustomTextSizeButton(
                x, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.literal("Deposit All"),
                () -> depositAll((HandledScreen<?>) accessor),
                (HandledScreen<?>) accessor, 0.8f, false
        ));
    }

    private static int getPlayerInventoryY(HandledScreen<?> screen) {
        var handler = screen.getScreenHandler();
        var player = MinecraftClient.getInstance().player;

        // First slot belonging to the player inventory
        int containerSize = handler.slots.size() - player.getInventory().size();
        return handler.slots.get(containerSize).y;
    }

}
