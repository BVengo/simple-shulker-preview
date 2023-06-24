package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.RegexGroup;
import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.config.IconPositionOptions;
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
			IconPositionOptions iconPositionOptions;
			// TODO: Turn position into a class, chuck all this into another function
			if(Utils.isObject(stack, RegexGroup.MINECRAFT_BUNDLE)) {
				iconPositionOptions = config.iconPositionOptionsBundle;
			}
			else if(Utils.isObject(stack, RegexGroup.MINECRAFT_SHULKER) && stack.getCount() > 1) {
				iconPositionOptions = config.iconPositionOptionsStacked;
			}
			else {
				iconPositionOptions = config.iconPositionOptionsGeneral;
			}

			// Normal icon location
			smallScale = iconPositionOptions.scale;
			smallTranslateX = iconPositionOptions.translateX;
			smallTranslateY = iconPositionOptions.translateY;
			smallTranslateZ = iconPositionOptions.translateZ * 10;

			adjustSize = true;
			this.drawItemWithoutEntity(displayItem, x, y);
			adjustSize = false;
		}

		// Display itemBar for shulkers (bundles already have a very similar feature)
		if(config.showCapacity && Utils.isObject(stack, RegexGroup.MINECRAFT_SHULKER)) {
			float capacity = Utils.getCapacity(stack, config);

			if(capacity > 0.0f && (!config.capacityBarOptions.hideWhenFull || capacity < 1.0f)) {
				int step = (int)(config.capacityBarOptions.length * capacity);
				int shadowHeight = config.capacityBarOptions.displayShadow ? 1 : 0;

				int xBackgroundStart = x + config.capacityBarOptions.translateX;
				int yBackgroundStart = y + config.capacityBarOptions.translateY;
				int xBackgroundEnd;
				int yBackgroundEnd;
				int xCapacityStart;
				int yCapacityStart;
				int xCapacityEnd;
				int yCapacityEnd;

				switch(config.capacityBarOptions.direction) {
					case LEFT_TO_RIGHT -> {
						xBackgroundEnd = xBackgroundStart + config.capacityBarOptions.length;
						yBackgroundEnd = yBackgroundStart + config.capacityBarOptions.width + shadowHeight;
						xCapacityStart = xBackgroundStart;
						yCapacityStart = yBackgroundStart;
						xCapacityEnd = xBackgroundStart + step;
						yCapacityEnd = yCapacityStart + config.capacityBarOptions.width;
					}
					case RIGHT_TO_LEFT -> {
						xBackgroundEnd = xBackgroundStart + config.capacityBarOptions.length;
						yBackgroundEnd = yBackgroundStart + config.capacityBarOptions.width  + shadowHeight;
						xCapacityStart = xBackgroundEnd - step;
						yCapacityStart = yBackgroundStart;
						xCapacityEnd = xBackgroundEnd;
						yCapacityEnd = yCapacityStart + config.capacityBarOptions.width;
					}
					case TOP_TO_BOTTOM -> {
						xBackgroundEnd = xBackgroundStart + config.capacityBarOptions.width;
						yBackgroundEnd = yBackgroundStart + config.capacityBarOptions.length;
						xCapacityStart = xBackgroundStart;
						yCapacityStart = yBackgroundStart;
						xCapacityEnd = xBackgroundEnd;
						yCapacityEnd = yCapacityStart + step;
					}
					case BOTTOM_TO_TOP -> {
						xBackgroundEnd = xBackgroundStart + config.capacityBarOptions.width;
						yBackgroundEnd = yBackgroundStart + config.capacityBarOptions.length;
						xCapacityStart = xBackgroundStart;
						yCapacityStart = yBackgroundEnd - step;
						xCapacityEnd = xBackgroundEnd;
						yCapacityEnd = yBackgroundEnd;
					}
					default -> {
						String err = "Unexpected value for capacity direction: " + config.capacityBarOptions.direction;

						SimpleShulkerPreviewMod.LOGGER.error(err);
						throw new IllegalStateException(err);
					}
				}

				// Display empty bar, then fill in the capacity on top
				this.fill(RenderLayer.getGuiOverlay(), xBackgroundStart, yBackgroundStart, xBackgroundEnd, yBackgroundEnd, -16777216);
				this.fill(RenderLayer.getGuiOverlay(), xCapacityStart, yCapacityStart, xCapacityEnd, yCapacityEnd, Utils.ITEM_BAR_COLOR | -16777216);
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