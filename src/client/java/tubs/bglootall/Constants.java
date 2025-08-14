package tubs.bglootall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;

public class Constants {
    public static final String MOD_ID = "tubs-bg-loot-all";
    public static final Logger MY_LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final int BUTTON_WIDTH = 50;
    public static final int BUTTON_HEIGHT = 10;
    public static final int BUTTON_SIDE_MARGIN = 7;
    public static final int BUTTON_SEPARATION_MARGIN = 4;
    public static final int BUTTON_TOP_MARGIN = 5;
    public static final int BUTTON_BOTTOM_MARGIN = 14;
    public static final HashSet<String> containerTitlesForButtons = new HashSet<String>(Arrays.asList("Booty", "Large Chest", "Chest", "Barrel"));

}
