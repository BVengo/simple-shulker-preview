package com.bvengo.simpleshulkerpreview.enums;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.util.TranslatableOption;

/** Shulker box slots that can be displayed */
public enum CustomNameOption implements TranslatableOption {
    ALWAYS,
    PREFER,
    NEVER;

    public static String getKey() {
        return "customName";
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