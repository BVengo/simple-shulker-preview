package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.enums.CapacityDirectionOption;
import com.bvengo.simpleshulkerpreview.enums.CustomNameOption;
import com.bvengo.simpleshulkerpreview.enums.IconDisplayOption;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;


public class ModConfig extends Config {
	protected File getFile() {
		return new File(FabricLoader.getInstance().getConfigDir().toFile(), SimpleShulkerPreviewMod.MOD_ID + ".json");
	}

	// General Options
	public IconDisplayOption displayIcon = IconDisplayOption.FIRST;
	public CustomNameOption customName = CustomNameOption.PREFER;

	public int minStackSize = 1;
	public int minStackCount = 1;

	public boolean groupEnchantments = false;

	// Shulker Box Options
	public int shulkerTranslateX = 4;
	public int shulkerTranslateY = -4;
	public int shulkerScale = 10;

	public boolean showCapacity = true;

	public int capacityTranslateX = 2;
	public int capacityTranslateY = 13;
	public int capacityLength = 13;
	public int capacityWidth = 1;

	public CapacityDirectionOption capacityDirection = CapacityDirectionOption.LEFT_TO_RIGHT;
	public boolean capacityDisplayShadow = true;
	public boolean capacityHideWhenEmpty = true;
	public boolean capacityHideWhenFull = false;

	// Bundle Options
	public boolean supportBundles = true;

	public int bundleTranslateX = 4;
	public int bundleTranslateY = -4;
	public int bundleScale = 10;

	// Compatibility Options
	public boolean supportOtherContainers = false;

	public int stackedTranslateX = 12;
	public int stackedTranslateY = -4;
	public int stackedScale = 10;

	public int shulkerInventoryRows = 3;
	public int shulkerInventoryCols = 9;
}