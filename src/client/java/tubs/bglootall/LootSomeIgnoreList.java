package tubs.bglootall;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static tubs.bglootall.BGIHelper.getItemId;
import static tubs.bglootall.Constants.MY_LOGGER;

public class LootSomeIgnoreList {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/loot-some_ignorelist.json");

    // ✅ Now it's a set of strings, not Items
    public static final Set<String> ITEMS = new HashSet<>();

    public static void load() {
        try {
            if (!FILE.exists()) {
                saveDefault();
            }

            try (FileReader reader = new FileReader(FILE)) {
                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> rawEntries = GSON.fromJson(reader, listType);

                ITEMS.clear();
                ITEMS.addAll(rawEntries);
            }

            MY_LOGGER.info("[LootAll] Loaded ignore list: {} entries", ITEMS.size());

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
                // default: ignore magma blocks + seeds
                GSON.toJson(List.of(
                        "minecraft:magma_block*Optional.empty*block.minecraft.magma_block",
                        "minecraft:bread*Optional[TOUGH_BREAD]*item.minecraft.bread",
                        "minecraft:bread*Optional[TOUGH_SANDWICH]*item.minecraft.bread",
                        "minecraft:wheat_seeds*Optional.empty*item.minecraft.wheat_seeds",
                        "minecraft:potato*Optional[RAW_POTATO]*item.minecraft.potato",
                        "minecraft:beetroot_seeds*Optional.empty*item.minecraft.beetroot_seeds"
                ), writer);
            }

            MY_LOGGER.info("[LootAll] Created default ignore list config");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(ITEMS, writer);
            MY_LOGGER.info("[LootAll] Saved ignore list ({} entries)", ITEMS.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** ✅ Helper: check if a string (like translation key or registry id) is ignored */
    public static boolean isIgnored(String key) {
        return ITEMS.contains(key);
    }

    public static String getUniqueItemIdentifier(ItemStack stack){
        String id = stack.getItem().toString();
        String bgiId = String.valueOf(getItemId(stack));
        String translationKey = stack.getItem().getTranslationKey();
        return id+"*"+bgiId+"*"+translationKey;
    }
}


