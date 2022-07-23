package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.Utils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Shadow @Final private TextureManager textureManager;
	@Shadow public float zOffset;

	@Shadow public abstract void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model);
	@Shadow public abstract void renderGuiItemIcon(ItemStack stack, int x, int y);
	@Shadow public abstract BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed);

	private float smallScale = 10F;
	private double smallTranslateX = 12.0;
	private double smallTranslateY = 12.0;

	boolean isSmall = false;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"),
			method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo info) {

		if (stack.getCount() != 1) {
			return;
		}

		NbtCompound compound = stack.getNbt();
		if(!Utils.isShulkerBox(compound)) {
			return;
		}

		ItemStack displayItem = Utils.getDisplayItem(compound.getCompound("BlockEntityTag"), 27, false);
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

