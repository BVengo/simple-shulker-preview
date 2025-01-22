package com.bvengo.simpleshulkerpreview.enums;

import net.minecraft.util.TranslatableOption;

/** Shulker box slots that can be displayed */
public enum IconDisplayOption implements TranslatableOption {
    FIRST,
    LAST,
    UNIQUE,
    MOST,
    LEAST;

    private static final String TRANSLATION_GROUP = "options.iconDisplay.";

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getTranslationKey() {
        return TRANSLATION_GROUP + this.name().toLowerCase();
    }
}