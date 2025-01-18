package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.container.ContainerManager;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.math.Fraction;

public class CapacityBarRenderer extends OverlayRenderer {
    // Taken from BundleItem.java
    private static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);

    private Fraction capacity;

    private int xBackgroundStart;
    private int yBackgroundStart;
    private int xBackgroundEnd;
    private int yBackgroundEnd;

    private int xCapacityStart;
    private int yCapacityStart;
    private int xCapacityEnd;
    private int yCapacityEnd;

    public CapacityBarRenderer(ContainerManager containerParser, ItemStack stack, int x, int y) {
        super(stack, x, y);
        this.capacity = containerParser.getCapacity();
    }

    protected boolean canDisplay() {
        return (
            (!SimpleShulkerPreviewMod.CONFIGS.capacityHideWhenEmpty || capacity.compareTo(Fraction.ZERO) > 0) &&
            (!SimpleShulkerPreviewMod.CONFIGS.capacityHideWhenFull || capacity.compareTo(Fraction.ONE) < 0)
        );
    }

    protected void calculatePositions() {
        int step = (int)(SimpleShulkerPreviewMod.CONFIGS.capacityLength * capacity.floatValue());
        int shadowHeight = SimpleShulkerPreviewMod.CONFIGS.capacityDisplayShadow ? 1 : 0;

        xBackgroundStart = stackX + SimpleShulkerPreviewMod.CONFIGS.capacityTranslateX;
        yBackgroundStart = stackY + SimpleShulkerPreviewMod.CONFIGS.capacityTranslateY;

        switch(SimpleShulkerPreviewMod.CONFIGS.capacityDirection) {
            case LEFT_TO_RIGHT -> {
                xBackgroundEnd = xBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityLength;
                yBackgroundEnd = yBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityWidth + shadowHeight;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundStart + step;
                yCapacityEnd = yCapacityStart + SimpleShulkerPreviewMod.CONFIGS.capacityWidth;
            }
            case RIGHT_TO_LEFT -> {
                xBackgroundEnd = xBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityLength;
                yBackgroundEnd = yBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityWidth  + shadowHeight;
                xCapacityStart = xBackgroundEnd - step;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yCapacityStart + SimpleShulkerPreviewMod.CONFIGS.capacityWidth;
            }
            case TOP_TO_BOTTOM -> {
                xBackgroundEnd = xBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityWidth;
                yBackgroundEnd = yBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityLength;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yCapacityStart + step;
            }
            case BOTTOM_TO_TOP -> {
                xBackgroundEnd = xBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityWidth;
                yBackgroundEnd = yBackgroundStart + SimpleShulkerPreviewMod.CONFIGS.capacityLength;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundEnd - step;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yBackgroundEnd;
            }
        }
    }

    protected void render(DrawContext context) {
        calculatePositions();

        if(SimpleShulkerPreviewMod.CONFIGS.capacityDisplayShadow) {
            context.fill(RenderLayer.getGuiOverlay(), xBackgroundStart, yBackgroundStart, xBackgroundEnd, yBackgroundEnd, Colors.BLACK);
        }

        int colour = capacity.compareTo(Fraction.ONE) == 0 ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
        context.fill(RenderLayer.getGuiOverlay(), xCapacityStart, yCapacityStart, xCapacityEnd, yCapacityEnd, ColorHelper.fullAlpha(colour));
    }
}