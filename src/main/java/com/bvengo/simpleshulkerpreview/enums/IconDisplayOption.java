package com.bvengo.simpleshulkerpreview.enums;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.util.TranslatableOption;

/** Shulker box slots that can be displayed */
public enum IconDisplayOption implements TranslatableOption {
    FIRST,
    LAST,
    UNIQUE,
    MOST,
    LEAST;

    public static String getKey() {
        return "iconDisplay";
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