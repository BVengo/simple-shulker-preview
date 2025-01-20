package com.bvengo.simpleshulkerpreview.enums;

public enum TabOptions {
	GENERAL("general"),
	SHULKER("shulker"),
	BUNDLE("bundle"),
	COMPATIBILITY("compatibility");

	private final String name;

	TabOptions(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
