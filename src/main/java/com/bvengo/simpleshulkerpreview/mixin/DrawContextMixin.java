package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.access.DrawContextAccess;
import com.bvengo.simpleshulkerpreview.container.ContainerManager;
import com.bvengo.simpleshulkerpreview.container.ContainerType;
import com.bvengo.simpleshulkerpreview.positioners.CapacityBarRenderer;
import com.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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

	@ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
				at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.translate(FFF)V"))
	private void injectedTranslateXYZ(Args args) {
		if(SimpleShulkerPreviewMod.CONFIGS.disableMod) {
			return;
		}

		if(adjustSize) {
			args.set(0, (float)args.get(0) + iconRenderer.xOffset);
			args.set(1, (float)args.get(1) + iconRenderer.yOffset);
			args.set(2, iconRenderer.zOffset);
		}
	}

	@ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
			at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.scale(FFF)V"))
	private void injectedScale(Args args) {
		if(SimpleShulkerPreviewMod.CONFIGS.disableMod) {
			return;
		}

		if(adjustSize) {
			args.set(0, iconRenderer.scale);
			args.set(1, -iconRenderer.scale);
			args.set(2, iconRenderer.scale);
		}
	}
}