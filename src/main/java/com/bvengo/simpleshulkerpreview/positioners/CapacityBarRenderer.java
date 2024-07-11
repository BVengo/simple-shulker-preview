package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
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

    public CapacityBarRenderer(ContainerManager containerParser, ItemStack stack, int x, int y) {
        super(stack, x, y);
        this.capacity = containerParser.getCapacity();
    }

    protected boolean canDisplay() {
        return (
            (!SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.hideWhenEmpty || capacity > 0.0f) &&
            (!SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.hideWhenFull || capacity < 1.0f)
        );
    }

    protected void calculatePositions() {
        int step = (int)(SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.length * capacity);
        int shadowHeight = SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.displayShadow ? 1 : 0;

        xBackgroundStart = stackX + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.translateX;
        yBackgroundStart = stackY + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.translateY;

        switch(SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.direction) {
            case LEFT_TO_RIGHT -> {
                xBackgroundEnd = xBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.length;
                yBackgroundEnd = yBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.width + shadowHeight;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundStart + step;
                yCapacityEnd = yCapacityStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.width;
            }
            case RIGHT_TO_LEFT -> {
                xBackgroundEnd = xBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.length;
                yBackgroundEnd = yBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.width  + shadowHeight;
                xCapacityStart = xBackgroundEnd - step;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yCapacityStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.width;
            }
            case TOP_TO_BOTTOM -> {
                xBackgroundEnd = xBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.width;
                yBackgroundEnd = yBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.length;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yCapacityStart + step;
            }
            case BOTTOM_TO_TOP -> {
                xBackgroundEnd = xBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.width;
                yBackgroundEnd = yBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.length;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundEnd - step;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yBackgroundEnd;
            }
            default -> {
                String err = "Unexpected value for capacity direction: " + SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions.direction;

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