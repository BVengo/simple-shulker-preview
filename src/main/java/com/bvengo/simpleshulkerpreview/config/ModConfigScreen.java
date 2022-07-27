package com.bvengo.simpleshulkerpreview.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

/**
 * The Mod Config screen.
 */
public class ModConfigScreen extends Screen {
    private final Screen parent;

    /**
     * Instantiates a new Mod Config screen.
     *
     * @param parent the parent screen
     */
    public ModConfigScreen(final Screen parent) {
        super(Text.translatable("config.simpleshulkerpreview.title.config"));
        this.parent = parent;
    }

    @Override
    public void init() {
        final MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(configBuilder(client.currentScreen).build());
    }

    /**
     * Instantiates a new Mod Config screen.
     *
     * @param parent the parent screen
     */
    public static ConfigBuilder configBuilder(final Screen parent) {

        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.simpleshulkerpreview.title"));

        builder.setSavingRunnable(ModConfigFile.saveRunnable);

        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        final ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.simpleshulkerpreview.title.general"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.simpleshulkerpreview.disable"), ConfigOptions.disableMod)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("config.simpleshulkerpreview.disable.tooltip"))
                .setSaveConsumer(newValue -> ConfigOptions.disableMod = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.simpleshulkerpreview.unique"), ConfigOptions.displayUnique)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("config.simpleshulkerpreview.unique.tooltip"))
                .setSaveConsumer(newValue -> ConfigOptions.displayUnique = newValue)
                .build());

//        general.addEntry(entryBuilder.startStringDropdownMenu(
//                            Text.translatable("config.simpleshulkerpreview.display_item"),
//                            Language.getInstance().get("config.simpleshulkerpreview." + ConfigOptions.displayItem))
//                .setDefaultValue(ConfigOptions.displayItemOptions.get("first"))
//                .setSelections(ConfigOptions.displayItemOptions.values())
//                .setTooltip(Text.translatable("config.simpleshulkerpreview.display_item.tooltip"))
//                .setSaveConsumer(newValue -> ConfigOptions.displayItem = newValue.toLowerCase())
//                .build());

        general.addEntry(entryBuilder.startStringDropdownMenu(Text.literal("Display Item"), ConfigOptions.displayItem)
                .setDefaultValue(ConfigOptions.displayItem)
                .setSelections(ConfigOptions.displayItemOptions.keySet())
                .setSaveConsumer(newValue -> ConfigOptions.displayItem = newValue)
                .build());

        builder.transparentBackground();
        return builder;
    }
}