package com.bvengo.simpleshulkerpreview;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class Utils {
    public static boolean isShulkerBox(NbtCompound compound) {

        // BlockEntityTag 10 -> item has Block Entity Tag
        if (compound != null && compound.contains("BlockEntityTag", 10)) {
            NbtCompound blockEntityTag = compound.getCompound("BlockEntityTag");

            // Items 9 -> object holds shulker items inventory
            return blockEntityTag != null && blockEntityTag.contains("Items", 9);
        }

        return false;
    }

    /**
     * Returns an item to display on a shulker box icon.
     * @param compound An NBTCompound containing container data
     * @param containerSize The size of the container being queried
     * @param unique If the item to be displayed must be the only item type in the container
     * @return An ItemStack for the display item, or null if there is none available
     */
    public static ItemStack getDisplayItem(NbtCompound compound, int containerSize, boolean unique) {
        ItemStack displayItem = null;

        DefaultedList<ItemStack> itemList = DefaultedList.ofSize(containerSize, ItemStack.EMPTY);
        Inventories.readNbt(compound, itemList);

        for (ItemStack itemStack : itemList) {
            if (itemStack.isEmpty()) {
                continue;
            } else if (!unique) {
                return itemStack;
            }
            if (displayItem == null) {
                displayItem = itemStack;
                continue;
            }
            if (!itemStack.isOf(displayItem.getItem())) {
                return null;
            }
        }

        return displayItem;
    }
}
