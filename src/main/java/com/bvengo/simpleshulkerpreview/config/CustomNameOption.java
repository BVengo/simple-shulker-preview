package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.locale.Language;

/** Shulker box slots that can be displayed */
public enum CustomNameOption {
    ALWAYS,
    PREFER,
    NEVER;

    @Override
    public String toString() {
        return Language.getInstance().getOrDefault("config." + SimpleShulkerPreviewMod.MOD_ID + ".customName." + this.name().toLowerCase());
    }
}