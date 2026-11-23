package com.bvengo.simpleshulkerpreview.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

public class CapacityBarOptions {
    @SerialEntry
    public int translateX = 2;

    @SerialEntry
    public int translateY = 13;

    @SerialEntry
    public int length = 13;

    @SerialEntry
    public int width = 1;

    @SerialEntry
    public CapacityDirectionOption direction = CapacityDirectionOption.LEFT_TO_RIGHT;

    @SerialEntry
    public boolean displayShadow = true;

    @SerialEntry
    public boolean hideWhenEmpty = true;

    @SerialEntry
    public boolean hideWhenFull = false;
}
