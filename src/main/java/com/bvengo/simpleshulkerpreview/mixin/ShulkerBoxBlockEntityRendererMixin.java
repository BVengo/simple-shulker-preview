package com.bvengo.simpleshulkerpreview.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public class ShulkerBoxBlockEntityRendererMixin {
  @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/client/render/block/entity/ShulkerBoxBlockEntityRenderer;render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V")
  private void renderGhostItemFrame(ShulkerBoxBlockEntity shulker, float f, MatrixStack stack,
      VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
    // Render item frame.
    MinecraftClient client = MinecraftClient.getInstance();
    EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
    BlockPos pos = shulker.getPos();
    ItemFrameEntity frame = new ItemFrameEntity(shulker.getWorld(), pos.withY(pos.getY() + 1), Direction.UP);
    dispatcher.render(frame, frame.getX(), frame.getY(), frame.getZ(), frame.getYaw(), 0.0f, stack,
        vertexConsumerProvider, i);
  }
}
