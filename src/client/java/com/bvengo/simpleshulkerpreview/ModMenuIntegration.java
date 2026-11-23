package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.YaclScreenBuilder;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Set up Mod Menu.
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return YaclScreenBuilder::createScreen;
    }
}
