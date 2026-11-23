package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.google.gson.FieldNamingPolicy;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class ConfigOptions {
    public static final ConfigClassHandler<ConfigOptions> HANDLER = ConfigClassHandler.createBuilder(ConfigOptions.class)
            .id(Identifier.tryParse(SimpleShulkerPreviewMod.MOD_ID + ":config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("simpleshulkerpreview.json"))
                    // YACL resolves top-level keys itself by field name, but hands nested objects
                    // straight to Gson, whose default naming policy is LOWER_CASE_WITH_UNDERSCORES.
                    // Without this, every nested setting silently falls back to its default.
                    .appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY))
                    .build())
            .build();

    @SerialEntry
    public boolean disableMod = false;

    /**
     * Custom head datapacks and mods, all use the same method. Tested with:
     * - MicroCutting
     * - HeadIndex
     * - JustMobHeads
     * - MoreMobHeads
     * - Player Head Drops
     * - All Mob Heads
     */
    @SerialEntry
    public boolean supportBundles = false;

    /**
     * Support any other item with the 'container' component.
     */
    @SerialEntry
    public boolean supportOtherContainers = false;

    @SerialEntry
    public IconDisplayOption displayIcon = IconDisplayOption.FIRST;

    @SerialEntry
    public CustomNameOption customName = CustomNameOption.PREFER;

    @SerialEntry
    public boolean showPreviewIcon = true;

    @SerialEntry
    public boolean showCapacity = true;

    @SerialEntry
    public boolean hideWhenNoIcon = false;

    @SerialEntry
    public CapacityBarOptions capacityBarOptions = new CapacityBarOptions();

    @SerialEntry
    public boolean overrideBundleIconPosition = false;

    @SerialEntry
    public boolean overrideStackedIconPosition = false;

    @SerialEntry
    public IconPositionOptions iconPositionOptionsGeneral = new IconPositionOptions(12, 4, 10);

    /**
     * x, y, z offsets and scale - default location overlaps with bundles count
     * indicator
     */
    @SerialEntry
    public IconPositionOptions iconPositionOptionsBundle = new IconPositionOptions(12, 4, 10);

    /**
     * Stacked shulkers - tested with Carpet Essential Addons
     * Requested in https://github.com/BVengo/simple-shulker-preview/issues/5
     *
     * x, y, z offsets and scale - different position to avoid overlap with stack
     * size indicator
     */
    @SerialEntry
    public IconPositionOptions iconPositionOptionsStacked = new IconPositionOptions(12, 4, 10);

    @SerialEntry
    public StackSizeOptions stackSizeOptions = new StackSizeOptions();

    @SerialEntry
    public boolean groupEnchantment = false;

    /**
     * Support mods that change the default size of shulkers.
     * Requested in https://github.com/BVengo/simple-shulker-preview/issues/33
     */
    @SerialEntry
    public ShulkerInventoryOptions shulkerInventoryOptions = new ShulkerInventoryOptions();
}
