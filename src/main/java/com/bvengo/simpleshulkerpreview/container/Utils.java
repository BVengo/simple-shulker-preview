// package com.bvengo.simpleshulkerpreview.container;


// public class Utils {


//     /**
//      * Returns an item to display on a shulker box icon.
//      *
//      * @param stack  An ItemStack containing container data
//      * @param config The current config options for SimpleShulkerPreview
//      * @return An ItemStack for the display item, or null if there is none available
//      */
//     public static ItemStack getDisplayItem(ItemStack containerStack, ConfigOptions config) {
//         String containerName = containerStack.getName().getString().toLowerCase();

        

//         return null;
//         // Map<String, Integer> storedItems = new HashMap<>();
//         // List<ItemStack> itemStackList = new ArrayList<>();

//         // NbtCompound compound = stack.getNbt();
//         // if (compound == null) return null; // Triggers on containers in the creative menu

//         // // TODO: isStackContainer
//         // // TODO: isContainerSupported

//         // // TODO: getItemFromCustomName
//         // // Check if stack is renamed
//         // if (stack.hasCustomName() && config.useCustomName != UseCustomNameOption.NEVER) {
//         //     String customName = stack.getName().getString().toUpperCase();

//         //     ItemStack result = null;
//         //     int mostMatchLength = -1;
//         //     for (Item item : Registries.ITEM) {
//         //         String itemName = item.getName().getString().toUpperCase();
//         //         String itemPattern = "\\b" + Pattern.quote(itemName) + "\\b";
//         //         if (customName.matches(".*" + itemPattern + ".*?")) {
//         //             if (itemName.length() < mostMatchLength) continue;

//         //             // If the long match is a shulker or bundle, ignore unless there is no other match
//         //             if (itemName.contains("SHULKER") || itemName.contains("BUNDLE")) {
//         //                 if (mostMatchLength > -1) continue;
//         //                 mostMatchLength = 0;
//         //             } else {
//         //                 mostMatchLength = itemName.length();
//         //             }

//         //             result = item.getDefaultStack();
//         //         }
//         //     }
//         //     if (result != null) return result;

//         //     if (config.useCustomName == UseCustomNameOption.ALWAYS) return result;
//         // }

//         // // TODO: getContainerItems
//         // if (Utils.isShulkerStack(stack)) {
//         //     compound = compound.getCompound("BlockEntityTag");
//         //     if(compound == null) return null; // Triggers on containers in the creative menu

//         //     itemStackList = flattenStackList(compound, config);
//         // }
//         // else if (Utils.isObject(stack, RegexGroup.MINECRAFT_BUNDLE)) {
//         //     // Bundles aren't a block. Get the items from the bundle's inventory and add them to the list.
//         //     NbtList bundleItems = compound.getList("Items", 10);
//         //     if(bundleItems == null) return null;

//         //     for(int i = 0; i < bundleItems.size(); i++) {
//         //         NbtCompound bundleItem = bundleItems.getCompound(i);
//         //         ItemStack itemStack = ItemStack.fromNbt(bundleItem);
//         //         itemStackList.add(itemStack);
//         //     }
//         // }
//         // else {
//         //     return null;
//         // }

//         // // TODO: selectDisplayItem
//         // // Track both stack and item name. The name can be used in the map to count values,
//         // // but the item stack itself is required for rendering the proper information (e.g. skull textures)
//         // ItemStack displayItemStack = null;
//         // String displayItemName = null;

//         // int itemThreshold = config.stackSizeOptions.minStackSize * config.stackSizeOptions.minStackCount;

//         // for (ItemStack itemStack : itemStackList) {
//         //     String itemName = itemStack.getItem().getTranslationKey();

//         //     // Player heads
//         //     if (isObject(itemStack, RegexGroup.MINECRAFT_PLAYER_HEAD)) {
//         //         // Change skulls to be ID dependent instead of all being called "player_head"
//         //         itemName = getSkullName(itemStack);
//         //     }

//         //     // Group enchantments
//         //     if (config.groupEnchantment && itemStack.hasEnchantments()) {
//         //         itemName += ".enchanted";
//         //     }

//         //     // Select item
//         //     storedItems.merge(itemName, itemStack.getCount(), Integer::sum);
//         //     int itemCount = storedItems.get(itemName);

//         //     if (config.displayIcon == IconDisplayOption.FIRST) {
//         //         if(itemCount >= itemThreshold) {
//         //             return itemStack;
//         //         }
//         //         continue;
//         //     }

//         //     if (((displayItemName == null) || (config.displayIcon == IconDisplayOption.LAST) ||
//         //         (config.displayIcon == IconDisplayOption.MOST && storedItems.get(itemName) > storedItems.get(displayItemName)) ||
//         //         (config.displayIcon == IconDisplayOption.LEAST && storedItems.get(itemName) < storedItems.get(displayItemName))) &&
//         //         (itemCount >= itemThreshold)) {
//         //         displayItemStack = itemStack;
//         //         displayItemName = itemName;
//         //         continue;
//         //     }

//         //     if (config.displayIcon == IconDisplayOption.UNIQUE && !itemName.equals(displayItemName)) return null;
//         // }

//         // return displayItemStack;
//     }

//     /**
//      * Returns an item to display on a shulker box icon.
//      *
//      * @param itemStack An ItemStack containing a minecraft player head
//      * @return A String indicating with the head ID. If missing, returns a default "minecraft.player_head".
//      */
//     // private static String getSkullName(ItemStack itemStack) {
//     //     String name = itemStack.getItem().getTranslationKey();
//     //     if (!itemStack.hasNbt()) return name;

//     //     NbtCompound skullCompound = itemStack.getNbt();
//     //     if (skullCompound == null) return name;

//     //     skullCompound = itemStack.getNbt().getCompound("SkullOwner");
//     //     if (skullCompound == null) return name;

//     //     NbtElement skullIdElement = skullCompound.get("Id");
//     //     if (skullIdElement == null) return name;

//     //     name += skullIdElement.toString();

//     //     return name;
//     // }
// }