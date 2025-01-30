package com.bvengo.simpleshulkerpreview.gui.panels;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.gui.components.BooleanOptions;
import com.bvengo.simpleshulkerpreview.gui.components.IntegerSlider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class BundlePanel extends Panel {

	public BundlePanel(int x, int y, int width, int height, MinecraftClient client) {
		super(x, y, width, height, Text.of("Shulker Options"), client);
	}

	@Override
	public void init() {
		int childY = this.getY();
		int fullGap = ELEMENT_HEIGHT + ELEMENT_GAP;

		this.addChild(new BooleanOptions(
				client, "supportBundles", getX(), childY, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.supportBundles,
				value -> SimpleShulkerPreviewMod.CONFIGS.supportBundles = value
		).widget);

		// Position sliders
		this.addChild(new IntegerSlider(
				client, "xpos", -8, 8, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.bundleTranslateX,
				value -> SimpleShulkerPreviewMod.CONFIGS.bundleTranslateX = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "ypos", -8, 8, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.bundleTranslateY,
				value -> SimpleShulkerPreviewMod.CONFIGS.bundleTranslateY = value
		).widget);

		this.addChild(new IntegerSlider(
				client, "scale", 1, 16, getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.bundleScale,
				value -> SimpleShulkerPreviewMod.CONFIGS.bundleScale = value
		).widget);

		this.setContentsHeightWithPadding(childY - ELEMENT_GAP);
	}
}
