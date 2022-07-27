package com.bvengo.simpleshulkerpreview.config;

import net.minecraft.text.Text;
import net.minecraft.util.Language;

import java.util.HashMap;

public final class ConfigOptions {

    /**
     * Disables the mod.
     */
    public static boolean disableMod = false;

    /**
     * Only displays the icon if the shulker box holds one item type.
     */
    public static boolean displayUnique = false;

    /**
     * Shulker box slots that can be displayed
     */
    public static HashMap<String, String> displayItemOptions = new HashMap<>() {{
        put("first", Language.getInstance().get("config.simpleshulkerpreview.display_item.first"));
        put("last",  Language.getInstance().get("config.simpleshulkerpreview.display_item.last"));
    }};

    /**
     * Which slot of the shulker box should be displayed.
     * Added for compatability with mods that can place items directly from the last slot of a shulker.
     */
    public static String displayItem = "first";

    private ConfigOptions() {}
}
