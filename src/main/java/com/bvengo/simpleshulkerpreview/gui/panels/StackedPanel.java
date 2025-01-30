package com.bvengo.simpleshulkerpreview.gui.panels;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.enums.CapacityDirectionOption;
import com.bvengo.simpleshulkerpreview.gui.components.BooleanOptions;
import com.bvengo.simpleshulkerpreview.gui.components.EnumOptions;
import com.bvengo.simpleshulkerpreview.gui.components.IntegerSlider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class StackedPanel extends Panel {

	public StackedPanel(int x, int y, int width, int height, MinecraftClient client) {
		super(x, y, width, height, Text.of("Shulker Options"), client);
	}

	@Override
	public void init() {
		int childY = this.getY();
		int fullGap = ELEMENT_HEIGHT + ELEMENT_GAP;

		// Position sliders
		this.addChild(new IntegerSlider(
				client, "xpos", -8, 8, getX(), childY, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.stackedTranslateX,
				value -> SimpleShulkerPreviewMod.CONFIGS.stackedTranslateX = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "ypos", -8, 8, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.stackedTranslateY,
				value -> SimpleShulkerPreviewMod.CONFIGS.stackedTranslateY = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "scale", 1, 16, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.stackedScale,
				value -> SimpleShulkerPreviewMod.CONFIGS.stackedScale = value
		).widget);

		this.setContentsHeightWithPadding(childY - ELEMENT_GAP);
	}
}
