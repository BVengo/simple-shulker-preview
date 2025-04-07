package com.bvengo.simpleshulkerpreview.container;


import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.config.CustomNameOption;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.commons.lang3.math.Fraction;

public class ContainerManager {
    private final ItemStack containerStack;
    private final ComponentMap containerComponents;
    private final String containerId;

    private boolean isContainerSupported;
    
    private ContainerType containerType;
    private ContainerContentsType containerContentsType;

    public ContainerManager(ItemStack containerStack) {
        this.containerStack = containerStack;
        this.containerId = containerStack.getRegistryEntry().getIdAsString();
        this.containerComponents = containerStack.getComponents();
        
        setContainerContentsType();
        setContainerType();
        setContainerSupported();
    }

    public ItemStack getDisplayStack() {
        if(!isContainerSupported) return null;

        // Use item from custom name (if necessary)
        ItemStack displayStack = ItemStackManager.getItemFromCustomName(containerStack);
        if(displayStack != null || SimpleShulkerPreviewMod.CONFIGS.customName == CustomNameOption.ALWAYS) {
            return displayStack;
        }

        // Get items from container
        Iterable<ItemStack> itemIterable;
        switch(containerContentsType) {
            case CONTAINER:
                ContainerComponent containerComponent = containerStack.get(DataComponentTypes.CONTAINER);
                itemIterable = containerComponent.iterateNonEmptyCopy();
                break;
            case BUNDLE:
                BundleContentsComponent bundleComponent = containerStack.get(DataComponentTypes.BUNDLE_CONTENTS);
                itemIterable = bundleComponent.iterateCopy();
                break;
            case NONE:
                // String badContainerId = containerStack.getRegistryEntry().getIdAsString();
                // String msg = String.format("Item %s marked as container, but has no container components.", badContainerId);
                // SimpleShulkerPreviewMod.LOGGER.warn(msg);
            default:
                return null;
        }

        return ItemStackManager.getDisplayStackFromIterable(itemIterable);
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

        Fraction maxItems = Fraction.getFraction(SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryOptions.getSize() * 64, 1); // Maximum number of items in the shulker
        Fraction numItems = Fraction.ZERO; // Actual number of items in the shulker

        Iterable<ItemStack> itemIterable = containerComponent.iterateNonEmpty();
        for(ItemStack itemStack : itemIterable) {
        	numItems = numItems.add(Fraction.getFraction(itemStack.getCount(), itemStack.getItem().getMaxCount()).multiplyBy(Fraction.getFraction(64, 1)));
 // Adjust by max stack size of item
        }

        return numItems.divideBy(maxItems);
    }

    private Fraction getBundleCapacity() {
        BundleContentsComponent bundleComponent = containerStack.get(DataComponentTypes.BUNDLE_CONTENTS);
        return bundleComponent.getOccupancy();
    }

    private void setContainerContentsType() {
        if(containerComponents.contains(DataComponentTypes.CONTAINER)) {
            containerContentsType = ContainerContentsType.CONTAINER;
        } else if(containerComponents.contains(DataComponentTypes.BUNDLE_CONTENTS)) {
            containerContentsType = ContainerContentsType.BUNDLE;
        } else {
            containerContentsType = ContainerContentsType.NONE;
        };
    }

    private void setContainerType() {
        if(containerContentsType == ContainerContentsType.NONE) {
            containerType = ContainerType.NONE;
        } else if (containerId.matches("^minecraft:(.*_)?shulker_box$")) {
            containerType = ContainerType.SHULKER_BOX;
        } else if (containerStack.isOf(Items.BUNDLE)) {
            containerType = ContainerType.BUNDLE;
        } else {
            containerType = ContainerType.OTHER;
        }
    }

    private void setContainerSupported() {
        // Check if the container display is enabled (e.g. bundles)
        switch(containerType) {
            case SHULKER_BOX:
                isContainerSupported = true;
                return;
            case BUNDLE:
                isContainerSupported = SimpleShulkerPreviewMod.CONFIGS.supportBundles;
                return;
            case OTHER:
                isContainerSupported = SimpleShulkerPreviewMod.CONFIGS.supportOtherContainers;
                return;
            default:
                isContainerSupported = false;
                return;
        }
    }
}