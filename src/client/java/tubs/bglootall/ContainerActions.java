package tubs.bglootall;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static tubs.bglootall.LootSomeIgnoreList.isIgnored;

public class ContainerActions {

    /**
     * Loot everything from the container into the player's inventory,
     * except items on the ignore list.
     */
    public static void lootAll(HandledScreen<?> screen, boolean useIgnoreList) {
        var client = MinecraftClient.getInstance();
        var handler = screen.getScreenHandler();
        var player = client.player;

        if (client.interactionManager == null || client.player == null) return;

        for (int i = 0; i < handler.slots.size(); i++) {
            var slot = handler.slots.get(i);

            // skip player slots and empty slots
            if (slot.inventory == player.getInventory() || !slot.hasStack()) continue;

            var stack = slot.getStack();
            if (useIgnoreList&&isIgnored(LootSomeIgnoreList.getUniqueItemIdentifier(stack))) continue;

            clickSlot(client, handler, player, slot);
        }

    }

    /**
     * Deposit everything from the player's inventory into the container.
     */
    public static void depositAll(HandledScreen<?> screen) {
        var client = MinecraftClient.getInstance();
        var handler = screen.getScreenHandler();
        var player = client.player;

        if (client.interactionManager == null || client.player == null) return;

        int containerSize = handler.slots.size() - player.getInventory().size();
        // TODO: make this optional ignore hotbar (last 9 slots)
//             if (i >= handler.slots.size() - 9) continue;

        for (int i = containerSize; i < handler.slots.size(); i++) {
            var slot = handler.slots.get(i);
            if (!slot.hasStack()) continue;

            clickSlot(client, handler, player, slot);
        }
    }

    /**
     * Deposit only items from the player's inventory that already exist in the container.
     */
    public static void depositMatching(HandledScreen<?> screen) {
        var client = MinecraftClient.getInstance();
        var handler = screen.getScreenHandler();
        var player = client.player;

        if (client.interactionManager == null || client.player == null) return;

        int containerSize = handler.slots.size() - player.getInventory().size();

        // Collect all items already in the container
        Set<Item> containerItems = new HashSet<>();
        for (int i = 0; i < handler.slots.size(); i++) {
            var slot = handler.slots.get(i);

            // skip player slots and empty slots
            if (slot.inventory == player.getInventory() || !slot.hasStack()) continue;

            if (slot.hasStack()) {
                containerItems.add(slot.getStack().getItem());
            }
        }

        // Deposit only matching items
        for (int i = containerSize; i < handler.slots.size(); i++) {
            var slot = handler.slots.get(i);
            // skip non-player slots and empty slots
            if (slot.inventory != player.getInventory() || !slot.hasStack()) continue;

            if (containerItems.contains(slot.getStack().getItem())) {
                clickSlot(client, handler, player, slot);
            }
        }
    }

    /**
     * Shared helper for performing a quick-move slot click.
     */
    private static void clickSlot(MinecraftClient client, ScreenHandler handler, PlayerEntity player, Slot slot) {
        if (client.interactionManager == null || client.player == null) return;
        client.interactionManager.clickSlot(
                handler.syncId,
                slot.id,
                0,
                SlotActionType.QUICK_MOVE,
                player
        );
    }
}


