package com.bvengo.simpleshulkerpreview.enums;

public enum TabOptions {
	GENERAL("general"),
	SHULKER("shulker"),
	BUNDLE("bundle"),
	STACKED("stacked"),
	COMPATIBILITY("compatibility");

	private final String name;

	TabOptions(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
