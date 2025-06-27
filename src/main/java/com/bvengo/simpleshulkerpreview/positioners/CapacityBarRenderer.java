package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.config.CapacityBarOptions;
import com.bvengo.simpleshulkerpreview.container.ContainerManager;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
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
    
    private final CapacityBarOptions configs = SimpleShulkerPreviewMod.CONFIGS.capacityBarOptions;

    public CapacityBarRenderer(ContainerManager containerParser, ItemStack stack, int x, int y) {
        super(stack, x, y);
        this.capacity = containerParser.getCapacity();
    }

    protected boolean canDisplay() {
        return (
            (!configs.hideWhenEmpty || capacity.compareTo(Fraction.ZERO) > 0) &&
            (!configs.hideWhenFull || capacity.compareTo(Fraction.ONE) < 0)
        );
    }

    protected void calculatePositions() {
        int step = (int)(configs.length * capacity.floatValue());
        int shadowHeight = configs.displayShadow ? 1 : 0;

        xBackgroundStart = stackX + configs.translateX;
        yBackgroundStart = stackY + configs.translateY;

        switch(configs.direction) {
            case LEFT_TO_RIGHT -> {
                xBackgroundEnd = xBackgroundStart + configs.length;
                yBackgroundEnd = yBackgroundStart + configs.width + shadowHeight;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundStart + step;
                yCapacityEnd = yCapacityStart + configs.width;
            }
            case RIGHT_TO_LEFT -> {
                xBackgroundEnd = xBackgroundStart + configs.length;
                yBackgroundEnd = yBackgroundStart + configs.width  + shadowHeight;
                xCapacityStart = xBackgroundEnd - step;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yCapacityStart + configs.width;
            }
            case TOP_TO_BOTTOM -> {
                xBackgroundEnd = xBackgroundStart + configs.width;
                yBackgroundEnd = yBackgroundStart + configs.length;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundStart;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yCapacityStart + step;
            }
            case BOTTOM_TO_TOP -> {
                xBackgroundEnd = xBackgroundStart + configs.width;
                yBackgroundEnd = yBackgroundStart + configs.length;
                xCapacityStart = xBackgroundStart;
                yCapacityStart = yBackgroundEnd - step;
                xCapacityEnd = xBackgroundEnd;
                yCapacityEnd = yBackgroundEnd;
            }
            default -> {
                String err = "Unexpected value for capacity direction: " + configs.direction;

                SimpleShulkerPreviewMod.LOGGER.error(err);
                throw new IllegalStateException(err);
            }
        }
    }

    protected void render(DrawContext context) {
        if(configs.displayShadow) {
            context.fill(RenderPipelines.GUI, xBackgroundStart, yBackgroundStart, xBackgroundEnd, yBackgroundEnd, Colors.BLACK);
        }

        int colour = capacity.compareTo(Fraction.ONE) == 0 ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
        context.fill(RenderPipelines.GUI, xCapacityStart, yCapacityStart, xCapacityEnd, yCapacityEnd, ColorHelper.fullAlpha(colour));
    }

    public void renderOptional(DrawContext context) {
        if(canDisplay()) {
            calculatePositions();
            render(context);
        }
    }
}