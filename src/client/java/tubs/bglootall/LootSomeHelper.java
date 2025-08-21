package tubs.bglootall;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import tubs.bglootall.mixin.client.HandledScreenAccessor;

import static tubs.bglootall.Constants.*;
import static tubs.bglootall.ContainerActions.lootAll;

public class LootSomeHelper {

    /**
     * Adds a Loot Some button to a handled screen in the top-right corner.
     */
    public static void addLootSomeButton(HandledScreenAccessor accessor) {
        // 5 diff
        int diff = 5;
        int x = accessor.getX() + accessor.getBackgroundWidth() - BUTTON_SIDE_MARGIN - BUTTON_SEPARATION_MARGIN - (BUTTON_WIDTH *2)+diff +15;
        int y = accessor.getY() + BUTTON_TOP_MARGIN;

        Screens.getButtons((Screen) accessor).add(new CustomTextSizeButton(
                x, y, BUTTON_WIDTH-diff, BUTTON_HEIGHT,
                Text.literal("Loot Some"),
                () -> lootAll((HandledScreen<?>) accessor, true),
                (HandledScreen<?>) accessor, DEFAULT_TEXT_SCALE, true
        ));
    }

}
