package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.RegexGroup;
import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.config.PositionOptions;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Shadow public abstract void renderGuiItemIcon(MatrixStack matrices, ItemStack stack, int x, int y);

	private float smallScale = 10f;
	private float smallTranslateX = 12f;
	private float smallTranslateY = 12f;
	private float smallTranslateZ = 10f;

	boolean adjustSize = false;

	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isItemBarVisible()Z"),
			method = "renderGuiItemOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(MatrixStack matrices, TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo info) {

		ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();

		if(!Utils.checkStackAllowed(stack)) return;

		ItemStack displayItem = Utils.getDisplayItem(stack, config);
		if(displayItem == null) return; // Triggers if configs don't allow displaying the items, or if it's empty

		PositionOptions positionOptions;
		// TODO: Turn position into a class, chuck all this into another function
		if(Utils.isObject(stack, RegexGroup.MINECRAFT_BUNDLE)) {
			positionOptions = config.positionOptionsBundle;
		}
		else if(Utils.isObject(stack, RegexGroup.MINECRAFT_SHULKER) && stack.getCount() > 1) {
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
		renderGuiItemIcon(matrices, displayItem, x, y);
		adjustSize = false;
	}

	@ModifyArg(method = "renderGuiItemModel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
		at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.translate(FFF)V", ordinal = 0),
		index = 2)
	private float injectedTranslateZ(float z) {
		return adjustSize ? (z + smallTranslateZ) : z;
	}

	@ModifyArgs(method = "renderGuiItemModel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
		at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.translate(FFF)V", ordinal = 1))
	private void injectedTranslateXY(Args args) {
		if(adjustSize) {
			args.set(0, smallTranslateX);
			args.set(1, smallTranslateY);
		}
	}

	@ModifyArgs(method = "renderGuiItemModel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
		at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.scale(FFF)V"))
	private void injectedScale(Args args) {
		if(adjustSize) {
			args.set(0, smallScale);
			args.set(1, smallScale);
			args.set(2, smallScale);
		}
	}
}