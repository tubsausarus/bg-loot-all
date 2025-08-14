package tubs.bglootall;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import tubs.bglootall.mixin.client.HandledScreenAccessor;

import java.util.HashSet;
import java.util.Set;

import static tubs.bglootall.Constants.*;
import static tubs.bglootall.Constants.BUTTON_HEIGHT;
import static tubs.bglootall.Constants.BUTTON_TOP_MARGIN;
import static tubs.bglootall.Constants.BUTTON_WIDTH;

public class DepositMatchingHelper {

    private static void depositMatching(HandledScreen<?> screen) {
        var client = MinecraftClient.getInstance();
        var handler = screen.getScreenHandler();
        var player = client.player;

        if (player == null) return;

        int containerSize = handler.slots.size() - player.getInventory().size();

        // Step 1: Collect container item types
        Set<Item> containerItems = new HashSet<>();
        for (int i = 0; i < containerSize; i++) {
            var slot = handler.slots.get(i);
            if (slot.hasStack()) {
                containerItems.add(slot.getStack().getItem());
            }
        }

        // Step 2: Deposit matching player inventory items
        for (int i = containerSize; i < handler.slots.size(); i++) {
            var slot = handler.slots.get(i);
            if (!slot.hasStack()) continue;

            var stack = slot.getStack();
            if (containerItems.contains(stack.getItem())) {
                client.interactionManager.clickSlot(
                        handler.syncId,
                        slot.id,
                        0,
                        SlotActionType.QUICK_MOVE,
                        player
                );
            }
        }
    }

    /**
     * Adds a Deposit All button to a handled screen above the player inventory.
     */
    public static void addDepositAllButton(HandledScreenAccessor accessor) {
        // TODO: stick this right above the player inventory
        int x = accessor.getX() + accessor.getBackgroundWidth() - BUTTON_SIDE_MARGIN -BUTTON_SEPARATION_MARGIN - BUTTON_WIDTH * 2;
        int y = accessor.getY() + getPlayerInventoryY((HandledScreen<?>) accessor) + BUTTON_BOTTOM_MARGIN + BUTTON_TOP_MARGIN;

        Screens.getButtons((Screen) accessor).add(new CustomTextSizeButton(
                x, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.literal("Stack Match"),
                () -> depositMatching((HandledScreen<?>) accessor),
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
