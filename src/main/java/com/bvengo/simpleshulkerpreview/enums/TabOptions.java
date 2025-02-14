package com.bvengo.simpleshulkerpreview.enums;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.util.TranslatableOption;

public enum TabOptions implements TranslatableOption {
	GENERAL,
	SHULKER,
	BUNDLE,
	STACKED,
	COMPATIBILITY;

	public static String getKey() {
		return "tabOptions";
	}

	@Override
	public int getId() {
		return this.ordinal();
	}

	@Override
	public String getTranslationKey() {
		return SimpleShulkerPreviewMod.MOD_ID + ".options." + getKey() + "." + this.name().toLowerCase();
	}
}
