package red.jackf.chesttracker_adapted.memory;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

@Environment(EnvType.CLIENT)
public record LightweightStack(Item item, @Nullable NbtCompound tag) {
    public LightweightStack(Item item, @Nullable NbtCompound tag) {
        this.item = item;
        this.tag = tag;
    }
}
