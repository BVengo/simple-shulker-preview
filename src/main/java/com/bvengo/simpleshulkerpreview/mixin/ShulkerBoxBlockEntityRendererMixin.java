package com.bvengo.simpleshulkerpreview.mixin;

// import static com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod.LOGGER;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionTypes;
import red.jackf.chesttracker_adapted.memory.Memory;
import red.jackf.chesttracker_adapted.memory.MemoryDatabase;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public class ShulkerBoxBlockEntityRendererMixin {
  private static ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).get();

  @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/client/render/block/entity/ShulkerBoxBlockEntityRenderer;render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V")
  private void renderGhostItemFrame(ShulkerBoxBlockEntity shulker, float f, MatrixStack stack,
      VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
    if (config.disableMod) {
      return;
    }

    MinecraftClient mc = MinecraftClient.getInstance();
    Identifier currentWorld;

    // Get the memory database.
    MemoryDatabase db = MemoryDatabase.getCurrent();
    if (mc.world != null) {
      currentWorld = mc.world.getRegistryKey().getValue();
    } else {
      currentWorld = DimensionTypes.OVERWORLD_ID;
    }
    Collection<Memory> memories = db.getAllMemories(currentWorld);

    // See if the position of the Memory equals the position of the shulker.
    memories.forEach(m -> {
      if (m.getPosition().equals(shulker.getPos())) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer renderer = client.getItemRenderer();
        ItemStack itemToRender = Utils.getDisplayItem(m.getItems(), config);
        // Required (I think)
        stack.push();
        stack.translate(0.5f, 0.5f, 0.0f);
        renderer.renderItem(itemToRender, ModelTransformationMode.FIXED, i, j, stack,
            vertexConsumerProvider, client.world, 0);
        stack.pop();
        stack.push();
        stack.translate(0.0f, 0.5f, 0.5f);

        renderer.renderItem(itemToRender, ModelTransformationMode.FIXED, i, j, stack,
            vertexConsumerProvider, client.world, 0);
        stack.pop();
        stack.push();
        stack.translate(0.5f, 0.5f, 1.0f);
        renderer.renderItem(itemToRender, ModelTransformationMode.FIXED, i, j, stack,
            vertexConsumerProvider, client.world, 0);
        stack.pop();
        stack.push();
        stack.translate(1.0f, 0.5f, 0.5f);
        renderer.renderItem(itemToRender, ModelTransformationMode.FIXED, i, j, stack,
            vertexConsumerProvider, client.world, 0);
        stack.pop();
        stack.push();
        stack.translate(0.5f, 0.0f, 0.5f);
        renderer.renderItem(itemToRender, ModelTransformationMode.FIXED, i, j, stack,
            vertexConsumerProvider, client.world, 0);
        stack.pop();
        stack.push();
        stack.translate(0.5f, 1.0f, 0.5f);
        renderer.renderItem(itemToRender, ModelTransformationMode.FIXED, i, j, stack,
            vertexConsumerProvider, client.world, 0);
        stack.pop();
      }
    });

  }
}
