package com.bvengo.simpleshulkerpreview.server;

// import static com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod.MOD_ID;
import static com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod.LOGGER;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

@Environment(EnvType.SERVER)
public class ServerSideSetup implements ModInitializer {
  @Override
  public void onInitialize() {
    LOGGER.info(LOGGER.getName() + " loading...");
  }
}
