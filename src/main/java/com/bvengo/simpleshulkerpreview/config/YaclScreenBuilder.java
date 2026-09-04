package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class YaclScreenBuilder {

    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(ConfigOptions.HANDLER, (defaults, config, builder) -> {
            // Mutable holder so option listeners can reference the preview widget
            // (which is created later in screenInit).
            final PersistentPreviewWidget[] previewHolder = {null};

            
            // -------------------------
            // GENERAL TAB
            // -------------------------
            Option<Boolean> supportBundlesOpt = Option.<Boolean>createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.supportBundles"))
                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.supportBundles.tooltip")))
                    .stateManager(StateManager.createInstant(defaults.supportBundles, () -> config.supportBundles, val -> config.supportBundles = val))
                    .controller(TickBoxControllerBuilder::create)
                    .build();

            ConfigCategory generalCategory = ConfigCategory.createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.category.general"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.disableMod"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.disableMod.tooltip")))
                                    .stateManager(StateManager.createInstant(!defaults.disableMod, () -> !config.disableMod, val -> config.disableMod = !val))
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.showPreviewIcon"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.showPreviewIcon.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.showPreviewIcon, () -> config.showPreviewIcon, val -> config.showPreviewIcon = val))
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.showCapacity"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.showCapacity.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.showCapacity, () -> config.showCapacity, val -> config.showCapacity = val))
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(supportBundlesOpt)
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.supportOtherContainers"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.supportOtherContainers.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.supportOtherContainers, () -> config.supportOtherContainers, val -> config.supportOtherContainers = val))
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<IconDisplayOption>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.displayIcon"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.displayIcon.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.displayIcon, () -> config.displayIcon, val -> config.displayIcon = val))
                                    .controller(opt -> EnumControllerBuilder.create(opt).enumClass(IconDisplayOption.class).formatValue(val -> Component.translatable("config.simpleshulkerpreview.displayIcon." + val.name().toLowerCase())))
                                    .build())
                            .option(Option.<CustomNameOption>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.customName"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.customName.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.customName, () -> config.customName, val -> config.customName = val))
                                    .controller(opt -> EnumControllerBuilder.create(opt).enumClass(CustomNameOption.class).formatValue(val -> Component.translatable("config.simpleshulkerpreview.customName." + val.name().toLowerCase())))
                                    .build())
                    .build();

            // -------------------------
            // VISUALS TAB
            // -------------------------
            ConfigCategory visualsCategory = ConfigCategory.createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.category.visuals"))
                    .tooltip(Component.translatable("config.simpleshulkerpreview.category.visuals.tooltip"))
                    .group(buildIconPositionGroup("config.simpleshulkerpreview.group.iconPositionOptionsGeneral", config.iconPositionOptionsGeneral, defaults.iconPositionOptionsGeneral))
                    .group(OptionGroup.createBuilder()
                            .name(Component.translatable("config.simpleshulkerpreview.group.capacityBarOptions"))
                            .description(OptionDescription.createBuilder()
                                    .text(Component.translatable("config.simpleshulkerpreview.group.capacityBarOptions.tooltip"))
                                    .build())

                            .option(Option.<CapacityDirectionOption>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.direction"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.direction.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.capacityBarOptions.direction, () -> config.capacityBarOptions.direction, val -> config.capacityBarOptions.direction = val))
                                    .controller(opt -> EnumControllerBuilder.create(opt).enumClass(CapacityDirectionOption.class).formatValue(val -> Component.translatable("config.simpleshulkerpreview.capacityDirection." + val.name().toLowerCase())))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.displayShadow"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.displayShadow.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.capacityBarOptions.displayShadow, () -> config.capacityBarOptions.displayShadow, val -> config.capacityBarOptions.displayShadow = val))
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.length"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.length.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.capacityBarOptions.length, () -> config.capacityBarOptions.length, val -> config.capacityBarOptions.length = val))
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 16).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.width"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.width.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.capacityBarOptions.width, () -> config.capacityBarOptions.width, val -> config.capacityBarOptions.width = val))
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 16).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.capacityBarX"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.capacityBarX.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.capacityBarOptions.translateX, () -> config.capacityBarOptions.translateX, val -> config.capacityBarOptions.translateX = val))
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.capacityBarY"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.capacityBarY.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.capacityBarOptions.translateY, () -> config.capacityBarOptions.translateY, val -> config.capacityBarOptions.translateY = val))
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.hideWhenEmpty"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.hideWhenEmpty.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.capacityBarOptions.hideWhenEmpty, () -> config.capacityBarOptions.hideWhenEmpty, val -> config.capacityBarOptions.hideWhenEmpty = val))
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.hideWhenFull"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.hideWhenFull.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.capacityBarOptions.hideWhenFull, () -> config.capacityBarOptions.hideWhenFull, val -> config.capacityBarOptions.hideWhenFull = val))
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.hideWhenNoIcon"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.hideWhenNoIcon.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.hideWhenNoIcon, () -> config.hideWhenNoIcon, val -> config.hideWhenNoIcon = val))
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .build())
                    .build();

            // -------------------------
            // ADVANCED TAB
            // -------------------------
            Option<Integer> bundleTransXOpt = Option.<Integer>createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.translateX"))
                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.translateX.tooltip")))
                    .stateManager(StateManager.createInstant(defaults.iconPositionOptionsBundle.translateX, () -> config.iconPositionOptionsBundle.translateX, val -> config.iconPositionOptionsBundle.translateX = val))
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
                    .available(config.overrideBundleIconPosition)
                    .build();

            Option<Integer> bundleTransYOpt = Option.<Integer>createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.translateY"))
                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.translateY.tooltip")))
                    .stateManager(StateManager.createInstant(defaults.iconPositionOptionsBundle.translateY, () -> config.iconPositionOptionsBundle.translateY, val -> config.iconPositionOptionsBundle.translateY = val))
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
                    .available(config.overrideBundleIconPosition)
                    .build();

            Option<Integer> bundleScaleOpt = Option.<Integer>createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.scale"))
                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.scale.tooltip")))
                    .stateManager(StateManager.createInstant(defaults.iconPositionOptionsBundle.scale, () -> config.iconPositionOptionsBundle.scale, val -> config.iconPositionOptionsBundle.scale = val))
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 16).step(1))
                    .available(config.overrideBundleIconPosition)
                    .build();

            Option<Boolean> overrideBundleOpt = Option.<Boolean>createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.overrideBundleIconPosition"))
                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.overrideBundleIconPosition.tooltip")))
                    .stateManager(StateManager.createInstant(defaults.overrideBundleIconPosition, () -> config.overrideBundleIconPosition, val -> config.overrideBundleIconPosition = val))
                    .controller(TickBoxControllerBuilder::create)
                    .listener((opt, val) -> {
                        bundleTransXOpt.setAvailable(val);
                        bundleTransYOpt.setAvailable(val);
                        bundleScaleOpt.setAvailable(val);
                    })
                    .build();

            Option<Integer> stackedTransXOpt = Option.<Integer>createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.translateX"))
                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.translateX.tooltip")))
                    .stateManager(StateManager.createInstant(defaults.iconPositionOptionsStacked.translateX, () -> config.iconPositionOptionsStacked.translateX, val -> config.iconPositionOptionsStacked.translateX = val))
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
                    .available(config.overrideStackedIconPosition)
                    .build();

            Option<Integer> stackedTransYOpt = Option.<Integer>createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.translateY"))
                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.translateY.tooltip")))
                    .stateManager(StateManager.createInstant(defaults.iconPositionOptionsStacked.translateY, () -> config.iconPositionOptionsStacked.translateY, val -> config.iconPositionOptionsStacked.translateY = val))
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
                    .available(config.overrideStackedIconPosition)
                    .build();

            Option<Integer> stackedScaleOpt = Option.<Integer>createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.scale"))
                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.scale.tooltip")))
                    .stateManager(StateManager.createInstant(defaults.iconPositionOptionsStacked.scale, () -> config.iconPositionOptionsStacked.scale, val -> config.iconPositionOptionsStacked.scale = val))
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 16).step(1))
                    .available(config.overrideStackedIconPosition)
                    .build();

            Option<Boolean> overrideStackedOpt = Option.<Boolean>createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.overrideStackedIconPosition"))
                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.overrideStackedIconPosition.tooltip")))
                    .stateManager(StateManager.createInstant(defaults.overrideStackedIconPosition, () -> config.overrideStackedIconPosition, val -> config.overrideStackedIconPosition = val))
                    .controller(TickBoxControllerBuilder::create)
                    .listener((opt, val) -> {
                        stackedTransXOpt.setAvailable(val);
                        stackedTransYOpt.setAvailable(val);
                        stackedScaleOpt.setAvailable(val);
                    })
                    .build();

            ConfigCategory advancedCategory = ConfigCategory.createBuilder()
                    .name(Component.translatable("config.simpleshulkerpreview.category.advanced"))
                    .tooltip(Component.translatable("config.simpleshulkerpreview.category.advanced.tooltip"))
                    .option(Option.<Boolean>createBuilder()
                            .name(Component.translatable("config.simpleshulkerpreview.groupEnchantment"))
                            .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.groupEnchantment.tooltip")))
                            .stateManager(StateManager.createInstant(defaults.groupEnchantment, () -> config.groupEnchantment, val -> config.groupEnchantment = val))
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                    .group(OptionGroup.createBuilder()
                            .name(Component.translatable("config.simpleshulkerpreview.group.overrideBundle"))
                            .collapsed(true)
                            .option(overrideBundleOpt)
                            .option(bundleTransXOpt)
                            .option(bundleTransYOpt)
                            .option(bundleScaleOpt)
                            .build())
                    .group(OptionGroup.createBuilder()
                            .name(Component.translatable("config.simpleshulkerpreview.group.overrideStacked"))
                            .collapsed(true)
                            .option(overrideStackedOpt)
                            .option(stackedTransXOpt)
                            .option(stackedTransYOpt)
                            .option(stackedScaleOpt)
                            .build())
                    .group(OptionGroup.createBuilder()
                            .name(Component.translatable("config.simpleshulkerpreview.group.stackSizeOptions"))
                            .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.group.stackSizeOptions.tooltip")))
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.minStackSize"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.minStackSize.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.stackSizeOptions.minStackSize, () -> config.stackSizeOptions.minStackSize, val -> config.stackSizeOptions.minStackSize = val))
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 64).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.minStackCount"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.minStackCount.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.stackSizeOptions.minStackCount, () -> config.stackSizeOptions.minStackCount, val -> config.stackSizeOptions.minStackCount = val))
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 27).step(1))
                                    .build())
                            .build())
                    .group(OptionGroup.createBuilder()
                            .name(Component.translatable("config.simpleshulkerpreview.group.shulkerInventoryOptions"))
                            .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.group.shulkerInventoryOptions.tooltip")))
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.shulkerInventoryRows"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.shulkerInventoryRows.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.shulkerInventoryOptions.shulkerInventoryRows, () -> config.shulkerInventoryOptions.shulkerInventoryRows, val -> config.shulkerInventoryOptions.shulkerInventoryRows = val))
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 64).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.translatable("config.simpleshulkerpreview.shulkerInventoryCols"))
                                    .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.shulkerInventoryCols.tooltip")))
                                    .stateManager(StateManager.createInstant(defaults.shulkerInventoryOptions.shulkerInventoryCols, () -> config.shulkerInventoryOptions.shulkerInventoryCols, val -> config.shulkerInventoryOptions.shulkerInventoryCols = val))
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 64).step(1))
                                    .build())
                            .build())
                    .build();

            return builder
                    .title(Component.translatable("config.simpleshulkerpreview.title"))
                    .category(generalCategory)
                    .category(visualsCategory)
                    .category(advancedCategory)
                    .screenInit(screen -> {
                        // ── Right-panel geometry ────────────────────────────
                        // YACL: left 2/3 = options list, right 1/3 = OptionDescriptionWidget + buttons
                        int rightPanelWidth = screen.width / 3;
                        int rightPanelX     = screen.width - rightPanelWidth;

                        // ── Anchor above search field ───────────────────────
                        // YACL has the search field 22px above undo button.
                        // Read directly, have a fallback. This prevents overlap of the Search Box
                        Tab currentTab = screen.tabNavigationBar != null && screen.tabNavigationBar.getTabManager() != null
                                ? screen.tabNavigationBar.getTabManager().getCurrentTab()
                                : null;

                        int padding = (screen.width / 3) / 20;
                        int searchFieldTop;
                        if (currentTab instanceof YACLScreen.CategoryTab categoryTab && categoryTab.undoButton != null) {
                            searchFieldTop = categoryTab.undoButton.getY() - 22;
                        } else {
                            searchFieldTop = screen.height - padding - 64; // Done(20) + Undo(22) + Search(22)
                        }

                        // ── Preview sizing (responsive to GUI scale) ────────
                        // The slot is 18 item-pixels (including borders).
                        // Target 280ish physical pixels so preview size stays relatively consistent
                        // across all GUI scales.
                        int tabAreaTop = screen.tabArea != null ? screen.tabArea.top() : 23;
                        int availableHeight = Math.max(36, searchFieldTop - tabAreaTop - 30); // 30px buffer below tab bar
                        int availableWidth  = Math.max(36, rightPanelWidth - 20);              // 20px side margin

                        int guiScale = (int) Minecraft.getInstance().getWindow().getGuiScale();
                        if (guiScale <= 0) guiScale = 1;

                        int idealScale = Math.max(2, Math.min(14, (int) Math.round(280.0 / (guiScale * 18))));
                        int maxFitScale = Math.max(2, Math.min((availableHeight - 10) / 18, (availableWidth) / 18));
                        int renderScale = Math.min(idealScale, maxFitScale);

                        int slotPx    = 18 * renderScale;
                        int boxWidth  = slotPx + 12;   // small margin around the slot
                        int boxHeight = slotPx + 16;   // divider + top/bottom padding

                        // ── Vertical positioning (above button area) ────────
                        int boxX = rightPanelX + (rightPanelWidth - boxWidth) / 2;
                        int boxY = searchFieldTop - boxHeight - 6; // 6px gap above search field

                        // ── Inject the preview widget ───────────────────────
                        PersistentPreviewWidget previewBox = new PersistentPreviewWidget(
                                boxX, boxY, boxWidth, boxHeight, config, renderScale
                        );
                        previewBox.setYaclScreen(screen);
                        java.util.Set<Option<?>> bundleOptions = java.util.Set.of(
                                supportBundlesOpt,
                                overrideBundleOpt,
                                bundleTransXOpt,
                                bundleTransYOpt,
                                bundleScaleOpt
                        );
                        previewBox.setBundleOptions(bundleOptions);
                        previewHolder[0] = previewBox;

                        // Put at index 0 so preview renders behind description text on first load and across tab switches
                        var widgets = net.fabricmc.fabric.api.client.screen.v1.Screens.getWidgets(screen);
                        if (!widgets.contains(previewBox)) {
                            widgets.add(0, previewBox);
                        }
                    });
        }).generateScreen(parent);
    }

    private static OptionGroup buildIconPositionGroup(String translationKey, IconPositionOptions configInstance, IconPositionOptions defaultInstance) {
        return OptionGroup.createBuilder()
                .name(Component.translatable(translationKey))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable(translationKey + ".tooltip"))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("config.simpleshulkerpreview.translateX"))
                        .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.translateX.tooltip")))
                        .stateManager(StateManager.createInstant(defaultInstance.translateX, () -> configInstance.translateX, val -> configInstance.translateX = val))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("config.simpleshulkerpreview.translateY"))
                        .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.translateY.tooltip")))
                        .stateManager(StateManager.createInstant(defaultInstance.translateY, () -> configInstance.translateY, val -> configInstance.translateY = val))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("config.simpleshulkerpreview.scale"))
                        .description(OptionDescription.of(Component.translatable("config.simpleshulkerpreview.scale.tooltip")))
                        .stateManager(StateManager.createInstant(defaultInstance.scale, () -> configInstance.scale, val -> configInstance.scale = val))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 16).step(1))
                        .build())
                .build();
    }
}
