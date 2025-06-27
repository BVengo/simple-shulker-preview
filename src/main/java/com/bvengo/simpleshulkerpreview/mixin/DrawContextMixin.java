package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.access.DrawContextAccess;
import com.bvengo.simpleshulkerpreview.container.ContainerManager;
import com.bvengo.simpleshulkerpreview.container.ContainerType;
import com.bvengo.simpleshulkerpreview.positioners.CapacityBarRenderer;
import com.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;

import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements DrawContextAccess {
	@Unique IconRenderer iconRenderer;
	@Unique boolean adjustSize = false;

	@Override
	public void simple_shulker_preview$setAdjustSize(boolean newValue) {
		adjustSize = newValue;
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemBar(Lnet/minecraft/item/ItemStack;II)V"),
			method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo info) {
		if(SimpleShulkerPreviewMod.CONFIGS.disableMod) {
			return;
		}

		ContainerManager containerParser = new ContainerManager(stack);

		ItemStack displayStack = containerParser.getDisplayStack();

		if(displayStack == null) return;

		iconRenderer = new IconRenderer(containerParser, displayStack, x, y);
		iconRenderer.renderOptional((DrawContext)(Object)this);

		// Display itemBar for containers. Ignore bundles - they already have this feature
		boolean isBundle = containerParser.getContainerType().equals(ContainerType.BUNDLE);
		if(SimpleShulkerPreviewMod.CONFIGS.showCapacity && !isBundle) {
			CapacityBarRenderer capacityBarRenderer = new CapacityBarRenderer(containerParser, stack, x, y);
			capacityBarRenderer.renderOptional((DrawContext)(Object)this);
		}
	}

	/**
	 * Redirects the item rendering to apply scaling and centering based on the icon renderer's settings.
	 * <p>
	 * Beyond the {@link DrawContext} class, the following classes/methods are relevant for the rendering process:
	 * <ul>
	 *   <li>{@link ItemGuiElementRenderState} - Represents the state of the item being rendered, including its position and transformation matrix.</li>
	 *   <li>{@link net.minecraft.client.gui.render.GuiRenderer#prepareItem(ItemGuiElementRenderState, float, float, int, int)} - Where the quad render state is prepared for rendering the item.</li>
	 * </ul>
	 */
	@Redirect(
			method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V",
			at = @At(
					value = "NEW",
					target = "(Ljava/lang/String;Lorg/joml/Matrix3x2f;Lnet/minecraft/client/render/item/ItemRenderState;IILnet/minecraft/client/gui/ScreenRect;)Lnet/minecraft/client/gui/render/state/ItemGuiElementRenderState;"
			)
	)
	private ItemGuiElementRenderState wrapDrawItem(String itemName, Matrix3x2f originalMatrix, ItemRenderState state, int x, int y, ScreenRect scissor) {
		if (!adjustSize) {
			return new ItemGuiElementRenderState(itemName, originalMatrix, state, x, y, scissor);
		}

		// Apply scaling
		float scale = iconRenderer.scale / 16.0f;
		Matrix3x2f newMatrix = new Matrix3x2f(originalMatrix);
		newMatrix.scale(scale, scale);

		// Required to center the icon correctly, due to how the item gets centered by the renderer
		float shift = 8 * (1 - scale);  // 0 at scale 1.0, 8 at scale 0.0

		int newScreenX = (int) ((x + iconRenderer.xOffset + shift) / scale);
		int newScreenY = (int) ((y + iconRenderer.yOffset + shift) / scale);

		return new ItemGuiElementRenderState(itemName, newMatrix, state, newScreenX, newScreenY, scissor);
	}
}