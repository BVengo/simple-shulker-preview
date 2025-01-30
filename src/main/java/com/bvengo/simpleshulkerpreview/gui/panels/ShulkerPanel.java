package com.bvengo.simpleshulkerpreview.gui.panels;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.enums.CapacityDirectionOption;
import com.bvengo.simpleshulkerpreview.gui.components.BooleanOptions;
import com.bvengo.simpleshulkerpreview.gui.components.EnumOptions;
import com.bvengo.simpleshulkerpreview.gui.components.IntegerSlider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ShulkerPanel extends Panel {

	public ShulkerPanel(int x, int y, int width, int height, MinecraftClient client) {
		super(x, y, width, height, Text.of("Shulker Options"), client);
	}

	@Override
	public void init() {
		int childY = this.getY();
		int fullGap = ELEMENT_HEIGHT + ELEMENT_GAP;

		// Position sliders
		this.addChild(new IntegerSlider(
				client, "xpos", -8, 8, getX(), childY, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX,
				value -> SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "ypos", -8, 8, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY,
				value -> SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "scale", 1, 16, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.shulkerScale,
				value -> SimpleShulkerPreviewMod.CONFIGS.shulkerScale = value
		).widget);
//
//		// Capacity options
		this.addChild(new BooleanOptions(
				client, "showCapacity", getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.showCapacity,
				value -> SimpleShulkerPreviewMod.CONFIGS.showCapacity = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "capacityX", -8, 8, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityTranslateX,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityTranslateX = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "capacityY", -8, 8, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityTranslateY,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityTranslateY = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "capacityLength", 1, 16, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityLength,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityLength = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "capacityWidth", 1, 8, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityWidth,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityWidth = value
		).widget);

		this.addChild(new EnumOptions<>(
				CapacityDirectionOption.class, client,
				CapacityDirectionOption.getKey(), getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityDirection,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityDirection = value
		).widget);

		this.addChild(new BooleanOptions(
				client, "capacityDisplayShadow", getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityDisplayShadow,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityDisplayShadow = value
		).widget);

		this.addChild(new BooleanOptions(
				client, "capacityHideWhenEmpty", getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityHideWhenEmpty,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityHideWhenEmpty = value
		).widget);

		this.addChild(new BooleanOptions(
				client, "capacityHideWhenFull", getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityHideWhenFull,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityHideWhenFull = value
		).widget);

		this.setContentsHeightWithPadding(childY - ELEMENT_GAP);
	}
}
