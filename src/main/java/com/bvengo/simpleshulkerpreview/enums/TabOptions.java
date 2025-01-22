package com.bvengo.simpleshulkerpreview.enums;

import net.minecraft.util.TranslatableOption;

public enum TabOptions implements TranslatableOption {
	GENERAL,
	SHULKER,
	BUNDLE,
	STACKED,
	COMPATIBILITY;

	private static final String TRANSLATION_GROUP = "options.tabOptions.";

	@Override
	public int getId() {
		return this.ordinal();
	}

	@Override
	public String getTranslationKey() {
		return TRANSLATION_GROUP + this.name().toLowerCase();
	}
}
