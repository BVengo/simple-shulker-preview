package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Language;

@Config(name = SimpleShulkerPreviewMod.MOD_ID)
public class ConfigOptions implements ConfigData {
    /**
     * Which slot of the shulker box should be displayed.
     * FIRST - the first item available in the box
     * LAST - the last item available in the box
     * UNIQUE - only display if there is one item type in the box
     * MOST - displays which item there is the most of in the box
     */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public DisplayOption displayItem = DisplayOption.FIRST;

    /** x offset */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public double translateX = 12.0;

    /** y offset */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public double translateY = 12.0;

    /** z offset - for if it appears above or below other mod overlays */
    @ConfigEntry.Gui.Tooltip()
    public double translateZ = 100.0;

    /** scale value */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public float scale = 10.0f;

    /** Disables the mod. */
    @ConfigEntry.Gui.Tooltip()
    public boolean disableMod = false;

    /** Shulker box slots that can be displayed */
    public enum DisplayOption {
        FIRST,
        LAST,
        UNIQUE,
        MOST,
        LEAST;

        @Override
        public String toString() {
            return Language.getInstance().get("config." + SimpleShulkerPreviewMod.MOD_ID + ".display_item." + this.name().toLowerCase());
        }
    }
}
