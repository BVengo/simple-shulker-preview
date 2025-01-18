package com.bvengo.simpleshulkerpreview.enums;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.util.Language;

/** Which direction does the capacity bar fill up in? */
public enum CapacityDirectionOption {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP;

    @Override
    public String toString() {
        return Language.getInstance().get("config." + SimpleShulkerPreviewMod.MOD_ID + ".capacityDirection." + this.name().toLowerCase());
    }
}