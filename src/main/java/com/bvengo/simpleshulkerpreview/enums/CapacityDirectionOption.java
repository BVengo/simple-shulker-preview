package com.bvengo.simpleshulkerpreview.enums;

import net.minecraft.util.TranslatableOption;

/** Which direction does the capacity bar fill up in? */
public enum CapacityDirectionOption implements TranslatableOption {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP;

    private static final String TRANSLATION_GROUP = "options.capacityDirection.";

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getTranslationKey() {
        return TRANSLATION_GROUP + this.name().toLowerCase();
    }
}