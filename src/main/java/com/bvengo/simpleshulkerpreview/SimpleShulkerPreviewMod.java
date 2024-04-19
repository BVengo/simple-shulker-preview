package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
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

	@Override
	public void onInitializeClient() {
        LOGGER.info("{} loading...", LOGGER.getName());

		AutoConfig.register(ConfigOptions.class, GsonConfigSerializer::new);

        LOGGER.info("{} loaded.", LOGGER.getName());
	}
}
