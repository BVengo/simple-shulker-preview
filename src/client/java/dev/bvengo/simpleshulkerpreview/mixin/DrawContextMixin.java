package dev.bvengo.simpleshulkerpreview.mixin;

import dev.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import dev.bvengo.simpleshulkerpreview.container.ContainerManager;
import dev.bvengo.simpleshulkerpreview.container.ContainerType;
import dev.bvengo.simpleshulkerpreview.positioners.CapacityBarRenderer;
import dev.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public abstract class DrawContextMixin {

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemBar(Lnet/minecraft/world/item/ItemStack;II)V"),
			method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V")
	private void renderShulkerItemOverlay(Font textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo info) {
		if(SimpleShulkerPreviewMod.CONFIGS.disableMod) {
			return;
		}

		ContainerManager containerParser = new ContainerManager(stack);

		ItemStack displayStack = containerParser.getDisplayStack();

		if(displayStack == null && (!containerParser.isSupported() || SimpleShulkerPreviewMod.CONFIGS.hideWhenNoIcon)) {
			return;
		}

		if (displayStack != null) {
			IconRenderer iconRenderer = new IconRenderer(containerParser, displayStack, x, y);
			iconRenderer.renderOptional((GuiGraphicsExtractor)(Object)this);
		}

		// Display itemBar for containers. Ignore bundles - they already have this feature
		boolean isBundle = containerParser.getContainerType().equals(ContainerType.BUNDLE);
		if(SimpleShulkerPreviewMod.CONFIGS.showCapacity && !isBundle) {
			CapacityBarRenderer capacityBarRenderer = new CapacityBarRenderer(containerParser, stack, x, y);
			capacityBarRenderer.renderOptional((GuiGraphicsExtractor)(Object)this);
		}
	}

}
