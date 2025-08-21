package tubs.bglootall;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import tubs.bglootall.mixin.client.HandledScreenAccessor;

import static tubs.bglootall.Constants.*;
import static tubs.bglootall.ContainerActions.depositAll;

public class DepositAllHelper {
    /**
     * Adds a Deposit All button to a handled screen above the player inventory.
     */
    public static void addDepositAllButton(HandledScreenAccessor accessor) {
        // diff 5
        int diff = 5;
        int x = accessor.getX() + accessor.getBackgroundWidth() - BUTTON_SIDE_MARGIN - BUTTON_WIDTH +diff;
        int y = accessor.getY() + getPlayerInventoryY((HandledScreen<?>) accessor) + BUTTON_BOTTOM_MARGIN + BUTTON_TOP_MARGIN;

        Screens.getButtons((Screen) accessor).add(new CustomTextSizeButton(
                x, y, BUTTON_WIDTH-diff, BUTTON_HEIGHT,
                Text.literal("Deposit All"),
                () -> depositAll((HandledScreen<?>) accessor),
                (HandledScreen<?>) accessor, DEFAULT_TEXT_SCALE, false
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
