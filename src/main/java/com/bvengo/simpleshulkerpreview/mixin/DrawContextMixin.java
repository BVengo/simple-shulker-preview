package com.bvengo.simpleshulkerpreview.mixin;

import com.bvengo.simpleshulkerpreview.RegexGroup;
import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow public abstract void drawItemWithoutEntity(ItemStack stack, int x, int y);

    private float smallScale = 10f;
    private float smallTranslateX = 12f;
    private float smallTranslateY = 12f;
    private float smallTranslateZ = 10f;

    boolean adjustSize = false;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"),
            method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    private void renderShulkerItemOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {

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
        drawItemWithoutEntity(displayItem, x, y);
        adjustSize = false;
    }

    @ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
            at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.translate(FFF)V"))
    private void injectedTranslateXYZ(Args args) {
        if(adjustSize) {
            args.set(0, (float)args.get(0) - 8.0F + smallTranslateX);
            args.set(1, (float)args.get(1) - 8.0F + smallTranslateY);
            args.set(2, 100.0F + smallTranslateZ);
        }
    }

    @ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
            at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.scale(FFF)V"))
    private void injectedScale(Args args) {
        if(adjustSize) {
            args.set(0, smallScale);
            args.set(1, smallScale);
            args.set(2, smallScale);
        }
    }
}
