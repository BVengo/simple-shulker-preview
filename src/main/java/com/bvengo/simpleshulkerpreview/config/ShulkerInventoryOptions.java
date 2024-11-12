package com.bvengo.simpleshulkerpreview.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ShulkerInventoryOptions {
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(max = 64)
    public int shulkerInventoryRows = 3;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(max = 64)
    public int shulkerInventoryCols = 9;

    public int getSize() {
        return shulkerInventoryRows * shulkerInventoryCols;
    }
}