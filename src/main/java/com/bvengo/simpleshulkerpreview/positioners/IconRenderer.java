package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.access.DrawContextAccess;
import com.bvengo.simpleshulkerpreview.container.ContainerManager;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class IconRenderer extends OverlayRenderer {

    public final static int DEFAULT_SCALE = 16;
    public final static int DEFAULT_X_OFFSET = 0;
    public final static int DEFAULT_Y_OFFSET = 0;
    public final static int DEFAULT_Z_OFFSET = DEFAULT_SCALE; // Scale also tells us depth of the item

    public float scale;
    public float xOffset;
    public float yOffset;

    public IconRenderer(ContainerManager containerParser, ItemStack displayStack, int x, int y) {
        super(displayStack, x, y);
        setPositionOptions(containerParser);
    }

    private void setPositionOptions(ContainerManager containerParser) {
        switch(containerParser.getContainerType()) {
            case SHULKER_BOX:
                if(containerParser.getStackSize() > 1) {
                    xOffset = SimpleShulkerPreviewMod.CONFIGS.stackedTranslateX;
                    yOffset = SimpleShulkerPreviewMod.CONFIGS.stackedTranslateY;
                    scale = SimpleShulkerPreviewMod.CONFIGS.stackedScale;
                } else {
                    xOffset = SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX;
                    yOffset = SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY;
                    scale = SimpleShulkerPreviewMod.CONFIGS.shulkerScale;
                }
                break;
            case BUNDLE:
                xOffset = SimpleShulkerPreviewMod.CONFIGS.bundleTranslateX;
                yOffset = SimpleShulkerPreviewMod.CONFIGS.bundleTranslateY;
                scale = SimpleShulkerPreviewMod.CONFIGS.bundleScale;
                break;
            case OTHER:
                // Use the shulker box settings for other containers
                xOffset = SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateX;
                yOffset = SimpleShulkerPreviewMod.CONFIGS.shulkerTranslateY;
                scale = SimpleShulkerPreviewMod.CONFIGS.shulkerScale;
                break;
            case NONE:
                break;
        }
    }

    protected void render(DrawContext context) {
        float zOffset = scale + 100.0f;  // Includes depth of the inventory slot
        setRenderingOptions(context, scale, xOffset, yOffset, zOffset);
        context.drawItemWithoutEntity(stack, stackX, stackY);
        resetRenderingOptions(context);
    }

    @Override
    protected boolean canDisplay() {
        return stack != null && stack.getItem() != null;
    }

    public static void setRenderingOptions(DrawContext context, float scale, float xOffset, float yOffset, float zOffset) {
        ((DrawContextAccess)context).simple_shulker_preview$setAdjustedScale(scale);
        ((DrawContextAccess)context).simple_shulker_preview$setAdjustedX(xOffset);
        ((DrawContextAccess)context).simple_shulker_preview$setAdjustedY(yOffset);
        ((DrawContextAccess)context).simple_shulker_preview$setAdjustedZ(zOffset);
    }

    public static void resetRenderingOptions(DrawContext context) {
        setRenderingOptions(context, DEFAULT_SCALE, DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET, DEFAULT_SCALE);
    }
}
