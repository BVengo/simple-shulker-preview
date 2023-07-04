package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.RegexGroup;
import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.config.PositionOptions;
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
public abstract class DrawContextMixin {
	@Shadow public abstract void drawItemWithoutEntity(ItemStack stack, int x, int y);
	@Shadow public abstract void fill(RenderLayer layer, int x1, int x2, int y1, int y2, int color);

	private int smallScale = 10;
	private int smallTranslateX = 12;
	private int smallTranslateY = 12;
	private int smallTranslateZ = 10;

	boolean adjustSize = false;

	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isItemBarVisible()Z"),
			method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo info) {

		ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();

		if(!Utils.checkStackAllowed(stack)) return;

		ItemStack displayItem = Utils.getDisplayItem(stack, config);
		if(displayItem != null) {
			PositionOptions positionOptions;
			// TODO: Turn position into a class, chuck all this into another function
			if(Utils.isObject(stack, RegexGroup.MINECRAFT_BUNDLE)) {
				positionOptions = config.positionOptionsBundle;
			}
			else if(Utils.isShulkerStack(stack) && stack.getCount() > 1) {
				positionOptions = config.positionOptionsStacked;
			}
			else {
				positionOptions = config.positionOptionsGeneral;
			}

			// Normal icon location
			smallScale = positionOptions.scale;
			smallTranslateX = positionOptions.translateX;
			smallTranslateY = positionOptions.translateY;
			smallTranslateZ = positionOptions.translateZ * 10;

			adjustSize = true;
			this.drawItemWithoutEntity(displayItem, x, y);
			adjustSize = false;
		}

		// Display itemBar for shulkers (bundles already have a very similar feature)
		if(config.showFullness && Utils.isShulkerStack(stack)) {
			float fullness = Utils.getFullness(stack, config);

			if(fullness > 0.0f) {
				int step = (int)(13f * fullness);
				int xPos = x + 2;
				int yPos = y + 13;

				// Display empty bar, then fill in the capacity on top
				this.fill(RenderLayer.getGuiOverlay(), xPos, yPos, xPos + 13, yPos + 2, -16777216);
				this.fill(RenderLayer.getGuiOverlay(), xPos, yPos, xPos + step, yPos + 1, Utils.ITEM_BAR_COLOR | -16777216);
			}
		}
	}

	@ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
				at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.translate(FFF)V"))
	private void injectedTranslateXYZ(Args args) {
		if(adjustSize) {
			args.set(0, (float)args.get(0) + (float)smallTranslateX - 8.0f);
			args.set(1, (float)args.get(1) + (float)smallTranslateY - 8.0f);
			args.set(2, 100.0f + (float)smallTranslateZ);
		}
	}

	@ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
			at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.scale(FFF)V"))
	private void injectedScale(Args args) {
		if(adjustSize) {
			args.set(0, (float)smallScale);
			args.set(1, (float)smallScale);
			args.set(2, (float)smallScale);
		}
	}
}