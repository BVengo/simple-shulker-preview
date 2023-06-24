package com.bvengo.simpleshulkerpreview.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class CapacityBarOptions {
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
    public int translateX = 2;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
    public int translateY = 13;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
    public int length = 13;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
    public int width = 1;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public CapacityDirectionOption direction = CapacityDirectionOption.LEFT_TO_RIGHT;

    @ConfigEntry.Gui.Tooltip()
    public boolean displayShadow = true;

    @ConfigEntry.Gui.Tooltip()
    public boolean hideWhenEmpty = true;

    @ConfigEntry.Gui.Tooltip()
    public boolean hideWhenFull = false;
}