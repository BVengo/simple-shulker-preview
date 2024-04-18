package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = SimpleShulkerPreviewMod.MOD_ID)
public class ConfigOptions implements ConfigData {
    /**
     * Which slot of the container should be displayed
     * FIRST - the first item available in the container
     * LAST - the last item available in the container
     * UNIQUE - only display if there is one item type in the container
     * MOST - displays which item there is the most of in the container
     */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public IconDisplayOption displayIcon = IconDisplayOption.FIRST;

    /**
     * Whether to use the custom name to determine the icon
     * PREFER - prefer the custom name if valid, otherwise use the slot option
     * ALWAYS - always use the custom name, otherwise don't display
     * NEVER - never use the custom name
     */
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public UseCustomNameOption useCustomName = UseCustomNameOption.PREFER;

    /** x, y, z offsets and scale */
    @ConfigEntry.Gui.CollapsibleObject()
    @ConfigEntry.Gui.Tooltip()
    public IconPositionOptions iconPositionOptionsGeneral = new IconPositionOptions(12, 4, 10, 10);

    /** Stack size bounds */
    @ConfigEntry.Gui.CollapsibleObject()
    @ConfigEntry.Gui.Tooltip()
    public StackSizeOptions stackSizeOptions = new StackSizeOptions();

    /** Treat enchanted items separately from un-enchanted items */
    @ConfigEntry.Gui.Tooltip()
    public boolean groupEnchantment = false;

    /** Capacity Bar. */
    @ConfigEntry.Gui.Tooltip()
    public boolean showCapacity = true;

    @ConfigEntry.Gui.CollapsibleObject()
    @ConfigEntry.Gui.Tooltip()
    public CapacityBarOptions capacityBarOptions = new CapacityBarOptions();

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

    /**
     * x, y, z offsets and scale - default location overlaps with bundles count
     * indicator
     */
    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.CollapsibleObject()
    @ConfigEntry.Gui.Tooltip()
    public IconPositionOptions iconPositionOptionsBundle = new IconPositionOptions(12, 4, 10, 10);

    /** Stacked shulkers - tested with Carpet Essential Addons */
    /**
     * x, y, z offsets and scale - different position to avoid overlap with stack
     * size indicator
     */
    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.CollapsibleObject()
    @ConfigEntry.Gui.Tooltip()
    public IconPositionOptions iconPositionOptionsStacked = new IconPositionOptions(12, 4, 10, 10);

    @ConfigEntry.Category("compatibility")
    @ConfigEntry.Gui.Tooltip()
    public boolean supportRecursiveShulkers = false;
}