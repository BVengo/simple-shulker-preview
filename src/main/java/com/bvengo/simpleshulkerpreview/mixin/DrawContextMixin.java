package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.access.DrawContextAccess;
import com.bvengo.simpleshulkerpreview.container.ContainerManager;
import com.bvengo.simpleshulkerpreview.container.ContainerType;
import com.bvengo.simpleshulkerpreview.positioners.CapacityBarRenderer;
import com.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiItemAtlas;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public abstract class DrawContextMixin implements DrawContextAccess {
	@Unique IconRenderer iconRenderer;
	@Unique boolean adjustSize = false;

	@Override
	public void simple_shulker_preview$setAdjustSize(boolean newValue) {
		adjustSize = newValue;
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemBar(Lnet/minecraft/world/item/ItemStack;II)V"),
			method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(Font textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo info) {
		if(SimpleShulkerPreviewMod.CONFIGS.disableMod) {
			return;
		}

		ContainerManager containerParser = new ContainerManager(stack);

		ItemStack displayStack = containerParser.getDisplayStack();

		if(displayStack == null) return;

		iconRenderer = new IconRenderer(containerParser, displayStack, x, y);
		iconRenderer.renderOptional((GuiGraphicsExtractor)(Object)this);

		// Display itemBar for containers. Ignore bundles - they already have this feature
		boolean isBundle = containerParser.getContainerType().equals(ContainerType.BUNDLE);
		if(SimpleShulkerPreviewMod.CONFIGS.showCapacity && !isBundle) {
			CapacityBarRenderer capacityBarRenderer = new CapacityBarRenderer(containerParser, stack, x, y);
			capacityBarRenderer.renderOptional((GuiGraphicsExtractor)(Object)this);
		}
	}

	/**
	 * Warps the item rendering to apply scaling and centering based on the icon renderer's settings.
	 * <p>
	 * Beyond the {@link GuiGraphicsExtractor} class, the following classes/methods are relevant for the rendering process:
	 * <ul>
	 *   <li>{@link GuiItemRenderState} - Represents the state of the item being rendered, including its position and transformation matrix.</li>
	 *   <li>{@link net.minecraft.client.gui.render.GuiRenderer#submitBlitFromItemAtlas(GuiItemRenderState, GuiItemAtlas.SlotView)} - Where the quad render state is prepared for rendering the item.</li>
	 * </ul>
	 */
	@WrapOperation(
			method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
			at = @At(
					value = "NEW",
					target = "(Lorg/joml/Matrix3x2f;Lnet/minecraft/client/renderer/item/TrackingItemStackRenderState;IILnet/minecraft/client/gui/navigation/ScreenRectangle;)Lnet/minecraft/client/renderer/state/gui/GuiItemRenderState;"
			)
	)
	private GuiItemRenderState wrapDrawItem(Matrix3x2f originalMatrix, TrackingItemStackRenderState state, int x, int y, ScreenRectangle scissor, Operation<GuiItemRenderState> original) {
		if (!adjustSize) {
			return original.call(originalMatrix, state, x, y, scissor);
		}

		// Apply scaling
		float scale = iconRenderer.scale / 16.0f;
		Matrix3x2f newMatrix = new Matrix3x2f(originalMatrix);
		newMatrix.scale(scale, scale);

		// Required to center the icon correctly, due to how the item gets centered by the renderer
		float shift = 8 * (1 - scale);  // 0 at scale 1.0, 8 at scale 0.0

		int newScreenX = (int) ((x + iconRenderer.xOffset + shift) / scale);
		int newScreenY = (int) ((y + iconRenderer.yOffset + shift) / scale);

		return original.call(newMatrix, state, newScreenX, newScreenY, scissor);
	}
}