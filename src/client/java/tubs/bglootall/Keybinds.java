package tubs.bglootall;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static tubs.bglootall.Constants.MOD_ID;
import static tubs.bglootall.Constants.MY_LOGGER;


public class Keybinds {

    public static KeyBinding manageIgnoreListKey;
    public static KeyBinding lootAllKey;
    public static KeyBinding lootSomeKey;
    public static KeyBinding depositAllKey;
    public static KeyBinding depositMatchingKey;

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
    }

    private static void handleIgnoreList(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if(player == null) return;
        ItemStack stack = player.getMainHandStack();

        String robustIdentifier = LootSomeIgnoreList.getUniqueItemIdentifier(stack); // registry ID

        boolean shift = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT);

        boolean ctrl = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL);
        String message = "[IgnoreList] ";
        if (stack.isEmpty() && !ctrl) {
            message += "Nothing in hand";
        }
        else{
            if (ctrl) {
                // List all entries
                message += "Currently has "+LootSomeIgnoreList.ITEMS.size()+" entries";
            } else if (shift) {
                // Remove
                if (LootSomeIgnoreList.ITEMS.remove(robustIdentifier)) {
                    LootSomeIgnoreList.save(); // persist
                    message += "Removed: " + robustIdentifier;
                } else {
                    message = "Not found: " + robustIdentifier;
                }
            } else {

                if(LootSomeIgnoreList.isIgnored(robustIdentifier)){
                    message += "Already contained: " + robustIdentifier;
                }else{
                    // Add
                    LootSomeIgnoreList.ITEMS.add(robustIdentifier);
                    LootSomeIgnoreList.save();
                    message = "Added: " + robustIdentifier;
                }

            }
        }
        MY_LOGGER.info(message);
        showToast(client, message);
    }

    public static void showToast(MinecraftClient client,String message) {
         client.getToastManager().add(
                            SystemToast.create(client, SystemToast.Type.NARRATOR_TOGGLE, Text.of(MOD_ID), Text.of(message))
         );

    }
}

