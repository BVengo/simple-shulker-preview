package com.bvengo.simpleshulkerpreview.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import java.io.File;
import java.nio.file.Paths;


public class ModConfigFile {
    private ModConfigFile() { }

    private static String storedFilePath = "config/simpleshulkerpreview.json";

    /**
     * Saves the config.
     */
    public final static Runnable saveRunnable = () -> {

        final FileConfig config = FileConfig.builder(Paths.get(storedFilePath)).concurrent().autosave().build();

        final Config general = Config.inMemory();
        general.set("disable_mod", ConfigOptions.disableMod);
        general.set("display_unique", ConfigOptions.displayUnique);
        general.set("display_item", ConfigOptions.displayItem);

        config.set("general", general);

        config.close();
    };

    /**
     * Loads the config.
     */
    public static void load() {

        final File file = new File(storedFilePath);

        if (!file.exists()) {
            return;
        }

        final FileConfig config = FileConfig.builder(file).concurrent().autosave().build();

        config.load();

        final Config general = config.getOrElse("general", () -> null);

        if (general != null) {
            ConfigOptions.disableMod = general.getOrElse("disable_mod", false);
            ConfigOptions.displayUnique = general.getOrElse("display_unique", false);
            ConfigOptions.displayItem = general.getOrElse("display_item", "first");

            if(!(ConfigOptions.displayItem.equals("first") || ConfigOptions.displayItem.equals("last"))) {
                ConfigOptions.displayItem = "first";
            }
        }

        config.close();

    }
}
