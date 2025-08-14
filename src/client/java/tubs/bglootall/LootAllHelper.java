package tubs.bglootall;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import tubs.bglootall.mixin.client.HandledScreenAccessor;

import static tubs.bglootall.Constants.*;


public class LootAllHelper {
    private static boolean isContainerSlot(HandledScreen<?> screen, int index) {
        var client = MinecraftClient.getInstance();
        if (client.player == null) return false; // safety check

        return screen.getScreenHandler().slots.get(index).inventory != client.player.getInventory();
    }

    /**
     * Adds a Loot All button to a handled screen in the top-right corner.
     */
    public static void addLootAllButton(HandledScreenAccessor accessor) {
        int x = accessor.getX() + accessor.getBackgroundWidth() - BUTTON_SIDE_MARGIN - BUTTON_WIDTH;
        int y = accessor.getY() + BUTTON_TOP_MARGIN;

        Screens.getButtons((Screen) accessor).add(new CustomTextSizeButton(
                x, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.literal("Loot All"),
                () -> lootAll((HandledScreen<?>) accessor),
                (HandledScreen<?>) accessor, 0.8f, true
        ));
    }

    private static void lootAll(HandledScreen<?> screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager == null || client.player == null) return;

        for (int i = 0; i < screen.getScreenHandler().slots.size(); i++) {
            if (isContainerSlot(screen, i)) {
                client.interactionManager.clickSlot(
                        screen.getScreenHandler().syncId,
                        i,
                        0,
                        SlotActionType.QUICK_MOVE,
                        client.player
                );
            }
        }

    }

}


