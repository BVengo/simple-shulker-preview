package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Shulker Preview Mod
 */
@Environment(EnvType.CLIENT)
public class SimpleShulkerPreviewMod implements ClientModInitializer {
	public static final String MOD_ID = "simpleshulkerpreview";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ModConfig CONFIGS = new ModConfig();

	@Override
	public void onInitializeClient() {
        LOGGER.info("{} loading...", LOGGER.getName());
		CONFIGS.load();
		CONFIGS.save();  // Replace old config file with new one to ensure all fields are present
        LOGGER.info("{} loaded.", LOGGER.getName());
	}
}
