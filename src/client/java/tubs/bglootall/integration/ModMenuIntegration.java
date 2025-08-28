package tubs.bglootall.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import tubs.bglootall.config.ConfigManager;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (Screen parent) -> {
            // Build the Cloth Config screen
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("BG Loot Config"));

            ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // Debug overlay toggle
            general.addEntry(entryBuilder
                    .startBooleanToggle(Text.literal("Show Debug Overlay"), ConfigManager.CONFIG.debugOverlay)
                    .setDefaultValue(false) // default if reset
                    .setSaveConsumer(newValue -> ConfigManager.CONFIG.debugOverlay = newValue)
                    .build()
            );

            builder.setSavingRunnable(ConfigManager::save);

            return builder.build();
        };
    }
}
