package com.bvengo.simpleshulkerpreview.gui.panels;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.gui.components.BooleanOptions;
import com.bvengo.simpleshulkerpreview.gui.components.IntegerTextInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class CompatibilityPanel extends Panel {
	public CompatibilityPanel(int x, int y, int width, int height, MinecraftClient client) {
		super(x, y, width, height, Text.of("General Options"), client);
	}

	@Override
	public void init() {
		int childY = this.getY();
		int fullGap = ELEMENT_HEIGHT + ELEMENT_GAP;

		this.addChild(new BooleanOptions(
				client, "groupEnchantments", getX(), childY, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.groupEnchantments,
				value -> SimpleShulkerPreviewMod.CONFIGS.groupEnchantments = value
		).widget);

		Text rowText = Text.of("Shulker Inventory Rows");
		this.addChild(new TextWidget(getX(), childY += fullGap, this.textRenderer.getWidth(rowText), 20, rowText, this.textRenderer));
		this.addChild(new IntegerTextInput(
						this.textRenderer, getX(), childY += fullGap, elementWidth, 20,
						rowText, SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryRows, 1, Integer.MAX_VALUE,
						value -> SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryRows = value
				)
		);

		Text colText = Text.of("Shulker Inventory Cols");
		this.addChild(new TextWidget(getX(), childY += fullGap, this.textRenderer.getWidth(colText), 20, colText, this.textRenderer));
		this.addChild(new IntegerTextInput(
						this.textRenderer, getX(), childY += fullGap, elementWidth, 20,
						colText, SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryCols, 1, Integer.MAX_VALUE,
						value -> SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryCols = value
				)
		);

		this.setContentsHeightWithPadding(childY - ELEMENT_GAP);
	}
}
