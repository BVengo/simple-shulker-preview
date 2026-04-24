package com.bvengo.simpleshulkerpreview.container;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ResolvableProfile;
import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.config.CustomNameOption;
import org.apache.commons.lang3.math.Fraction;

public class ItemStackManager {
    private static class ItemStackGrouper {
        public final ItemStack itemStack;
        public String itemString;

        public ItemStackGrouper(ItemStack itemStack) {
            this.itemStack = itemStack;
            setItemString();
        }

        private void setItemString() {
            itemString = itemStack.getItem().getDescriptionId();
            
            // Player heads
            if(itemStack.is(Items.PLAYER_HEAD)) {
                String skullName = getSkullName(itemStack);
                if(skullName != null) {
                    itemString += "." + skullName;
                }
            }

			// Potions
			if(itemStack.is(Items.POTION)) {
				String potionType = getPotionType(itemStack);
				if(potionType != null) {
					itemString += "." + potionType;
				}
			}

            // Group enchantments
            if (SimpleShulkerPreviewMod.CONFIGS.groupEnchantment && itemStack.isEnchanted()) {
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

    private static Map<ItemStackGrouper, Double> groupItemStacks(Iterable<ItemStack> itemIterable) {
        return StreamSupport.stream(itemIterable.spliterator(), false)
                .collect(Collectors.groupingBy(
                        ItemStackGrouper::new,
                        LinkedHashMap::new, // Preserving insertion order
                        Collectors.summingDouble(x -> getItemCountEquivalent(x).doubleValue())));
    }

    public static Fraction getItemFraction(ItemStack itemStack) {
        return Fraction.getFraction(itemStack.getCount(), itemStack.getMaxStackSize());
    }

    public static Fraction getItemCountEquivalent(ItemStack itemStack) {
        return getItemFraction(itemStack).multiplyBy(Fraction.getFraction(64, 1));
    }
    
    public static ItemStack getDisplayStackFromIterable(Iterable<ItemStack> itemIterable) {
        int itemThreshold = SimpleShulkerPreviewMod.CONFIGS.stackSizeOptions.minStackSize * SimpleShulkerPreviewMod.CONFIGS.stackSizeOptions.minStackCount;
        Map<ItemStackGrouper, Double> groupedItems = groupItemStacks(itemIterable);

        Optional<Map.Entry<ItemStackGrouper, Double>> selected;

		selected = switch (SimpleShulkerPreviewMod.CONFIGS.displayIcon) {
			case FIRST -> groupedItems.entrySet().stream()
					.filter(entry -> entry.getValue() >= itemThreshold)
                    .findFirst();
			case LAST -> groupedItems.entrySet().stream()
                    .filter(entry -> entry.getValue() >= itemThreshold)
					.reduce((first, second) -> second);
			case UNIQUE -> {
				if (groupedItems.size() != 1) {
					yield Optional.empty(); // More than one type of item in the shulker.
				}

				// Now check if the unique item is above the item limit.
				yield groupedItems.entrySet().stream()
						.filter(entry -> entry.getValue() >= itemThreshold)
						.findFirst();
			}
			case MOST -> groupedItems.entrySet().stream()
					.filter(entry -> entry.getValue() >= itemThreshold)
					.max(Map.Entry.comparingByValue());
			case LEAST -> groupedItems.entrySet().stream()
					.filter(entry -> entry.getValue() >= itemThreshold)
					.min(Map.Entry.comparingByValue());
		};

        return selected.map(Map.Entry::getKey)
                .map(grouper -> grouper.itemStack)
                .orElse(null);
    }

    public static ItemStack getItemFromCustomName(ItemStack itemStack) {
        if(SimpleShulkerPreviewMod.CONFIGS.customName == CustomNameOption.NEVER) return null;

        Component customName = itemStack.getComponents().get(DataComponents.CUSTOM_NAME);
        if(customName == null) return null;

        Identifier itemId = Identifier.tryParse(customName.getString());
        // Note: Invalid custom names still return `minecraft:<customName>`, so we need to check the item instead of the ID
        if(itemId == null) return null;

        Item item = BuiltInRegistries.ITEM.getValue(itemId);
        if(item.equals(Items.AIR)) return null;  // Check for invalid item

        return new ItemStack(item);
    }

    /**
     * Returns the name of a skull.
     *
     * @param itemStack An ItemStack containing a minecraft player head
     * @return A String indicating with the head ID. If missing, returns null.
     */
    private static String getSkullName(ItemStack itemStack) {
        ResolvableProfile profileComponent = itemStack.get(DataComponents.PROFILE);
        if(profileComponent == null) return null;

        return(profileComponent.name().orElse(null));
    }

	/**
	 * Returns the type of potion item, rather than just 'potion'.
	 *
	 * @param itemStack An ItemStack containing a potion
	 * @return A String indicating the potion type. If missing, returns null.
	 */
	private static String getPotionType(ItemStack itemStack) {
		PotionContents potionComponent = itemStack.get(DataComponents.POTION_CONTENTS);
		if(potionComponent == null) return null;

		return potionComponent.getName("").getString();
	}
}
