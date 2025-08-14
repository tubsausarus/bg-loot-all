package tubs.bglootall;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import static tubs.bglootall.Constants.MY_LOGGER;

public class LootSomeIgnoreList {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/loot-some_ignorelist.json");

    public static final Set<Item> ITEMS = new HashSet<>();

    public static void load() {
        try {
            if (!FILE.exists()) {
                // default config
                saveDefault();
            }

            try (FileReader reader = new FileReader(FILE)) {
                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> itemIds = GSON.fromJson(reader, listType);

                ITEMS.clear();
                for (String id : itemIds) {
                    try {
                        Identifier identifier = Identifier.of(id);
                        Item item = Registries.ITEM.get(identifier);
                        if (item != Items.AIR) { // ✅ AIR check instead of null
                            ITEMS.add(item);
                        } else {
                            MY_LOGGER.warn("[LootAll] Unknown item in ignore list: {}", id);
                        }
                    } catch (Exception e) {
                        MY_LOGGER.warn("[LootAll] Invalid identifier in ignore list: {}", id);
                    }
                }
            }

            MY_LOGGER.warn("[LootAll] Loaded ignore list: {} items", ITEMS.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void saveDefault() {
        try {
            File parent = FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    MY_LOGGER.warn("[LootAll] Warning: Failed to create config directory {}", parent.getAbsolutePath());
                }
            }

            try (FileWriter writer = new FileWriter(FILE)) {
                GSON.toJson(List.of("minecraft:magma_block"), writer);
            }

            MY_LOGGER.info("[LootAll] Created default ignore list config");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

