package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.ModConfigFile;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Shulker Preview Mod
 */
@Environment(EnvType.CLIENT)
public class SimpleShulkerPreviewMod implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("simpleshulkerpreview");

	@Override
	public void onInitializeClient() {
		LOGGER.info(LOGGER.getName() + " loading...");

		// Setup config with JSON file type
		ModConfigFile.load();

		LOGGER.info(LOGGER.getName() + " loaded.");
	}
}
