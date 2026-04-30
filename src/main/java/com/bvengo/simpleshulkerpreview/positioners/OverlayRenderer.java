package com.bvengo.simpleshulkerpreview.positioners;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public abstract class OverlayRenderer {

    ItemStack stack;

    int stackX;
    int stackY;

    public OverlayRenderer(ItemStack stack, int x, int y) {
        this.stack = stack;
        this.stackX = x;
        this.stackY = y;
    }

    public void renderOptional(GuiGraphicsExtractor context) {
        if(canDisplay()) {
            calculatePositions();
            render(context);
        }
    }

    protected abstract boolean canDisplay();
    protected abstract void calculatePositions();
    protected abstract void render(GuiGraphicsExtractor context);
}
