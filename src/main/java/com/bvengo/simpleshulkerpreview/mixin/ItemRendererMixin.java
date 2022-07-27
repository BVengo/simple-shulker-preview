package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
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

	private float smallScale = 10F;
	private double smallTranslateX = 12.0;
	private double smallTranslateY = 12.0;

	boolean isSmall = false;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"),
			method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo info) {

		ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();

		if (config.disableMod || stack.getCount() != 1) {
			return;
		}

		NbtCompound compound = stack.getNbt();
		if(!Utils.isShulkerBox(compound)) {
			return;
		}

		ItemStack displayItem = Utils.getDisplayItem(compound.getCompound("BlockEntityTag"), 27, config.displayItem, config.displayUnique);
		if(displayItem == null) {
			return;
		}

		isSmall = true;
		renderGuiItemIcon(displayItem, x, y);
		isSmall = false;
	}

	@ModifyArg(method = "renderGuiItemModel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V", ordinal = 0),
		index = 2)
	private double injectedTranslateZ(double z) {
		return isSmall ? (z + 100.0) : z;
	}

	@ModifyArgs(method = "renderGuiItemModel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V", ordinal = 1))
	private void injectedTranslateXY(Args args) {
		if(isSmall) {
			args.set(0, smallTranslateX);
			args.set(1, smallTranslateY);
		}
	}

	@ModifyArgs(method = "renderGuiItemModel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V", ordinal = 1))
	private void injectedScale(Args args) {
		if(isSmall) {
			args.set(0, smallScale);
			args.set(1, smallScale);
			args.set(2, smallScale);
		}
	}
}

