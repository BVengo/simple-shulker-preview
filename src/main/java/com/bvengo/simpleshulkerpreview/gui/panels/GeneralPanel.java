package com.bvengo.simpleshulkerpreview.gui.panels;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.enums.CustomNameOption;
import com.bvengo.simpleshulkerpreview.enums.IconDisplayOption;
import com.bvengo.simpleshulkerpreview.gui.components.BooleanOptions;
import com.bvengo.simpleshulkerpreview.gui.components.EnumOptions;
import com.bvengo.simpleshulkerpreview.gui.components.IntegerTextInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class GeneralPanel extends Panel {
	public GeneralPanel(int x, int y, int width, int height, MinecraftClient client) {
		super(x, y, width, height, Text.of("General Options"), client);
	}

	@Override
	public void init() {
		int childY = this.getY();
		int fullGap = ELEMENT_HEIGHT + ELEMENT_GAP;

		this.addChild(new EnumOptions<>(
				IconDisplayOption.class, client,
				IconDisplayOption.getKey(), getX(), childY, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.displayIcon,
				value -> SimpleShulkerPreviewMod.CONFIGS.displayIcon = value
		).widget);

		this.addChild(new EnumOptions<>(
				CustomNameOption.class, client,
				CustomNameOption.getKey(), getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.customName,
				value -> SimpleShulkerPreviewMod.CONFIGS.customName = value
		).widget);

		Text minStackText = Text.of("Minimum Stack Size");

		this.addChild(new TextWidget(
				getX(), childY += fullGap, textRenderer.getWidth(minStackText), 20,
				minStackText, this.textRenderer
		));
		this.addChild(new IntegerTextInput(
						textRenderer, getX(), childY += ELEMENT_HEIGHT, elementWidth, 20,
						minStackText, SimpleShulkerPreviewMod.CONFIGS.minStackSize, 1, Integer.MAX_VALUE,
						value -> SimpleShulkerPreviewMod.CONFIGS.minStackSize = value
				));

		Text minCountText = Text.of("Minimum Stack Count");
		this.addChild(new TextWidget(
				getX(), childY += fullGap, this.textRenderer.getWidth(minCountText), 20,
				minCountText, this.textRenderer
		));
		this.addChild(new IntegerTextInput(
						textRenderer, getX(), childY += ELEMENT_HEIGHT, elementWidth, 20,
						minCountText, SimpleShulkerPreviewMod.CONFIGS.minStackCount, 1, Integer.MAX_VALUE,
						value -> SimpleShulkerPreviewMod.CONFIGS.minStackCount = value
				));

		this.addChild(new BooleanOptions(
				client, "groupEnchantments", getX(), childY += fullGap, elementWidth,
				SimpleShulkerPreviewMod.CONFIGS.groupEnchantments,
				value -> SimpleShulkerPreviewMod.CONFIGS.groupEnchantments = value
		).widget);

		this.setContentsHeightWithPadding(childY - ELEMENT_GAP);
	}
}
