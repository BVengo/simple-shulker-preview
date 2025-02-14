package com.bvengo.simpleshulkerpreview.container;


import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.enums.CustomNameOption;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.commons.lang3.math.Fraction;

import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class ContainerManager {
    private final ItemStack containerStack;

	private final boolean isContainerSupported;
    
    private final ContainerType containerType;
    private final ContainerContentsType containerContentsType;

    private ItemStack currentDisplayStack;
    private Instant lastUpdated;
    private final AtomicInteger cycleIndex = new AtomicInteger(0);

    public ContainerManager(ItemStack containerStack) {
        this.containerStack = containerStack;

        String containerId = containerStack.getRegistryEntry().getIdAsString();
		ComponentMap containerComponents = containerStack.getComponents();
        
        containerContentsType = getContainerContentsType(containerComponents);
        containerType = getContainerType(containerContentsType, containerId, containerStack);
        isContainerSupported = getContainerSupported(containerType);

        this.lastUpdated = Instant.now();
        this.currentDisplayStack = findDisplayStack();
    }

    public static boolean isContainer(ItemStack stack) {
        ContainerContentsType containerContentsType = getContainerContentsType(stack.getComponents());
        return containerContentsType != ContainerContentsType.NONE;
    }

    public ItemStack getDisplayStack() {
        if (!isContainerSupported) return null;

        if (Duration.between(lastUpdated, Instant.now()).getSeconds() >= 1) {
            this.currentDisplayStack = findDisplayStack();
            this.lastUpdated = Instant.now();
        }

        return currentDisplayStack;
    }

    private ItemStack findDisplayStack() {
        // Use item from custom name (if necessary)
        ItemStack displayStack = ItemStackManager.getItemFromCustomName(containerStack);
        if (displayStack != null || SimpleShulkerPreviewMod.CONFIGS.customName == CustomNameOption.ALWAYS) {
            return displayStack;
        }

        // Get items from container
        Iterable<ItemStack> itemIterable;
        switch (containerContentsType) {
            case CONTAINER:
                ContainerComponent containerComponent = containerStack.get(DataComponentTypes.CONTAINER);
                itemIterable = containerComponent.iterateNonEmptyCopy();
                break;
            case BUNDLE:
                BundleContentsComponent bundleComponent = containerStack.get(DataComponentTypes.BUNDLE_CONTENTS);
                itemIterable = bundleComponent.iterateCopy();
                break;
            case NONE:
            default:
                return null;
        }

        return ItemStackManager.getDisplayStackFromIterable(itemIterable, cycleIndex);
    }

    public int getStackSize() {
        return containerStack.getCount();
    }

    /**
     * Returns the ratio full that a container is.
     * @return A float between 0 and 1 indicating how full the container is
     */
    public Fraction getCapacity() {
		Fraction capacity = switch (containerContentsType) {
			case CONTAINER -> getContainerCapacity();
			case BUNDLE -> getBundleCapacity();
			default ->
				// String msg = String.format("Cannot get capacity of container '%s' with no contents type.", containerId);
				// SimpleShulkerPreviewMod.LOGGER.warn(msg);
					Fraction.ZERO;
		};

        // Cap the capacity at 1, in case unsupported large containers are used without using the configs to
        // modify inventory sizes
        if(capacity.compareTo(Fraction.ONE) > 0) {
            capacity = Fraction.ONE;
        }

        return capacity;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    private Fraction getContainerCapacity() {
        if(containerType != ContainerType.SHULKER_BOX) {
            // String msg = String.format("Cannot get maximum inventory size of the container '%s'.", containerId);
            // SimpleShulkerPreviewMod.LOGGER.warn(msg);
            return Fraction.ZERO;
        }

        return getShulkerCapacity();
    }

    private Fraction getShulkerCapacity() {
        ContainerComponent containerComponent = containerStack.get(DataComponentTypes.CONTAINER);
        if(containerComponent == null) {
//            String msg = String.format("Cannot get container component for container '%s'.", containerId);
//            SimpleShulkerPreviewMod.LOGGER.warn(msg);
            return Fraction.ZERO;
        }

        int maxItems = SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryRows * SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryCols * 64; // Maximum number of items in the shulker
        int numItems = 0; // Actual number of items in the shulker

        Iterable<ItemStack> itemIterable = containerComponent.iterateNonEmpty();
        for(ItemStack itemStack : itemIterable) {
            numItems += itemStack.getCount();
            maxItems += itemStack.getItem().getMaxCount() - 64; // Replace the previous 64 with the actual max size of the item
        }

        return Fraction.getFraction(numItems, maxItems);
    }

    private Fraction getBundleCapacity() {
        BundleContentsComponent bundleComponent = containerStack.get(DataComponentTypes.BUNDLE_CONTENTS);
        return bundleComponent.getOccupancy();
    }

    private static ContainerContentsType getContainerContentsType(ComponentMap containerComponents) {
        if(containerComponents.contains(DataComponentTypes.CONTAINER)) {
            return ContainerContentsType.CONTAINER;
        } else if(containerComponents.contains(DataComponentTypes.BUNDLE_CONTENTS)) {
            return ContainerContentsType.BUNDLE;
        } else {
            return ContainerContentsType.NONE;
        }
    }

    private static ContainerType getContainerType(ContainerContentsType containerContentsType, String containerId, ItemStack containerStack) {
        if(containerContentsType == ContainerContentsType.NONE) {
            return ContainerType.NONE;
        } else if (containerId.matches("^minecraft:(.*_)?shulker_box$")) {
            return ContainerType.SHULKER_BOX;
        } else if (containerStack.isOf(Items.BUNDLE)) {
            return ContainerType.BUNDLE;
        } else {
            return ContainerType.OTHER;
        }
    }

    private static boolean getContainerSupported(ContainerType containerType) {
        // Check if the container display is enabled (e.g. bundles)
        switch(containerType) {
            case SHULKER_BOX:
                return true;
            case BUNDLE:
                return SimpleShulkerPreviewMod.CONFIGS.supportBundles;
            case OTHER:
                return SimpleShulkerPreviewMod.CONFIGS.supportOtherContainers;
            default:
                return false;
        }
    }
}