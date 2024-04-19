package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.container.ContainerManager;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class CapacityBarRenderer extends OverlayRenderer {
    public static final int CAPACITY_BAR_COLOR_FILL = MathHelper.packRgb(0.4F, 0.4F, 1.0F);
    public static final int CAPACITY_BAR_COLOR_BACK = -16777216;

    float capacity;

    public int xBackgroundStart;
    public int yBackgroundStart;
    public int xBackgroundEnd;
    public int yBackgroundEnd;

    public int xCapacityStart;
    public int yCapacityStart;
    public int xCapacityEnd;
    public int yCapacityEnd;

    public CapacityBarRenderer(ConfigOptions config, ContainerManager containerParser, ItemStack stack, int x, int y) {
        super(config, stack, x, y);
        this.capacity = containerParser.getCapacity();
    }

    protected boolean canDisplay() {
        return (
            (!config.capacityBarOptions.hideWhenEmpty || capacity > 0.0f) &&
            (!config.capacityBarOptions.hideWhenFull || capacity < 1.0f)
        );
    }

    protected void calculatePositions() {
        int step = (int)(config.capacityBarOptions.length * capacity);
        int shadowHeight = config.capacityBarOptions.displayShadow ? 1 : 0;

        xBackgroundStart = stackX + config.capacityBarOptions.translateX;
        yBackgroundStart = stackY + config.capacityBarOptions.translateY;

        switch(config.capacityBarOptions.direction) {
            case LEFT_TO_RIGHT -> {
                xBackgroundEnd = xBackgroundStart + config.capacityBarOptions.length;
                yBackgroundEnd = yBackgroundStart + config.capacityBarOptions.width + shadowHeight;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundStart + step;
                yCapacityEnd = yCapacityStart + config.capacityBarOptions.width;
            }
            case RIGHT_TO_LEFT -> {
                xBackgroundEnd = xBackgroundStart + config.capacityBarOptions.length;
                yBackgroundEnd = yBackgroundStart + config.capacityBarOptions.width  + shadowHeight;
                xCapacityStart = xBackgroundEnd - step;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yCapacityStart + config.capacityBarOptions.width;
            }
            case TOP_TO_BOTTOM -> {
                xBackgroundEnd = xBackgroundStart + config.capacityBarOptions.width;
                yBackgroundEnd = yBackgroundStart + config.capacityBarOptions.length;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yCapacityStart + step;
            }
            case BOTTOM_TO_TOP -> {
                xBackgroundEnd = xBackgroundStart + config.capacityBarOptions.width;
                yBackgroundEnd = yBackgroundStart + config.capacityBarOptions.length;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundEnd - step;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yBackgroundEnd;
            }
            default -> {
                String err = "Unexpected value for capacity direction: " + config.capacityBarOptions.direction;

                SimpleShulkerPreviewMod.LOGGER.error(err);
                throw new IllegalStateException(err);
            }
        }
    }

    protected void render(DrawContext context) {
        context.fill(RenderLayer.getGuiOverlay(), xBackgroundStart, yBackgroundStart, xBackgroundEnd, yBackgroundEnd,
                    CAPACITY_BAR_COLOR_BACK);
        context.fill(RenderLayer.getGuiOverlay(), xCapacityStart, yCapacityStart, xCapacityEnd, yCapacityEnd,
                    CAPACITY_BAR_COLOR_FILL | CAPACITY_BAR_COLOR_BACK);
    }

    public void renderOptional(DrawContext context) {
        if(canDisplay()) {
            calculatePositions();
            render(context);
        }
    }
}