package tubs.bglootall;

import dev.bnjc.bglib.BGIData;
import dev.bnjc.bglib.BGIField;
import dev.bnjc.bglib.BGIParser;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class BGIHelper {
    public static Optional<String> getItemId(ItemStack itemStack) {
        Optional<BGIData> parseResult = BGIParser.parse(itemStack).result();
        if (parseResult.isEmpty()) {
            return Optional.empty();
        }

        BGIData bgiData = parseResult.get();
        return bgiData.getString(BGIField.ITEM_ID);
    }
}
