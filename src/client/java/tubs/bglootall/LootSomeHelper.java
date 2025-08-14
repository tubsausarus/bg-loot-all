package tubs.bglootall;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import tubs.bglootall.mixin.client.HandledScreenAccessor;

import static tubs.bglootall.Constants.*;

public class LootSomeHelper {

    /**
     * Adds a Loot All button to a handled screen in the top-right corner.
     */
    public static void addLootSomeButton(HandledScreenAccessor accessor) {
        int x = accessor.getX() + accessor.getBackgroundWidth() - BUTTON_SIDE_MARGIN - BUTTON_SEPARATION_MARGIN - BUTTON_WIDTH *2;
        int y = accessor.getY() + BUTTON_TOP_MARGIN;

        Screens.getButtons((Screen) accessor).add(new CustomTextSizeButton(
                x, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.literal("Loot Some"),
                () -> lootAllExcept((HandledScreen<?>) accessor),
                (HandledScreen<?>) accessor, 0.8f, true
        ));
    }
    private static void lootAllExcept(HandledScreen<?> screen) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.interactionManager == null || client.player == null) {
            return; // safety guard
        }

        for (int slotIndex = 0; slotIndex < screen.getScreenHandler().slots.size(); slotIndex++) {
            var slot = screen.getScreenHandler().slots.get(slotIndex);

            if (slot.inventory != client.player.getInventory() && slot.hasStack()) {
                var stack = slot.getStack();

                // 🚫 Skip blacklisted items
                if (LootSomeIgnoreList.ITEMS.contains(stack.getItem())) {
                    continue;
                }

                client.interactionManager.clickSlot(
                        screen.getScreenHandler().syncId,
                        slotIndex,
                        0,
                        net.minecraft.screen.slot.SlotActionType.QUICK_MOVE,
                        client.player
                );
            }
        }
    }

}
