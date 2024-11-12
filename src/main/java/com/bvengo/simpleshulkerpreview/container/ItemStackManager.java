package com.bvengo.simpleshulkerpreview.container;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.config.CustomNameOption;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemStackManager {
    private static class ItemStackGrouper {
        public final ItemStack itemStack;
        public String itemString;

        public ItemStackGrouper(ItemStack itemStack) {
            this.itemStack = itemStack;
            setItemString();
        }

        private void setItemString() {
            itemString = itemStack.getItem().getTranslationKey();
            
            // Player heads
            if(itemStack.isOf(Items.PLAYER_HEAD)) {
                String skullName = getSkullName(itemStack);
                if(skullName != null) {
                    itemString += skullName;
                }
            }

            // Group enchantments
            if (SimpleShulkerPreviewMod.CONFIGS.groupEnchantment && itemStack.hasEnchantments()) {
                itemString += ".enchanted";
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            
            ItemStackGrouper otherGrouper = (ItemStackGrouper) obj;
            
            return (
                itemString.equals(otherGrouper.itemString) && 
                itemStack.getItem().equals(otherGrouper.itemStack.getItem())
            );
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemString);
        }
    }

    private static Map<ItemStackGrouper, Integer> groupItemStacks(Iterable<ItemStack> itemIterable) {
        return StreamSupport.stream(itemIterable.spliterator(), false)
                .collect(Collectors.groupingBy(ItemStackGrouper::new, Collectors.summingInt(ItemStack::getCount)));
    }
    
    public static ItemStack getDisplayStackFromIterable(Iterable<ItemStack> itemIterable) {
        int itemThreshold = SimpleShulkerPreviewMod.CONFIGS.stackSizeOptions.minStackSize * SimpleShulkerPreviewMod.CONFIGS.stackSizeOptions.minStackCount;

        switch (SimpleShulkerPreviewMod.CONFIGS.displayIcon) {
            case FIRST:
                return StreamSupport.stream(itemIterable.spliterator(), false)
                        .filter(itemStack -> itemStack.getCount() >= itemThreshold)
                        .findFirst()
                        .orElse(null);

            case LAST:
                return StreamSupport.stream(itemIterable.spliterator(), false)
                        .filter(itemStack -> itemStack.getCount() >= itemThreshold)
                        .reduce((first, second) -> second)
                        .orElse(null);

            case UNIQUE:
                Map<ItemStackGrouper, Integer> groupedItems = groupItemStacks(itemIterable);
                if (groupedItems.size() == 1) {
                    Map.Entry<ItemStackGrouper, Integer> entry = groupedItems.entrySet().iterator().next();
                    return entry.getValue() >= itemThreshold ? entry.getKey().itemStack : null;
                }
                return null;

            case MOST:
                return groupItemStacks(itemIterable).entrySet().stream()
                        .filter(entry -> entry.getValue() >= itemThreshold)
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .map(grouper -> grouper.itemStack)
                        .orElse(null);

            case LEAST:
                return groupItemStacks(itemIterable).entrySet().stream()
                        .filter(entry -> entry.getValue() >= itemThreshold)
                        .min(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .map(grouper -> grouper.itemStack)
                        .orElse(null);

            default:
                return null;
        }
    }

    public static ItemStack getItemFromCustomName(ItemStack itemStack) {
        if(SimpleShulkerPreviewMod.CONFIGS.customName == CustomNameOption.NEVER) return null;

        Text customName = itemStack.getComponents().get(DataComponentTypes.CUSTOM_NAME);
        if(customName == null) return null;

        Identifier itemId = Identifier.tryParse(customName.getString());
        if(itemId == null) return null;
        
        Item item = Registries.ITEM.get(itemId);
        return new ItemStack(item);
    }

    /**
     * Returns an item to display on a shulker box icon.
     *
     * @param itemStack An ItemStack containing a minecraft player head
     * @return A String indicating with the head ID. If missing, returns a default "minecraft.player_head".
     */
    private static String getSkullName(ItemStack itemStack) {
        ProfileComponent profileComponent = itemStack.get(DataComponentTypes.PROFILE);
        if(profileComponent == null) return null;

        return(profileComponent.name().orElse(null));
    }
}
