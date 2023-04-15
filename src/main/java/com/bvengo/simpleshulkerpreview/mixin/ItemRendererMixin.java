package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Shadow public abstract void renderGuiItemIcon(ItemStack stack, int x, int y);

	private float smallScale;
	private float smallTranslateX;
	private float smallTranslateY;
	private float smallTranslateZ;

	boolean adjustSize = false;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"),
			method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo info) {

		ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();
		if (config.disableMod) return;
		if(!config.supportStackedShulkers && stack.getCount() != 1) return;
		if(Utils.isNotAShulker(stack)) return;

		NbtCompound compound = stack.getNbt();
		if(compound == null) return; // Triggers on containers in the creative menu

		ItemStack displayItem = Utils.getDisplayItem(compound, config);
		if(displayItem == null) return; // Triggers if configs don't allow displaying the items, or if it's empty

		if(config.shulkerCount && Utils.isShulkerFull(compound)) {
			smallScale = 8;
			smallTranslateX = 13;
			smallTranslateY = 13;
			smallTranslateZ = config.translateZ * 10;
			adjustSize = true;
			renderGuiItemIcon(stack, x, y);
			adjustSize = false;
			return;
		}

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
		renderGuiItemIcon(displayItem, x, y);
		adjustSize = false;
	}

	@Inject(method = "innerRenderInGui(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
	private void renderItemInsteadOfShulker(LivingEntity entity, ItemStack stack, int x, int y, int seed, int depth, CallbackInfo ci){
		ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();
		if(config.disableMod || !config.shulkerCount) return;
		if(!config.supportStackedShulkers && stack.getCount() != 1) return;
		if(Utils.isNotAShulker(stack)) return;

		NbtCompound compound = stack.getNbt();
		if(compound == null) return; // Triggers on containers in the creative menu

		ItemStack displayItem = Utils.getDisplayItem(compound, config);
		if(displayItem == null) return; // Triggers if configs don't allow displaying the items, or if it's empty

		if (Utils.isShulkerFull(compound)) {
			renderGuiItemIcon(displayItem, x, y);
			ci.cancel();
		}
	}

	@Shadow public void renderItem(ItemStack stack, ModelTransformation.Mode transformationType, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed) {}

	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
	private void renderItemOnFrame(ItemStack stack, ModelTransformation.Mode transformationType, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed, CallbackInfo ci){
		if(transformationType != ModelTransformation.Mode.FIXED) return;
        ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();
		if(config.disableMod || !config.itemFrame) return;
		if(!config.supportStackedShulkers && stack.getCount() != 1) return;
		if(Utils.isNotAShulker(stack)) return;

		NbtCompound compound = stack.getNbt();
		if(compound == null) return; // Triggers on containers in the creative menu

		ItemStack displayItem = Utils.getDisplayItem(compound, config);
		if(displayItem == null) return; // Triggers if configs don't allow displaying the items, or if it's empty

		matrices.translate(0, 0, -0.25);
		float sf = 5/8f;
		matrices.scale(sf, sf, sf);
		renderItem(displayItem, transformationType, light, overlay, matrices, vertexConsumers, seed);
		sf = 8/5f;
		matrices.scale(sf, sf, sf);
		matrices.translate(0, 0, 0.25);
	}

	@Shadow public void renderItem(@Nullable LivingEntity entity, ItemStack item, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed) {}

	@Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V", at = @At("TAIL"))
	private void renderItemAboveHeld(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci){
		ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();
		if(config.disableMod || !config.entityHeld) return;
		if(!config.supportStackedShulkers && stack.getCount() != 1) return;
		if(Utils.isNotAShulker(stack)) return;

		NbtCompound compound = stack.getNbt();
		if(compound == null) return; // Triggers on containers in the creative menu

		ItemStack displayItem = Utils.getDisplayItem(compound, config);
		if(displayItem == null) return; // Triggers if configs don't allow displaying the items, or if it's empty

		float[] offset = {0,0,0};
		float sf = 5/8f;

		switch (renderMode){
			case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
				if (!config.selfHeld) return;
				offset[1] = 0.125f;
				sf = 0.5f;
			}
			case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
				offset[2] = 0.1875f;
				offset[1] = 0.1f;
			}
			default -> {
				return;
			}
		}

		matrices.translate(offset[0], offset[1], offset[2]);
		matrices.scale(sf, sf, sf);
		renderItem(entity, displayItem, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed);
		sf = 1/sf;
		matrices.scale(sf, sf, sf);
		matrices.translate(-offset[0], -offset[1], -offset[2]);
	}

	@ModifyArg(method = "renderGuiItemModel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0),
		index = 2)
	private float injectedTranslateZ(float z) {
		return adjustSize ? z + smallTranslateZ : z;
	}

	@ModifyArgs(method = "renderGuiItemModel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 1))
	private void injectedTranslateXY(Args args) {
		if(adjustSize) {
			args.set(0, smallTranslateX);
			args.set(1, smallTranslateY);
		}
	}

	@ModifyArgs(method = "renderGuiItemModel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V", ordinal = 1))
	private void injectedScale(Args args) {
		if(adjustSize) {
			args.set(0, smallScale);
			args.set(1, smallScale);
			args.set(2, smallScale);
		}
	}
}

