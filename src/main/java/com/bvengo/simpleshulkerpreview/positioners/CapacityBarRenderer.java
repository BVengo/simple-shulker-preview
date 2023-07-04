package com.bvengo.simpleshulkerpreview.positioners;

import com.bvengo.simpleshulkerpreview.RegexGroup;
import com.bvengo.simpleshulkerpreview.access.ShulkerSizeExtension;
import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.Utils;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
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

    public CapacityBarRenderer(ConfigOptions config, ItemStack stack, int x, int y) {
        super(config, stack, x, y);
        this.capacity = getCapacity(stack, config);
    }

    /**
     * Returns the ratio full that a container is.
     * @param stack A container's NbtCompound
     * @param config The current config options for SimpleShulkerPreview
     * @return A float between 0 and 1 indicating how full the container is
     */
    private float getCapacity(ItemStack stack, ConfigOptions config) {
        NbtCompound compound = stack.getNbt();
        if(compound == null) return 0; // Triggers on containers in the creative menu

        compound = compound.getCompound("BlockEntityTag");
        if(compound == null) return 0; // Triggers on containers in the creative menu

        NbtList nbtList = compound.getList("Items", 10);
        if (nbtList == null) return 0; // No items in container

        int total = 27; // Default number of slots in a shulker box
        float sumCapacity = 0; // Sum of 'fullness' level for each slot

        if (Utils.isShulkerStack(stack)) {
            BlockWithEntity block = (BlockWithEntity) ((BlockItem) stack.getItem()).getBlock();
            ShulkerSizeExtension entity = (ShulkerSizeExtension) block.createBlockEntity(BlockPos.ORIGIN, block.getDefaultState());

            if(entity != null) {
                total = entity.simple_shulker_preview$getInventorySize();
            }

        } else if (Utils.isObject(stack, RegexGroup.MINECRAFT_BUNDLE)) {
            total = 1;
        }

        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);

            // Reduce the maxItemCount if items can't stack to 64
            int maxStackSize = itemStack.getItem().getMaxCount();
            sumCapacity += (float) itemStack.getCount() / (float) maxStackSize;

            // Calculate the ratio of items in stacked containers
            if (config.supportRecursiveShulkers && Utils.isShulkerStack(itemStack)) {
                // Can ignore stacked shulkers since their ratio multiplier gets cancelled out
                sumCapacity += getCapacity(itemStack, config);
            }

            if (config.supportBundles && Utils.isObject(itemStack, RegexGroup.MINECRAFT_BUNDLE)) {
                sumCapacity += BundleItem.getAmountFilled(itemStack);
            }
        }

        return sumCapacity / (float)total; // Total divided by number of shulker slots
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