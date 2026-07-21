package com.bvengo.simpleshulkerpreview.container;

import java.util.stream.Stream;
import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.bvengo.simpleshulkerpreview.config.CustomNameOption;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import org.apache.commons.lang3.math.Fraction;

public class ContainerManager {
    private final ItemStack containerStack;
    private final DataComponentMap containerComponents;
    private final String containerId;

    private boolean isContainerSupported;
    
    private ContainerType containerType;

    public ContainerManager(ItemStack containerStack) {
        this.containerStack = containerStack;
        this.containerId = containerStack.typeHolder().getRegisteredName();
        this.containerComponents = containerStack.getComponents();
        
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

        Iterable<ItemStack> itemIterable;
        if (containerType == ContainerType.SHULKER_BOX || containerType == ContainerType.OTHER) {
            ItemContainerContents containerComponent = containerStack.get(DataComponents.CONTAINER);
            if (containerComponent == null) return null;
            itemIterable = () -> containerComponent.nonEmptyItemCopyStream().iterator();
        } else if (containerType == ContainerType.BUNDLE) {
            BundleContents bundleComponent = containerStack.get(DataComponents.BUNDLE_CONTENTS);
            if (bundleComponent == null) return null;
            itemIterable = () -> bundleComponent.itemCopyStream().iterator();
        } else {
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
		Fraction capacity = switch (containerType) {
			case SHULKER_BOX, OTHER -> getShulkerCapacity();
			case BUNDLE -> getBundleCapacity();
			default -> Fraction.ZERO;
		};

        // Cap the capacity at 1, in case unsupported large containers are used without using the configs to
        // modify inventory sizes
        if(capacity.compareTo(Fraction.ONE) > 0) {
            capacity = Fraction.ONE;
        }

        return capacity;
    }

    public boolean isSupported() {
        return isContainerSupported;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    private Fraction getShulkerCapacity() {
        ItemContainerContents containerComponent = containerStack.get(DataComponents.CONTAINER);
        if(containerComponent == null) {
//            String msg = String.format("Cannot get container component for container '%s'.", containerId);
//            SimpleShulkerPreviewMod.LOGGER.warn(msg);
            return Fraction.ZERO;
        }

        Fraction maxItems = Fraction.getFraction(SimpleShulkerPreviewMod.CONFIGS.shulkerInventoryOptions.getSize() * 64, 1); // Maximum number of items in the shulker
        Fraction numItems = Fraction.ZERO; // Actual number of items in the shulker

        Stream<ItemStack> nonEmptyItemCopyStream = containerComponent.nonEmptyItemCopyStream();
        Iterable<ItemStack> itemIterable = () -> nonEmptyItemCopyStream.iterator();
        for(ItemStack itemStack : itemIterable) {
        	numItems = numItems.add(ItemStackManager.getItemCountEquivalent(itemStack)); // Adjust by max stack size of item
        }

        return numItems.divideBy(maxItems);
    }

    private Fraction getBundleCapacity() {
        BundleContents bundleComponent = containerStack.get(DataComponents.BUNDLE_CONTENTS);
        return bundleComponent.weight().result().get();
    }

    private void setContainerType() {
        if (containerComponents.has(DataComponents.CONTAINER)) {
            if (containerId.matches("^minecraft:(.*_)?shulker_box$")) {
                containerType = ContainerType.SHULKER_BOX;
            } else {
                containerType = ContainerType.OTHER;
            }
        } else if (containerComponents.has(DataComponents.BUNDLE_CONTENTS)) {
            containerType = ContainerType.BUNDLE;
        } else {
            containerType = ContainerType.NONE;
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
