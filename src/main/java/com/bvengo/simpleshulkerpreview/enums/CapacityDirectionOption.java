package com.bvengo.simpleshulkerpreview.enums;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.util.TranslatableOption;

/** Which direction does the capacity bar fill up in? */
public enum CapacityDirectionOption implements TranslatableOption {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP;

    public static String getKey() {
        return "capacityDirection";
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getTranslationKey() {
        return SimpleShulkerPreviewMod.MOD_ID + "options." + getKey() + "." + this.name().toLowerCase();
    }
}