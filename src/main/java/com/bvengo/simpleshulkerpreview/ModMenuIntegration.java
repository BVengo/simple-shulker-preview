package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

/**
 * Set up Mod Menu.
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(ConfigOptions.class, parent).get();
    }
}