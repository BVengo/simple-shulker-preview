package com.bvengo.simpleshulkerpreview.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.bvengo.simpleshulkerpreview.access.ShulkerSizeExtension;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin implements ShulkerSizeExtension {
    @Shadow private DefaultedList<ItemStack> inventory;

    public int simple_shulker_preview$getInventorySize() {
        return inventory.size();
    }
}
