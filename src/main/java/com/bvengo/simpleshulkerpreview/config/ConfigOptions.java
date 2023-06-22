package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.config.PositionOptions;
import com.bvengo.simpleshulkerpreview.config.DisplayOption;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;


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

    /** Show the fullness of the shulker box using a fullness bar. */
    @ConfigEntry.Gui.Tooltip()
    public boolean showFullness = true;

    /** x, y, z offsets and scale */
    @ConfigEntry.Gui.CollapsibleObject()
    @ConfigEntry.Gui.Tooltip()
    public PositionOptions positionOptionsGeneral = new PositionOptions(12, 4, 10, 10);

    /** Stack size bounds */
    @ConfigEntry.Gui.CollapsibleObject()
    @ConfigEntry.Gui.Tooltip()
    public StackSizeOptions stackSizeOptions = new StackSizeOptions();
    
    /** Treat enchanted items separately from un-enchanted items */
    @ConfigEntry.Gui.Tooltip()
    public boolean groupEnchantment = false;

    /** Disables the mod. */
    @ConfigEntry.Gui.Tooltip()
    public boolean disableMod = false;

    /**
     * Custom head datapacks and mods, all use the same method. Tested with:
     * - MicroCutting
     * - HeadIndex
     * - JustMobHeads
     * - MoreMobHeads
     * - Player Head Drops
     * - All Mob Heads
     */
    @ConfigEntry.Category("compatibility")
    /** Include bundles. */
    @ConfigEntry.Gui.Tooltip()
    public boolean supportBundles = false;

    /** x, y, z offsets and scale - default location overlaps with bundles count indicator */
    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.CollapsibleObject()
    @ConfigEntry.Gui.Tooltip()
    public PositionOptions positionOptionsBundle = new PositionOptions(12, 4, 10, 10);

    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.Tooltip()
    public boolean supportCustomHeads = false;

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

    /** x, y, z offsets and scale - default location overlaps with bundles count indicator */
    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.CollapsibleObject()
    @ConfigEntry.Gui.Tooltip()
    public PositionOptions positionOptionsStacked = new PositionOptions(12, 4, 10, 10);
}