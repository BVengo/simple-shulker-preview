package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public abstract class OverlayRenderer {

    ConfigOptions config;
    ItemStack stack;

    int stackX;
    int stackY;

    public OverlayRenderer(ConfigOptions config, ItemStack stack, int x, int y) {
        this.config = config;
        this.stack = stack;
        this.stackX = x;
        this.stackY = y;
    }

    public void renderOptional(DrawContext context) {
        if(canDisplay()) {
            calculatePositions();
            render(context);
        }
    }

    protected abstract boolean canDisplay();
    protected abstract void calculatePositions();
    protected abstract void render(DrawContext context);
}
