package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.access.DrawContextAccess;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.config.IconPositionOptions;
import com.bvengo.simpleshulkerpreview.container.ContainerManager;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class IconRenderer extends OverlayRenderer {

    IconPositionOptions iconPositionOptions;

    public float scale;
    public float xOffset;
    public float yOffset;
    public float zOffset;

    public IconRenderer(ConfigOptions config, ContainerManager containerParser, ItemStack displayStack, int x, int y) {
        super(config, displayStack, x, y);
        setPositionOptions(config, containerParser);
    }

    private void setPositionOptions(ConfigOptions config, ContainerManager containerParser) {
        switch(containerParser.getContainerType()) {
            case SHULKER_BOX:
                iconPositionOptions = (
                    containerParser.getStackSize() > 1 ? 
                    config.iconPositionOptionsStacked : 
                    config.iconPositionOptionsGeneral
                );
                break;
            case BUNDLE:
                iconPositionOptions = config.iconPositionOptionsBundle;
                break;
            case OTHER:
                iconPositionOptions = config.iconPositionOptionsGeneral;
                break;
            case NONE:
                break;
        }
    }

    protected void calculatePositions() {
        // Normal icon location
        xOffset = (float)iconPositionOptions.translateX - 8.0f;
        yOffset = (float)iconPositionOptions.translateY - 8.0f;
        zOffset = 100.0f + (float)(iconPositionOptions.translateZ * 10);

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
