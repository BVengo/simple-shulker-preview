package com.bvengo.simpleshulkerpreview.gui;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.gui.components.IntegerSlider;
import com.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.client.option.GameOptions.getGenericValueText;

public class OptionsScreen extends Screen {
	protected final Screen parent;
	protected TextFieldWidget tabSelect;  // Temporary until dropdown is implemented

	protected IntegerSlider xPosSlider;
	protected IntegerSlider yPosSlider;
	protected IntegerSlider scaleSlider;

	final int xPadding = 16;

	public OptionsScreen(@Nullable Screen parent) {
		super(Text.translatable("text.autoconfig.simpleshulkerpreview.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.tabSelect = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 8, 200, 20, this.tabSelect, Text.of("Select page..."));
		this.tabSelect.setChangedListener(search -> {}); // TODO: Implement dropdown. Set page to selected value
		this.addSelectableChild(this.tabSelect);

		int sliderWidth = (int)(width / 2.0f - xPadding * 2);

		// Position sliders
		this.xPosSlider = new IntegerSlider(client, "xpos", -8, 8, xPadding, 44, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX,
				value -> SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX = value
		);
		this.addDrawableChild(xPosSlider.widget);

		this.yPosSlider = new IntegerSlider(client, "ypos", -8, 8, xPadding, xPosSlider.widget.getBottom() + 4, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY,
				value -> SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY = value
		);
		this.addDrawableChild(yPosSlider.widget);

		// Scale slider
		this.scaleSlider = new IntegerSlider(client, "scale", 1, 16, xPadding, yPosSlider.widget.getBottom() + 4, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.shulkerScale,
				value -> SimpleShulkerPreviewMod.CONFIGS.shulkerScale = value
		);
		this.addDrawableChild(scaleSlider.widget);
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.tabSelect);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		renderHeader(context, mouseX, mouseY, delta);
		context.drawHorizontalLine(xPadding, this.width - xPadding, 36, Colors.LIGHT_GRAY);

		// Note: Stuff below here will be based on the selected tab, and each will likely be in a different method (switch or classes)
		renderShulker(context);
	}

	private void renderHeader(DrawContext context, int mouseX, int mouseY, float delta) {
		/**
		 * Renders the title and tab selector field
		 */
		final int titleWidth = this.textRenderer.getWidth(this.title);

		int tabSelectWidth = this.width - titleWidth - xPadding * 3;  // padding on each side and between

		this.tabSelect.setWidth(tabSelectWidth);
		this.tabSelect.setX(titleWidth + xPadding * 2); // padding on left of title and between

		this.tabSelect.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, xPadding + titleWidth / 2, 14, 16777215);
	}

	private void renderShulker(DrawContext context) {
		/**
		 * Renders a shulker box with a grass block inside
		 */
		final float scaleMultiplier = 8.0f;

		float shulkerSize = IconRenderer.DEFAULT_SCALE * scaleMultiplier;
		int shulkerCenterX = (int)(this.width * 3.0f / 4.0f) - xPadding;
		int shulkerCenterY = 44 + (int)(shulkerSize / 2.0f);  // 44 is the height of the header

		IconRenderer.setRenderingOptions(context, shulkerSize, 0.0f, 0.0f, 0.0f);
		context.drawItem(Items.SHULKER_BOX.getDefaultStack(), shulkerCenterX, shulkerCenterY);
		IconRenderer.resetRenderingOptions(context);

		// Render a small grass block icon
		IconRenderer.setRenderingOptions(context,
				SimpleShulkerPreviewMod.CONFIGS.shulkerScale * scaleMultiplier,
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX * scaleMultiplier,
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY * scaleMultiplier,
				shulkerSize * scaleMultiplier
		);
		context.drawItemWithoutEntity(Items.GRASS_BLOCK.getDefaultStack(), shulkerCenterX, shulkerCenterY);
		IconRenderer.resetRenderingOptions(context);
	}

	@Override
	public void close() {
		SimpleShulkerPreviewMod.CONFIGS.save();
		this.client.setScreen(parent);
	}
}
