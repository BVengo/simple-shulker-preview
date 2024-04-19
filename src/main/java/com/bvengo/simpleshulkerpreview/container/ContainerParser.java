package com.bvengo.simpleshulkerpreview.container;

import java.rmi.UnexpectedException;
import com.bvengo.simpleshulkerpreview.config.ConfigOptions;
import com.bvengo.simpleshulkerpreview.config.CustomNameOption;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ContainerParser {
    ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();
    
    private ItemStack containerStack;
    private ComponentMap containerComponents;
    private String containerId;

    boolean hasCustomName;
    boolean isContainerSupported;
    
    ContainerType containerType;
    ContainerContentsType containerContentsType;

    public ContainerParser(ItemStack containerStack) {
        this.containerStack = containerStack;
        this.containerId = containerStack.getRegistryEntry().getIdAsString();
        this.containerComponents = containerStack.getComponents();

        this.hasCustomName = containerComponents.contains(DataComponentTypes.CUSTOM_NAME); // TODO: Change to 'getCustomName'
        
        setContainerContentsType();
        setContainerType();
        setContainerSupported();
    }

    public ItemStack getDisplayStack() throws UnexpectedException {
        if(!isContainerSupported) return null;

        // Use item from custom name (if necessary)
        ItemStack displayStack = getItemFromCustomName();
        
        if(displayStack != null || config.useCustomName == CustomNameOption.ALWAYS) {
            return displayStack;
        }

        // Get items from container
        Iterable<ItemStack> itemIterator;
        switch(containerContentsType) {
            case CONTAINER:
                ContainerComponent containerComponent = containerStack.get(DataComponentTypes.CONTAINER);
                itemIterator = containerComponent.iterateNonEmptyCopy();
                break;
            case BUNDLE:
                BundleContentsComponent bundleComponent = containerStack.get(DataComponentTypes.BUNDLE_CONTENTS);
                itemIterator = bundleComponent.iterateCopy();
                break;
            case NONE:
                throw new UnexpectedException("Item marked as container, but has no container-like components.");
            default:
                return null;
        }

        return getDisplayStackFromList(itemIterator);
    }

    public int getStackSize() {
        return containerStack.getCount();
    }

    public float getCapacity() {
        switch(containerContentsType) {
            case CONTAINER:
                return 1.0f; // TODO: Implement
            case BUNDLE:
                BundleContentsComponent bundleComponent = containerStack.get(DataComponentTypes.BUNDLE_CONTENTS);
                return bundleComponent.getOccupancy().floatValue();
            default:
                return 0.0f;
        }
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

    public ContainerContentsType getContainerContentsType() {
        return containerContentsType;
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

    public ContainerType getContainerType() {
        return containerType;
    }

    private void setContainerSupported() {
        // Check if the container display is enabled (e.g. bundles)
        switch(containerType) {
            case SHULKER_BOX:
                isContainerSupported = true;
                return;
            case BUNDLE:
                isContainerSupported = config.supportBundles;
                return;
            case OTHER:
                isContainerSupported = config.supportOtherContainers;
                return;
            default:
                isContainerSupported = false;
                return;
        }
    }

    public boolean isContainerSupported() {
        return isContainerSupported;
    }

    private ItemStack getItemFromCustomName() {
        if(config.useCustomName == CustomNameOption.NEVER) return null;

        // get custom stack name (should be done in constructor)
        // get item from custom name, or null if it doesn't match an item type

        return null;
    }

    private ItemStack getDisplayStackFromList(Iterable<ItemStack> items) {
        if(!items.iterator().hasNext()) return null;

        return items.iterator().next();

        // TODO: Implement proper item extraction based on configs
    }
}