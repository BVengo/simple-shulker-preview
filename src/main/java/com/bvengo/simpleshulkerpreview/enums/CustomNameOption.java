package com.bvengo.simpleshulkerpreview.enums;

import net.minecraft.util.TranslatableOption;

/** Shulker box slots that can be displayed */
public enum CustomNameOption implements TranslatableOption {
    ALWAYS,
    PREFER,
    NEVER;

    private static final String TRANSLATION_GROUP = "options.customName.";

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getTranslationKey() {
        return TRANSLATION_GROUP + this.name().toLowerCase();
    }
}