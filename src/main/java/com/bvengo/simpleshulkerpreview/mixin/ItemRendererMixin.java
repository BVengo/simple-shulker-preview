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
	@Shadow public abstract void renderGuiItemIcon(ItemStack stack, int x, int y);

	private int smallScale = 10;
	private int smallTranslateX = 12;
	private int smallTranslateY = 12;
	private int smallTranslateZ = 10;

	boolean adjustSize = false;

	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isItemBarVisible()Z"),
			method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo info) {

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
		renderGuiItemIcon(displayItem, x, y);
		adjustSize = false;
	}

	@ModifyArg(method = "renderGuiItemModel(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
		at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.translate(DDD)V", ordinal = 0),
		index = 2)
	private double injectedTranslateZ(double z) {
		return adjustSize ? (z + smallTranslateZ) : z;
	}

	@ModifyArgs(method = "renderGuiItemModel(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
		at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.translate(DDD)V", ordinal = 1))
	private void injectedTranslateXY(Args args) {
		if(adjustSize) {
			args.set(0, (double)smallTranslateX);
			args.set(1, (double)smallTranslateY);
		}
	}

	@ModifyArgs(method = "renderGuiItemModel(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
		at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.scale(FFF)V", ordinal=1))
	private void injectedScale(Args args) {
		if(adjustSize) {
			args.set(0, (float)smallScale);
			args.set(1, (float)smallScale);
			args.set(2, (float)smallScale);
		}
	}
}