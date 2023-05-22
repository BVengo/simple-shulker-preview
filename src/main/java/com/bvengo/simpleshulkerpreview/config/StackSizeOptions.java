package com.bvengo.simpleshulkerpreview.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;


public class StackSizeOptions {
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 1, max = 64)
    public int minStackSize = 1;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 1, max = 27)
    public int minStackCount = 1;
}