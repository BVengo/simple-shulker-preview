package com.bvengo.simpleshulkerpreview.gui;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.enums.TabOptions;
import com.bvengo.simpleshulkerpreview.gui.panels.*;
import com.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class OptionsScreen extends Screen {
	protected final Screen parent;
	protected TextFieldWidget tabSelect;  // Temporary until dropdown is implemented

	Panel selectedPanel;
	TabOptions selectedTab = TabOptions.GENERAL;

	int titleWidth = 0;

	protected final int SCALE_MULTIPLIER = 8;

	float itemSize = IconRenderer.DEFAULT_SCALE * SCALE_MULTIPLIER;
	int slotSize = (int)(itemSize + SCALE_MULTIPLIER * 2);

	HashMap<TabOptions, Panel> panels = new HashMap<>();

	public OptionsScreen(@Nullable Screen parent) {
		super(Text.translatable("text.autoconfig.simpleshulkerpreview.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		titleWidth = this.textRenderer.getWidth(this.title);

		tabSelect = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 8, 200, 20, Text.of("Select page..."));
		tabSelect.setChangedListener(search -> {
			if(search.isEmpty()) return;
			try {
				TabOptions tab = TabOptions.valueOf(search.toUpperCase());
				selectTab(tab);
			} catch (IllegalArgumentException ignored) {}
		}); // TODO: Implement dropdown. Set page to selected value
		addDrawableChild(tabSelect);

		int panelY = tabSelect.getBottom() + 16;
		int panelWidth = this.width - slotSize - Panel.X_PADDING * 3;

		int panelHeight = height - panelY - 16;

		panels = new HashMap<>();
		panels.put(TabOptions.GENERAL, new GeneralPanel(Panel.X_PADDING, panelY, panelWidth, panelHeight, client));
		panels.put(TabOptions.SHULKER, new ShulkerPanel(Panel.X_PADDING, panelY, panelWidth, panelHeight, client));
		panels.put(TabOptions.BUNDLE, new BundlePanel(Panel.X_PADDING, panelY, panelWidth, panelHeight, client));
		panels.put(TabOptions.STACKED, new StackedPanel(Panel.X_PADDING, panelY, panelWidth, panelHeight, client));
		panels.put(TabOptions.COMPATIBILITY, new CompatibilityPanel(Panel.X_PADDING, panelY, panelWidth, panelHeight, client));

		panels.forEach((tab, panel) -> {
			panel.init();
		});

		this.selectedPanel = panels.get(selectedTab);
		this.addDrawableChild(selectedPanel);
	}

	private void selectTab(TabOptions tab) {
		/**
		 * Selects the tab to display
		 */
		if(tab == selectedTab) return;  // No point in re-selecting the same tab

		this.remove(selectedPanel);
		selectedTab = tab;
		selectedPanel = panels.get(tab);
		this.addDrawableChild(selectedPanel);
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.tabSelect);
	}

	private void renderHeader(DrawContext context, int mouseX, int mouseY, float delta) {
		/**
		 * Renders the title and tab selector field
		 */
		final int titleWidth = this.textRenderer.getWidth(this.title);

		int tabSelectWidth = this.width - titleWidth - Panel.X_PADDING * 3;  // padding on each side and between

		this.tabSelect.setWidth(tabSelectWidth);
		this.tabSelect.setX(titleWidth + Panel.X_PADDING * 2); // padding on left of title and between

		this.tabSelect.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, Panel.X_PADDING + titleWidth / 2, 14, 16777215);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		renderHeader(context, mouseX, mouseY, delta);
		context.drawHorizontalLine(Panel.X_PADDING, this.width - Panel.X_PADDING, tabSelect.getBottom() + 8, Colors.LIGHT_GRAY);

		panels.get(selectedTab).render(context, mouseX, mouseY, delta);

		if(selectedTab == TabOptions.BUNDLE) {
			renderItem(
					context,
					Items.BUNDLE.getDefaultStack(),
					SimpleShulkerPreviewMod.CONFIGS.bundleTranslateX,
					SimpleShulkerPreviewMod.CONFIGS.bundleTranslateY,
					SimpleShulkerPreviewMod.CONFIGS.bundleScale
			);
		} else if (selectedTab == TabOptions.STACKED) {
			ItemStack shulkerStack = Items.SHULKER_BOX.getDefaultStack();
			shulkerStack.setCount(64);

			renderItem(
					context,
					shulkerStack,
					SimpleShulkerPreviewMod.CONFIGS.stackedTranslateX,
					SimpleShulkerPreviewMod.CONFIGS.stackedTranslateY,
					SimpleShulkerPreviewMod.CONFIGS.stackedScale
			);
		} else {
			renderItem(
					context,
					Items.SHULKER_BOX.getDefaultStack(),
					SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX,
					SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY,
					SimpleShulkerPreviewMod.CONFIGS.shulkerScale
			);
		}
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

		float itemSize = IconRenderer.DEFAULT_SCALE * SCALE_MULTIPLIER;

		int slotSize = (int)(itemSize + SCALE_MULTIPLIER * 2);
		int slotX = this.width - slotSize - Panel.X_PADDING;  // left x
		int slotY = 36 + (int)((this.height - 36 - slotSize) / 2.0f);

		int itemCenterX = (int)(slotX + slotSize / 2.0f);
		int itemCenterY = (int)(slotY + slotSize / 2.0f);

		context.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, Colors.GRAY);
		context.drawGuiTexture(RenderLayer::getGuiTextured, SLOT_FRAME_TEXTURE, slotX, slotY, slotSize, slotSize);

		// Render the item stack
		// Offsets required due to the way the item is rendered
		IconRenderer.setRenderingOptions(context, itemSize, -(int)SCALE_MULTIPLIER, -(int)SCALE_MULTIPLIER, 0.0f);
		context.drawItem(stack, itemCenterX, itemCenterY);

		if (stack.getCount() != 1) {
			String string = String.valueOf(stack.getCount());
			int textX = slotX + slotSize - (int)(textRenderer.getWidth(string) * SCALE_MULTIPLIER);
			int textY = itemCenterY + 9;

			renderText(context, string, textX, textY, (int)(itemSize * 2), SCALE_MULTIPLIER);
		}

		// Render a small grass block icon
		IconRenderer.setRenderingOptions(context,
				overlayScale * SCALE_MULTIPLIER,
				overlayX * SCALE_MULTIPLIER,
				overlayY * SCALE_MULTIPLIER,
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
