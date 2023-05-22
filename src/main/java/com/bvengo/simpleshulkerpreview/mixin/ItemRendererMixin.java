package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.RegexGroup;
import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
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
		if (config.disableMod) return;
		if(!config.supportStackedShulkers && stack.getCount() != 1) return;
		if(!Utils.isObject(stack, RegexGroup.MINECRAFT_SHULKER)) return;

		NbtCompound compound = stack.getNbt();
		if(compound == null) return; // Triggers on containers in the creative menu

		ItemStack displayItem = Utils.getDisplayItem(compound, config);
		if(displayItem == null) return; // Triggers if configs don't allow displaying the items, or if it's empty

		if(stack.getCount() == 1) {
			// Normal icon location
			smallScale = config.scale;
			smallTranslateX = config.translateX;
			smallTranslateY = config.translateY;
			smallTranslateZ = config.translateZ * 10;
		} else {
			// Stackable shulkers are enabled, so change icon location to avoid item counter
			smallScale = config.stackedScale;
			smallTranslateX = config.stackedTranslateX;
			smallTranslateY = config.stackedTranslateY;
			smallTranslateZ = config.stackedTranslateZ * 10;
		}
		
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

