package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.RegexGroup;
import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.access.DrawContextAccess;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.config.IconPositionOptions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class IconRenderer extends OverlayRenderer {

    IconPositionOptions iconPositionOptions;

    public float scale;
    public float xOffset;
    public float yOffset;
    public float zOffset;

    public IconRenderer(ConfigOptions config, ItemStack stack, ItemStack displayItem, int x, int y) {
        super(config, displayItem, x, y);

        if(Utils.isObject(stack, RegexGroup.MINECRAFT_BUNDLE)) {
            iconPositionOptions = config.iconPositionOptionsBundle;
        }
        else if(Utils.isObject(stack, RegexGroup.MINECRAFT_SHULKER) && stack.getCount() > 1) {
            iconPositionOptions = config.iconPositionOptionsStacked;
        }
        else {
            iconPositionOptions = config.iconPositionOptionsGeneral;
        }

    }

    protected boolean canDisplay() {
        return stack != null;
    }

    protected void calculatePositions() {


        // Normal icon location
        this.xOffset = (float)iconPositionOptions.translateX - 8.0f;
        this.yOffset = (float)iconPositionOptions.translateY - 8.0f;
        this.zOffset = 100.0f + (float)(iconPositionOptions.translateZ * 10);

        this.scale = iconPositionOptions.scale;
    }

    protected void render(DrawContext context) {
        ((DrawContextAccess) context).setAdjustSize(true);
        context.drawItemWithoutEntity(stack, stackX, stackY);
        ((DrawContextAccess) context).setAdjustSize(false);
    }

    public void renderOptional(DrawContext context) {
        if(canDisplay()) {
            calculatePositions();
            render(context);
        }
    }
}
