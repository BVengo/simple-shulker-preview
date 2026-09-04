package com.bvengo.simpleshulkerpreview.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

public class IconPositionOptions {
    @SerialEntry
    public int translateX = 12;

    @SerialEntry
    public int translateY = 4;

    @SerialEntry
    public int scale = 10;

    public IconPositionOptions() {
    }

    public IconPositionOptions(int x, int y, int scale) {
        this.translateX = x;
        this.translateY = y;
        this.scale = scale;
    }
}
