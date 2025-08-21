package tubs.bglootall;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static tubs.bglootall.Constants.MY_LOGGER;


public class IgnoreListKeybinds {

    private static KeyBinding manageIgnoreListKey;

    // Overlay message state
    private static String overlayMessage = "";
    private static long overlayUntil = 0;

    public static void register() {
        manageIgnoreListKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mymod.manageIgnoreList",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                "category.mymod.debug"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (manageIgnoreListKey.wasPressed()) {
                if (client.player != null) {
                    handleKeyPress(client);
                }
            }
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            if (!overlayMessage.isEmpty() && System.currentTimeMillis() < overlayUntil) {
                var mc = MinecraftClient.getInstance();
                context.drawText(mc.textRenderer, overlayMessage, 5, 25, 0xFFFF55, true);
            }
        });
    }

    private static void handleKeyPress(MinecraftClient client) {
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

    private static void showOverlay(String message) {
        overlayMessage = message;
        overlayUntil = System.currentTimeMillis() + 3000; // show for 3s
    }
}

