package com.bvengo.simpleshulkerpreview.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

public class ShulkerInventoryOptions {
    @SerialEntry
    public int shulkerInventoryRows = 3;

    @SerialEntry
    public int shulkerInventoryCols = 9;

    public int getSize() {
        return shulkerInventoryRows * shulkerInventoryCols;
    }
}
