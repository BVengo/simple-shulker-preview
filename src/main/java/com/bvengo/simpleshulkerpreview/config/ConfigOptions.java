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

    /**
     * Whether to separate items with different NBT:
     */
    @ConfigEntry.Gui.Tooltip()
    public boolean compareNBT = true;

    /** Treat enchanted items separately from un-enchanted items */
    @ConfigEntry.Gui.Tooltip()
    public boolean groupEnchantment = false;

    /** x offset */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int translateX = 12;

    /** y offset */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int translateY = 12;

    /** z offset - for if it appears above or below other mod overlays */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int translateZ = 10;

    /** scale value */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int scale = 10;

    /** Randomize preview item if there are multiple options. */
    @ConfigEntry.Gui.Tooltip()
    public boolean changePreview = true;

    /** Show shulker as count instead if full of one resource. */
    @ConfigEntry.Gui.Tooltip()
    public boolean shulkerCount = true;


    /** Show items on shulkers in item frames */
    //@ConfigEntry.Gui.Tooltip()
    public boolean itemFrame = true;

    /** Show items above shulkers on entities */
    //@ConfigEntry.Gui.Tooltip()
    public boolean entityHeld = true;

    /** Show items above shulkers in your hands */
    //@ConfigEntry.Gui.Tooltip()
    public boolean selfHeld = false;

    /** Disables the mod. */
    @ConfigEntry.Gui.Tooltip()
    public boolean disableMod = false;

    /**
     * Recursive and stacking shulkers. Tested with:
     * -  Carpet - EssentialAddons
     */
    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.Tooltip()
    public boolean supportRecursiveShulkers = false;

    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.Tooltip()
    public boolean supportStackedShulkers = false;

    /** x offset - default location overlaps with stacked shulkers count indicator */
    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int stackedTranslateX = 4;

    /** y offset - default location overlaps with stacked shulkers count indicator */
    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int stackedTranslateY = 12;

    /** z offset - default location overlaps with stacked shulkers count indicator */
    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int stackedTranslateZ = 9;

    /** scale value - default location overlaps with stacked shulkers count indicator*/
    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int stackedScale = 10;

    /** Shulker box slots that can be displayed */
    public enum DisplayOption {
        FIRST,
        LAST,
        UNIQUE,
        MOST,
        LEAST,

        RANDOM;

        @Override
        public String toString() {
            return Language.getInstance().get("config." + SimpleShulkerPreviewMod.MOD_ID + ".display_item." + this.name().toLowerCase());
        }
    }
}
