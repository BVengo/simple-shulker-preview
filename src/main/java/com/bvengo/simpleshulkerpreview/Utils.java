package com.bvengo.simpleshulkerpreview;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.config.IconDisplayOption;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BundleItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    public static boolean isObject(ItemStack stack, RegexGroup group) {
        if(stack == null) return false;

        Pattern pattern = Pattern.compile(group.regex);
        Matcher matcher = pattern.matcher(stack.getTranslationKey());

        return matcher.find();
    }

    /**
     * Checks if an item meets the requirements for rendering the overlay, based on the selected configs.
     *
     * @param stack The inventory stack to render over (e.g. a shulkerbox)
     * @return A boolean indicating if the overlay should be rendered.
     */
    public static boolean checkStackAllowed(ItemStack stack) {
        ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();

        if(config.disableMod) return false;
        if(stack.getCount() > 1 && Utils.isObject(stack, RegexGroup.MINECRAFT_SHULKER)) return config.supportStackedShulkers;
        if(Utils.isObject(stack, RegexGroup.MINECRAFT_BUNDLE)) return config.supportBundles;

        return Utils.isObject(stack, RegexGroup.MINECRAFT_SHULKER);
    }

    /**
     * Returns an item to display on a shulker box icon.
     *
     * @param stack  An ItemStack containing container data
     * @param config The current config options for SimpleShulkerPreview
     * @return An ItemStack for the display item, or null if there is none available
     */
    public static ItemStack getDisplayItem(ItemStack stack, ConfigOptions config) {
        Map<String, Integer> storedItems = new HashMap<>();
        List<ItemStack> itemStackList = new ArrayList<>();

        NbtCompound compound = stack.getNbt();
        if(compound == null) return null; // Triggers on containers in the creative menu

        if(Utils.isObject(stack, RegexGroup.MINECRAFT_SHULKER)) {
            compound = compound.getCompound("BlockEntityTag");
            if(compound == null) return null; // Triggers on containers in the creative menu

            itemStackList = flattenStackList(compound, config);
        }
        else if(Utils.isObject(stack, RegexGroup.MINECRAFT_BUNDLE)) {
            // Bundles aren't a block. Get the items from the bundle's inventory and add them to the list.
            NbtList bundleItems = compound.getList("Items", 10);
            if(bundleItems == null) return null;

            for(int i = 0; i < bundleItems.size(); i++) {
                NbtCompound bundleItem = bundleItems.getCompound(i);
                ItemStack itemStack = ItemStack.fromNbt(bundleItem);
                itemStackList.add(itemStack);
            }
        }
        else {
            return null;
        }
        
        // Track both stack and item name. The name can be used in the map to count values,
        // but the item stack itself is required for rendering the proper information (e.g. skull textures)
        ItemStack displayItemStack = null;
        String displayItemName = null;

        int itemThreshold = config.stackSizeOptions.minStackSize * config.stackSizeOptions.minStackCount;

        for (ItemStack itemStack : itemStackList) {
            String itemName = itemStack.getItem().getTranslationKey();

            // Player heads
            if (isObject(itemStack, RegexGroup.MINECRAFT_PLAYER_HEAD)) {
                // Change skulls to be ID dependent instead of all being called "player_head"
                itemName = getSkullName(itemStack);
            }

            // Group enchantments
            if (config.groupEnchantment && itemStack.hasEnchantments()) {
                itemName += ".enchanted";
            }

            // Select item
            storedItems.merge(itemName, itemStack.getCount(), Integer::sum);
            int itemCount = storedItems.get(itemName);

            if (config.displayIcon == IconDisplayOption.FIRST) {
                if(itemCount >= itemThreshold) {
                    return itemStack;
                }
                continue;
            }
            
            if (((displayItemName == null) || (config.displayIcon == IconDisplayOption.LAST) ||
                (config.displayIcon == IconDisplayOption.MOST && storedItems.get(itemName) > storedItems.get(displayItemName)) ||
                (config.displayIcon == IconDisplayOption.LEAST && storedItems.get(itemName) < storedItems.get(displayItemName))) &&
                (itemCount >= itemThreshold)) {
                displayItemStack = itemStack;
                displayItemName = itemName;
                continue;
            }

            if (config.displayIcon == IconDisplayOption.UNIQUE && !itemName.equals(displayItemName)) return null;
        }

        return displayItemStack;
    }

    /**
     * Flattens container NBT data into a single ArrayList. This is for recursive shulker compatibility.
     * @param compound A container's NbtCompound
     * @param config The current config options for SimpleShulkerPreview
     * @return A list of ItemStacks extracted from a container NbtCompound
     */
     public static List<ItemStack> flattenStackList(NbtCompound compound, ConfigOptions config) {
         List<ItemStack> itemStackList = new ArrayList<>();

         NbtList nbtList = compound.getList("Items", 10);
         if (nbtList == null) return itemStackList;

         for (int i = 0; i < nbtList.size(); ++i) {
             NbtCompound nbtCompound = nbtList.getCompound(i);
             ItemStack itemStack = ItemStack.fromNbt(nbtCompound);

             itemStackList.add(itemStack);

             if (config.supportRecursiveShulkers && isObject(itemStack, RegexGroup.MINECRAFT_SHULKER)) {
                 NbtCompound stackCompound = itemStack.getNbt();
                 if (stackCompound == null) continue;

                 stackCompound = stackCompound.getCompound("BlockEntityTag");
                 if(stackCompound == null) continue; // Triggers on containers in the creative menu

                 int multiplier = config.supportStackedShulkers ? itemStack.getCount() : 1;
                 for (int j = 0; j < multiplier; j++) {
                     itemStackList.addAll(flattenStackList(stackCompound, config));
                 }
             }
         }

         return itemStackList;
     }

    /**
     * Returns an item to display on a shulker box icon.
     *
     * @param itemStack  An ItemStack containing a minecraft player head
     * @return A String indicating with the head ID. If missing, returns a default "minecraft.player_head".
     */
    private static String getSkullName(ItemStack itemStack) {
        String name = itemStack.getItem().getTranslationKey();
        if (!itemStack.hasNbt()) return name;

        NbtCompound skullCompound = itemStack.getNbt();
        if (skullCompound == null) return name;

        skullCompound = itemStack.getNbt().getCompound("SkullOwner");
        if (skullCompound == null) return name;

        NbtElement skullIdElement = skullCompound.get("Id");
        if (skullIdElement == null) return name;

        name += skullIdElement.toString();

        return name;
    }
}