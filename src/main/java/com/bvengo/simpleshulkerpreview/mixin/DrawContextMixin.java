package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.*;
import com.bvengo.simpleshulkerpreview.access.DrawContextAccess;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.positioners.CapacityBarRenderer;
import com.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements DrawContextAccess {
	@Shadow public abstract void drawItemWithoutEntity(ItemStack stack, int x, int y);
	@Shadow public abstract void fill(RenderLayer layer, int x1, int x2, int y1, int y2, int color);

	IconRenderer iconRenderer;


	boolean adjustSize = false;

	@Override
	public void setAdjustSize(boolean newValue) {
		adjustSize = newValue;
	}

	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isItemBarVisible()Z"),
			method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo info) {

		ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();

		if(!Utils.checkStackAllowed(stack)) return;

		ItemStack displayItem = Utils.getDisplayItem(stack, config);
		iconRenderer = new IconRenderer(config, stack, displayItem, x, y);
		iconRenderer.renderOptional((DrawContext)(Object)this);

		// Display itemBar for shulkers (bundles already have a very similar feature)
		if(config.showCapacity && Utils.isShulkerStack(stack)) {
			CapacityBarRenderer capacityBarRenderer = new CapacityBarRenderer(config, stack, x, y);
			capacityBarRenderer.renderOptional((DrawContext)(Object)this);
		}
	}

	@ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
				at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.translate(FFF)V"))
	private void injectedTranslateXYZ(Args args) {
		if(adjustSize) {
			args.set(0, (float)args.get(0) + iconRenderer.xOffset);
			args.set(1, (float)args.get(1) + iconRenderer.yOffset);
			args.set(2, iconRenderer.zOffset);
		}
	}

	@ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
			at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.scale(FFF)V"))
	private void injectedScale(Args args) {
		if(adjustSize) {
			args.set(0, iconRenderer.scale);
			args.set(1, iconRenderer.scale);
			args.set(2, iconRenderer.scale);
		}
	}
}