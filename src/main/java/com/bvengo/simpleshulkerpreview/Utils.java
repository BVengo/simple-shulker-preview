package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;

public class Utils {

    /**
     * Returns an item to display on a shulker box icon.
     *
     * @param compound An NBTCompound containing container data
     * @param config   The current config options for SimpleShulkerPreview
     * @return An ItemStack for the display item, or null if there is none matching the config or available
     */
    public static @Nullable ItemStack getDisplayItem(NbtCompound compound, ConfigOptions config) {
        // If enabled, enable adding items recursively from nested shulkers
        LinkedList<ItemStack> storedItems = config.supportRecursiveShulkers ?
                new LinkedList<>() {//region ...}
                    public @Override boolean add(ItemStack itemStack) {
                        // Add the nested shulker anyway
                        super.add(itemStack);
                        // If it's a shulker
                        if (isNotAShulker(itemStack)) return true;
                        // And it has items
                        NbtCompound compound;
                        NbtList nbtList;
                        if ((compound = itemStack.getNbt()) == null
                                || (compound = compound.getCompound("BlockEntityTag")) == null
                                || (nbtList = compound.getList("Items", 10)) == null
                        ) return true;
                        // Get the amount of shulkers stacked in that stack
                        int multiplier = config.supportStackedShulkers ? itemStack.getCount() : 1;
                        // And add each item it contains, multiplied by the amount of shulkers in the stack
                        for (int i = 0; i < nbtList.size(); ++i) {
                            ItemStack containedItemStack = ItemStack.fromNbt(nbtList.getCompound(i));
                            containedItemStack.setCount(containedItemStack.getCount() * multiplier);
                            add(containedItemStack);
                        }
                        // Say that the list was modified
                        return true;
                    }
                } //endregion
                : new LinkedList<>();
        // Start here
        // If the shulker has items
        NbtList nbtList;
        if (       (compound = compound.getCompound("BlockEntityTag")) == null
                || (nbtList = compound.getList("Items", 10)) == null
        ) return null;
        // Add each item it contains
        for (int i = 0; i < nbtList.size(); ++i) storedItems.add(ItemStack.fromNbt(nbtList.getCompound(i)));
        // If none, no overlay
        if (storedItems.isEmpty()) return null;
        // Check whether further work is required
        switch (config.displayItem){
            case FIRST -> {return storedItems.getFirst();}
            case LAST -> {return storedItems.getLast();}
            default -> {}
        }

        // Set up merging
        final BiPredicate<ItemStack, ItemStack> shouldMerge = (a,b)-> {
            // Define cases two stacks should merge in
            if (config.groupEnchantment && a.hasEnchantments() && b.hasEnchantments()) return true;
            //noinspection RedundantIfStatement
            if (ItemStack.areItemsEqual(a, b) && (!config.compareNBT || ItemStack.areNbtEqual(a, b))) return true;
            return false;
        };
        // Merge the counts of the stored items
        for (var backIterator = storedItems.descendingIterator(); backIterator.hasNext();) {
            ItemStack base = backIterator.next();
            for (ItemStack dest : storedItems) {
                if (dest == base) continue;
                if (shouldMerge.test(base, dest)) {
                    dest.setCount(dest.getCount() + base.getCount());
                    backIterator.remove();
                    break;
                }
            }
        }
        // Complete the options
        if (storedItems.isEmpty())return null;
        switch (config.displayItem){
            case MOST  -> {return (storedItems.stream().reduce((a,b)->a.getCount()>b.getCount()?a:b)).get();}
            case LEAST -> {return (storedItems.stream().reduce((a,b)->a.getCount()<b.getCount()?a:b)).get();}
            case UNIQUE -> {return storedItems.size() == 1 ? storedItems.getFirst() : null;}
            case RANDOM -> {
                int time = config.changePreview ? (int) ((System.currentTimeMillis() >> 9) & 0x7fffffff) : 0; // changing every 512ms
                return storedItems.get(time%storedItems.size());
            }
        }

        return null;
    }

    /**
     * Returns whether a stack is not a shulker box.
     *
     * @param stack ItemStack being checked
     * @return Whether the stack is not a shulker
     */
    public static boolean isNotAShulker(ItemStack stack){
        return !(stack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof ShulkerBoxBlock);
    }

    /**
     * Returns whether a shulker is completely filled with the same item.
     *
     * @param compound An NBTCompound containing container data
     * @return Whether the shulker is considered full and not mixed by TMC
     */
    public static boolean isShulkerFull(NbtCompound compound){
        // If the shulker has items
        NbtList nbtList;
        if (       (compound = compound.getCompound("BlockEntityTag")) == null
                || (nbtList = compound.getList("Items", 10)) == null
        ) return false;
        // Chack that there are 27 stacks
        if (nbtList.size() != 27) return false;
        // Check that the first stack is full
        ItemStack firstItemStack = ItemStack.fromNbt(nbtList.getCompound(0));
        if (firstItemStack.getCount() != firstItemStack.getMaxCount()) return false;
        // Check that all other stacks are the same as the first
        // NbtCompound stackCompound = nbtList.getCompound(0);
        for (int i = 1; i < nbtList.size(); ++i) {
            // if (!stackCompound.equals(nbtList.getCompound(i))) return false; TODO faster check
            if (!ItemStack.areEqual(firstItemStack, ItemStack.fromNbt(nbtList.getCompound(i)))) return false;
        }
        return true;
    }
}