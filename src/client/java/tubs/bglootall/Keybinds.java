package tubs.bglootall;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static tubs.bglootall.Constants.MY_LOGGER;


public class Keybinds {

    public static KeyBinding manageIgnoreListKey;
    public static KeyBinding lootAllKey;
    public static KeyBinding lootSomeKey;
    public static KeyBinding depositAllKey;
    public static KeyBinding depositMatchingKey;


    // Overlay message state for IgnoreList Display Messages
    private static String overlayMessage = "";
    private static long overlayUntil = 0;

    public static void register() {
        // Register for Controls menu (so users can rebind)
        lootAllKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bg-loot-all.lootAllHotkey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "category.bg-loot-all.loot"
        ));
        lootSomeKey= KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bg-loot-all.lootSomeHotkey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.bg-loot-all.loot"
        ));

        depositAllKey= KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bg-loot-all.depositAllHotkey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.bg-loot-all.loot"
        ));
        depositMatchingKey= KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bg-loot-all.depositMatchingHotkey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.bg-loot-all.loot"
        ));

        manageIgnoreListKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bg-loot-all.manageIgnoreList",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                "category.bg-loot-all.debug"
        ));

        // global tick listener for hotkeys that should work OUTSIDE GUIs - ie just our ignore list manager one for now
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (manageIgnoreListKey.wasPressed()) {
                handleIgnoreList(client);
            }
        });

        // see src/client/java/tubs/bglootall/mixin/client/HandledScreenMixin.java for where we manage the chest hotkeys

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            if (!overlayMessage.isEmpty() && System.currentTimeMillis() < overlayUntil) {
                var mc = MinecraftClient.getInstance();
                context.drawText(mc.textRenderer, overlayMessage, 5, 25, 0xFFFF55, true);
            }
        });
    }

    private static void handleIgnoreList(MinecraftClient client) {
        var stack = client.player.getMainHandStack();
        if (stack.isEmpty()) {
            String message = "[IgnoreList] Nothing in hand";
            MY_LOGGER.info(message);
            showOverlay(message);
            return;
        }

        String robustIdentifier = LootSomeIgnoreList.getUniqueItemIdentifier(stack); // registry ID

        boolean shift = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT);

        boolean ctrl = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL);

        if (ctrl) {
            // List all entries
            String message = "[IgnoreList] Current entries: " + LootSomeIgnoreList.ITEMS;
                    MY_LOGGER.info(message);
            showOverlay(message);
        } else if (shift) {
            // Remove
            if (LootSomeIgnoreList.ITEMS.remove(robustIdentifier)) {
                LootSomeIgnoreList.save(); // persist
                String message = "[IgnoreList] Removed: " + robustIdentifier;
                MY_LOGGER.info(message);
                showOverlay(message);
            } else {
                String message = "[IgnoreList] Not found: " + robustIdentifier;
                MY_LOGGER.info(message);
                showOverlay(message);
            }
        } else {
            // Add
            LootSomeIgnoreList.ITEMS.add(robustIdentifier);
            LootSomeIgnoreList.save();
            String message = "[IgnoreList] Added: " + robustIdentifier;
            MY_LOGGER.info(message);
            showOverlay(message);
        }
    }

    public static void showOverlay(String message) {
        if (!tubs.bglootall.config.ConfigManager.CONFIG.debugOverlay) {
            return; // only show if debug is enabled
        }
        overlayMessage = message;
        overlayUntil = System.currentTimeMillis() + 3000; // show for 3s
    }
}

