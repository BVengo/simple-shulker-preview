package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.locale.Language;

/** Shulker box slots that can be displayed */
public enum IconDisplayOption {
    FIRST,
    LAST,
    UNIQUE,
    MOST,
    LEAST;

    @Override
    public String toString() {
        return Language.getInstance().getOrDefault("config." + SimpleShulkerPreviewMod.MOD_ID + ".displayIcon." + this.name().toLowerCase());
    }
}