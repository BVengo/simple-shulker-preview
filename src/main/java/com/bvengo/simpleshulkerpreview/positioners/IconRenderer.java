package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.access.DrawContextAccess;
import com.bvengo.simpleshulkerpreview.config.IconPositionOptions;
import com.bvengo.simpleshulkerpreview.container.ContainerManager;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class IconRenderer extends OverlayRenderer {

    IconPositionOptions iconPositionOptions;

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
                iconPositionOptions = (
                    containerParser.getStackSize() > 1 ?
                        SimpleShulkerPreviewMod.CONFIGS.iconPositionOptionsStacked :
                        SimpleShulkerPreviewMod.CONFIGS.iconPositionOptionsGeneral
                );
                break;
            case BUNDLE:
                iconPositionOptions = SimpleShulkerPreviewMod.CONFIGS.iconPositionOptionsBundle;
                break;
            case OTHER:
                iconPositionOptions = SimpleShulkerPreviewMod.CONFIGS.iconPositionOptionsGeneral;
                break;
            case NONE:
                break;
        }
    }

    protected void calculatePositions() {
        // Normal icon location
        xOffset = (float)iconPositionOptions.translateX - 8.0f;
        yOffset = (float)iconPositionOptions.translateY - 8.0f;

        scale = iconPositionOptions.scale;
    }

    protected void render(DrawContext context) {
        ((DrawContextAccess) context).simple_shulker_preview$setAdjustSize(true);
        context.drawItemWithoutEntity(stack, stackX, stackY);
        ((DrawContextAccess) context).simple_shulker_preview$setAdjustSize(false);
    }

    public void renderOptional(DrawContext context) {
        if(canDisplay()) {
            calculatePositions();
            render(context);
        }
    }

    @Override
    protected boolean canDisplay() {
        return stack != null && stack.getItem() != null;
    }
}
