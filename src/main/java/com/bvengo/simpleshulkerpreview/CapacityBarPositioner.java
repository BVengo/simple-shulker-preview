package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;

public class CapacityBarPositioner {
    ConfigOptions config;
    float capacity;

    int step = (int)(config.capacityBarOptions.length * capacity);
    int shadowHeight = config.capacityBarOptions.displayShadow ? 1 : 0;

    public int xBackgroundStart;
    public int yBackgroundStart;
    public int xBackgroundEnd;
    public int yBackgroundEnd;
    
    public int xCapacityStart;
    public int yCapacityStart;
    public int xCapacityEnd;
    public int yCapacityEnd;

    public CapacityBarPositioner(ConfigOptions config, float capacity) {
        this.config = config;
        this.capacity = capacity;
    }

    public boolean canDisplay() {
        return (
            (!config.capacityBarOptions.hideWhenEmpty || capacity > 0.0f) &&
            (!config.capacityBarOptions.hideWhenFull || capacity < 1.0f)
        );
    }

    public void calculatePositions(int x, int y) {
        int step = (int)(config.capacityBarOptions.length * capacity);
        int shadowHeight = config.capacityBarOptions.displayShadow ? 1 : 0;

        xBackgroundStart = x + config.capacityBarOptions.translateX;
        yBackgroundStart = y + config.capacityBarOptions.translateY;

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
}
