package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions.DisplayOption;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.Map;

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
     *
     * @param compound  An NBTCompound containing container data
     * @param selection Which item to display
     * @return An ItemStack for the display item, or null if there is none available
     */
    public static ItemStack getDisplayItem(NbtCompound compound, DisplayOption selection) {
        Map<Item, Integer> storedItems = new HashMap<>();
        Item displayItem = null;

        NbtList nbtList = compound.getList("Items", 10);

        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);

            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            Item item = itemStack.getItem();

            int itemCount = itemStack.getCount();
            storedItems.merge(item, itemCount, Integer::sum);

            if (selection == DisplayOption.FIRST) {
                return itemStack;
            }

            if ((displayItem == null) ||
                    (selection == DisplayOption.LAST) ||
                    (selection == DisplayOption.MOST && storedItems.get(item) > storedItems.get(displayItem)) ||
                    (selection == DisplayOption.LEAST && storedItems.get(item) < storedItems.get(displayItem))) {

                displayItem = item;
                continue;
            }

            if (selection == DisplayOption.UNIQUE && item != displayItem) {
                return null;
            }
        }

        return new ItemStack(displayItem);
    }
}