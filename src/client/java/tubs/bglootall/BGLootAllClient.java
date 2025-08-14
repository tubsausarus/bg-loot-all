package tubs.bglootall;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import tubs.bglootall.mixin.client.HandledScreenAccessor;


import static tubs.bglootall.Constants.*;

public class BGLootAllClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MY_LOGGER.info("Hello from Tubs Loot All Mod!");
        // Load ignore list at startup
        LootSomeIgnoreList.load();

        // Register reload command for the LootSomeIgnoreList
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("lootignore")
                    .then(ClientCommandManager.literal("reload")
                            .executes(ctx -> {
                                LootSomeIgnoreList.load();
                                ctx.getSource().sendFeedback(Text.literal("[LootAll] Ignore list reloaded ("
                                        + LootSomeIgnoreList.ITEMS.size() + " items)"));
                                return 1;
                            })
                    )
            );
        });

        // adds buttons to the allowed screen/containers
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (client.player == null) return; // safety guard
            MY_LOGGER.info("[LootAll] Opened screen: {}", screen.getClass().getName());

            if (screen instanceof HandledScreen<?> handled) {
                var title = handled.getTitle().getString();

                if(containerTitlesForButtons.contains(title)) {
                    LootAllHelper.addLootAllButton((HandledScreenAccessor) handled);
                    LootSomeHelper.addLootSomeButton((HandledScreenAccessor) handled);
                    DepositAllHelper.addDepositAllButton((HandledScreenAccessor) handled);
                    DepositMatchingHelper.addDepositAllButton((HandledScreenAccessor) handled);
                } else{
                    MY_LOGGER.debug("[LootAll] Not adding Button to screen with the values - Screen: {} | HandlerType: {} | Title: {}.",
                            handled.getClass().getName(),
                            handled.getScreenHandler().getType(),
                            title
                    );
                }

            }


        });
    }


}