package com.bvengo.simpleshulkerpreview.gui;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.enums.CapacityDirectionOption;
import com.bvengo.simpleshulkerpreview.enums.TabOptions;
import com.bvengo.simpleshulkerpreview.gui.components.IntegerSlider;
import com.bvengo.simpleshulkerpreview.gui.components.IntegerTextInput;
import com.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class OptionsScreen extends Screen {
	protected final Screen parent;
	protected TextFieldWidget tabSelect;  // Temporary until dropdown is implemented

	TabOptions selectedTab = TabOptions.GENERAL;

	final int xPadding = 16;
	int sliderWidth = 0;

	public OptionsScreen(@Nullable Screen parent) {
		super(Text.translatable("text.autoconfig.simpleshulkerpreview.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.tabSelect = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 8, 200, 20, Text.of("Select page..."));
		this.tabSelect.setChangedListener(search -> {
			if(search.isEmpty()) return;
			try {
				TabOptions tab = TabOptions.valueOf(search.toUpperCase());
				selectTab(tab);
			} catch (IllegalArgumentException ignored) {}
		}); // TODO: Implement dropdown. Set page to selected value

		sliderWidth = (int)(width / 2.0f - xPadding * 2);

		selectTab(selectedTab);
	}

	private void selectTab(TabOptions tab) {
		/**
		 * Selects the tab to display
		 */
		this.selectedTab = tab;

		this.clearChildren();
		this.addDrawableChild(this.tabSelect);

		switch (tab) {
			case GENERAL:
				initGeneralOptions();
				break;
			case SHULKER:
				initShulkerOptions();
				break;
			case BUNDLE:
				initBundleOptions();
				break;
			case STACKED:
				initStackedShulkersOptions();
				break;
			case COMPATIBILITY:
				initCompatibilityOptions();
				break;
			default:
				break;
		}
	}

	private void initGeneralOptions() {
		// enum dropdown displayIcon
		// enum dropdown customName

		int inputX = (int)(width / 2.0f + xPadding);
		int inputWidth = (int)(width / 2.0f - xPadding * 2);

		Text minStackText = Text.of("Minimum Stack Size");
		this.addDrawable(new TextWidget(xPadding, 44, this.textRenderer.getWidth(minStackText), 20, minStackText, this.textRenderer));
		this.addDrawableChild(new IntegerTextInput(
				this.textRenderer, inputX, 44, inputWidth, 20,
				minStackText, SimpleShulkerPreviewMod.CONFIGS.minStackSize, 1, Integer.MAX_VALUE,
				value -> SimpleShulkerPreviewMod.CONFIGS.minStackSize = value
			)
		);

		Text minCountText = Text.of("Minimum Stack Count");
		this.addDrawable(new TextWidget(xPadding, 68, this.textRenderer.getWidth(minCountText), 20, minCountText, this.textRenderer));
		this.addDrawableChild(new IntegerTextInput(
				this.textRenderer, inputX, 68, inputWidth, 20,
				minCountText, SimpleShulkerPreviewMod.CONFIGS.minStackCount, 1, Integer.MAX_VALUE,
				value -> SimpleShulkerPreviewMod.CONFIGS.minStackCount = value
			)
		);

		// groupEnchantments boolean button

	}

	private void initShulkerOptions() {
		// TODO: Use a scrollable area for these options

		// Position sliders
		this.addDrawableChild(new IntegerSlider(
				client, "xpos", -8, 8, xPadding, 44, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX,
				value -> SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX = value
			).widget);

		this.addDrawableChild(new IntegerSlider(
				client, "ypos", -8, 8, xPadding, 72, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY,
				value -> SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY = value
			).widget);

		this.addDrawableChild(new IntegerSlider(
				client, "scale", 1, 16, xPadding, 100, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.shulkerScale,
				value -> SimpleShulkerPreviewMod.CONFIGS.shulkerScale = value
			).widget);

		// Capacity sliders
		// boolean showCapacity

		this.addDrawableChild(new IntegerSlider(
				client, "capacityX", -8, 8, xPadding, 128, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityTranslateX,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityTranslateX = value
			).widget);

		this.addDrawableChild(new IntegerSlider(
				client, "capacityY", -8, 8, xPadding, 156, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityTranslateY,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityTranslateY = value
			).widget);

		this.addDrawableChild(new IntegerSlider(
				client, "capacityLength", 1, 16, xPadding, 184, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityLength,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityLength = value
			).widget);

		this.addDrawableChild(new IntegerSlider(
				client, "capacityWidth", 1, 8, xPadding, 212, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.capacityWidth,
				value -> SimpleShulkerPreviewMod.CONFIGS.capacityWidth = value
			).widget);

		// dropdown capacity direction
		// boolean capacityDisplayShadow
		// boolean capacityHideWhenEmpty
		// boolean capacityHideWhenFull
	}

	private void initBundleOptions() {
		// bopolean supportBundles

		// Position sliders
		this.addDrawableChild(new IntegerSlider(
				client, "xpos", -8, 8, xPadding, 44, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.bundleTranslateX,
				value -> SimpleShulkerPreviewMod.CONFIGS.bundleTranslateX = value
			).widget);

		this.addDrawableChild(new IntegerSlider(
				client, "ypos", -8, 8, xPadding, 72, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.bundleTranslateY,
				value -> SimpleShulkerPreviewMod.CONFIGS.bundleTranslateY = value
			).widget);

		this.addDrawableChild(new IntegerSlider(
				client, "scale", 1, 16, xPadding, 100, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.bundleScale,
				value -> SimpleShulkerPreviewMod.CONFIGS.bundleScale = value
			).widget);
	}

	private void initStackedShulkersOptions() {
		// Position sliders
		this.addDrawableChild(new IntegerSlider(
				client, "xpos", -8, 8, xPadding, 44, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.stackedTranslateX,
				value -> SimpleShulkerPreviewMod.CONFIGS.stackedTranslateX = value
			).widget);

		this.addDrawableChild(new IntegerSlider(
				client, "ypos", -8, 8, xPadding, 72, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.stackedTranslateY,
				value -> SimpleShulkerPreviewMod.CONFIGS.stackedTranslateY = value
			).widget);

		this.addDrawableChild(new IntegerSlider(
				client, "scale", 1, 16, xPadding, 100, sliderWidth,
				SimpleShulkerPreviewMod.CONFIGS.stackedScale,
				value -> SimpleShulkerPreviewMod.CONFIGS.stackedScale = value
			).widget);
	}

	private void initCompatibilityOptions() {
		// boolean supportOtherContainers

		int inputX = (int)(width / 2.0f + xPadding);
		int inputWidth = (int)(width / 2.0f - xPadding * 2);

		Text rowText = Text.of("Shulker Inventory Rows");
		this.addDrawable(new TextWidget(xPadding, 44, this.textRenderer.getWidth(rowText), 20, rowText, this.textRenderer));
		this.addDrawableChild(new IntegerTextInput(
						this.textRenderer, inputX, 44, inputWidth, 20,
						rowText, SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryCols, 1, Integer.MAX_VALUE,
						value -> SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryCols = value
				)
		);

		Text colText = Text.of("Shulker Inventory Cols");
		this.addDrawable(new TextWidget(xPadding, 68, this.textRenderer.getWidth(colText), 20, colText, this.textRenderer));
		this.addDrawableChild(new IntegerTextInput(
						this.textRenderer, inputX, 68, inputWidth, 20,
						colText, SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryCols, 1, Integer.MAX_VALUE,
						value -> SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryCols = value
				)
		);

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

		switch(selectedTab) {
			case GENERAL:
				renderGeneralOptions(context, mouseX, mouseY, delta);
				break;
			case SHULKER:
				renderShulkerOptions(context, mouseX, mouseY, delta);
				break;
			case BUNDLE:
				renderBundleOptions(context, mouseX, mouseY, delta);
				break;
			case STACKED:
				renderStackedShulkersOptions(context, mouseX, mouseY, delta);
				break;
			case COMPATIBILITY:
				renderCompatibilityOptions(context, mouseX, mouseY, delta);
				break;
			default:
				break;
		}
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

	private void renderGeneralOptions(DrawContext context, int mouseX, int mouseY, float delta) {
		/**
		 * Renders the general options tab
		 */
	}

	private void renderShulkerOptions(DrawContext context, int mouseX, int mouseY, float delta) {
		/**
		 * Renders the shulker options tab
		 */
		renderItem(
				context,
				Items.SHULKER_BOX.getDefaultStack(),
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX,
				SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY,
				SimpleShulkerPreviewMod.CONFIGS.shulkerScale
		);
	}

	private void renderBundleOptions(DrawContext context, int mouseX, int mouseY, float delta) {
		/**
		 * Renders the bundle options tab
		 */
		renderItem(
				context,
				Items.BUNDLE.getDefaultStack(),
				SimpleShulkerPreviewMod.CONFIGS.bundleTranslateX,
				SimpleShulkerPreviewMod.CONFIGS.bundleTranslateY,
				SimpleShulkerPreviewMod.CONFIGS.bundleScale
		);
	}

	private void renderStackedShulkersOptions(DrawContext context, int mouseX, int mouseY, float delta) {
		/**
		 * Renders the stacked shulkers options tab
		 */
		ItemStack shulkerStack = Items.SHULKER_BOX.getDefaultStack();
		shulkerStack.setCount(64);

		renderItem(
				context,
				shulkerStack,
				SimpleShulkerPreviewMod.CONFIGS.stackedTranslateX,
				SimpleShulkerPreviewMod.CONFIGS.stackedTranslateY,
				SimpleShulkerPreviewMod.CONFIGS.stackedScale
		);
	}

	private void renderCompatibilityOptions(DrawContext context, int mouseX, int mouseY, float delta) {
		/**
		 * Renders the compatibility options tab
		 */
	}

	private void renderText(DrawContext context, String text, int x, int y, int z, float scale) {
		/**
		 * Renders text at the specified position
		 */
		context.getMatrices().push();
		context.getMatrices().translate(0, 0, z);
		context.getMatrices().scale(scale, scale, 1.0f);
		context.drawText(textRenderer, text, (int)(x / scale), (int)(y / scale), Colors.WHITE, true);
		context.getMatrices().pop();
	}

	private void renderItem(DrawContext context, ItemStack stack, int overlayX, int overlayY, int overlayScale) {
		/**
		 * Renders an item stack at the specified position
		 * TODO: Unify this with MC code, so it is a single render call with only a custom scale and position set
		 */
		// Calculations
		final Identifier SLOT_FRAME_TEXTURE = Identifier.ofVanilla("widget/slot_frame");

		final float scaleMultiplier = 8.0f;
		float itemSize = IconRenderer.DEFAULT_SCALE * scaleMultiplier;
		int itemCenterX = (int)(this.width * 3.0f / 4.0f - xPadding);
		int itemCenterY = (int)(itemSize / 2.0f) + 44;  // 44 is the height of the header

		// Render the slot
		int slotX = itemCenterX - (int)(itemSize / 2.0f);
		int slotY = itemCenterY - (int)(itemSize / 2.0f);
		int slotSize = (int)(itemSize + scaleMultiplier * 2);

		context.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, Colors.GRAY);
		context.drawGuiTexture(RenderLayer::getGuiTextured, SLOT_FRAME_TEXTURE, slotX, slotY, slotSize, slotSize);

		// Render the item stack
		IconRenderer.setRenderingOptions(context, itemSize, 0.0f, 0.0f, 0.0f);
		context.drawItem(stack, itemCenterX, itemCenterY);

		if (stack.getCount() != 1) {
			String string = String.valueOf(stack.getCount());
			int textX = slotX + slotSize - (int)(textRenderer.getWidth(string) * scaleMultiplier);
			int textY = itemCenterY + 9;

			renderText(context, string, textX, textY, (int)(itemSize * 2), scaleMultiplier);
		}

		IconRenderer.resetRenderingOptions(context);

		// Render a small grass block icon
		IconRenderer.setRenderingOptions(context,
				overlayScale * scaleMultiplier,
				overlayX * scaleMultiplier,
				overlayY * scaleMultiplier,
				itemSize  // Size of the shulker ItemStack is our z-offset
		);
		context.drawItemWithoutEntity(Items.GRASS_BLOCK.getDefaultStack(), itemCenterX, itemCenterY);
		IconRenderer.resetRenderingOptions(context);
	}

	@Override
	public void close() {
		SimpleShulkerPreviewMod.CONFIGS.save();
		this.client.setScreen(parent);
	}
}
