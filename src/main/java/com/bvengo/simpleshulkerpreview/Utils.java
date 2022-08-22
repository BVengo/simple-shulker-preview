package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions.DisplayOption;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

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
        ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();

        Map<String, Integer> storedItems = new HashMap<>();

        // Track both stack and item name. The name can be used in the map to count values,
        // but the item stack itself is required for rendering the proper information (e.g. skull textures)
        ItemStack displayItemStack = null;
        String displayItemName = null;

        NbtList nbtList = compound.getList("Items", 10);

        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);

            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            String itemName = itemStack.getItem().getTranslationKey();

            if (itemStack.isOf(Items.PLAYER_HEAD)) {
                // Change skulls to be ID dependent instead of all being called "player_head"
                if (config.supportCustomHeads) {
                    itemName = getSkullName(itemStack);
                } else {
                    itemStack = new ItemStack(itemStack.getItem());
                }
            }

            if (config.groupEnchantment && itemStack.hasEnchantments()) {
                itemName += ".enchanted";
            }

            int itemCount = itemStack.getCount();
            storedItems.merge(itemName, itemCount, Integer::sum);

            if (selection == DisplayOption.FIRST) return itemStack;

            if ((displayItemName == null) ||
                    (selection == DisplayOption.LAST) ||
                    (selection == DisplayOption.MOST && storedItems.get(itemName) > storedItems.get(displayItemName)) ||
                    (selection == DisplayOption.LEAST && storedItems.get(itemName) < storedItems.get(displayItemName))) {

                displayItemStack = itemStack;
                displayItemName = itemName;
                continue;
            }

            if (selection == DisplayOption.UNIQUE && !itemName.equals(displayItemName)) return null;
        }

        return displayItemStack;
    }

    /**
     * Returns an item to display on a shulker box icon.
     *
     * @param itemStack  An ItemStack containing a minecraft player head
     * @return A String indicating with the head ID. If missing, returns a default "minecraft.player_head".
     */
    public static String getSkullName(ItemStack itemStack) {
        String name = itemStack.getItem().getTranslationKey();

        if (!itemStack.hasNbt()) return name;

        NbtCompound skullCompound = itemStack.getNbt().getCompound("SkullOwner");
        if(skullCompound == null) return name;

        NbtElement skullIdElement = skullCompound.get("Id");
        if(skullIdElement == null) return name;

        name += skullIdElement.toString();

        return name;
    }
}