package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Language;

@Config(name = SimpleShulkerPreviewMod.MOD_ID)
public class ConfigOptions implements ConfigData {
    /** Disables the mod. */
    @ConfigEntry.Gui.Tooltip()
    public boolean disableMod = false;

    /** Only displays the icon if the shulker box holds one item type. */
    @ConfigEntry.Gui.Tooltip()
    public boolean displayUnique = false;

    /**
     * Which slot of the shulker box should be displayed.
     * Added for compatability with mods that can place items directly from the last slot of a shulker.
     */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public DisplayItem displayItem = DisplayItem.FIRST;

    /** Shulker box slots that can be displayed */
    public enum DisplayItem {
        FIRST,
        LAST;

        @Override
        public String toString() {
            return Language.getInstance().get("config.simpleshulkerpreview.display_item." + this.name().toLowerCase());
        }
    }
}
