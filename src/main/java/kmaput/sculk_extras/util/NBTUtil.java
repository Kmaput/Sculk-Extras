package kmaput.sculk_extras.util;

import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class NBTUtil {
    public static IntArrayTag saveBlockPos(BlockPos pos) {
        return new IntArrayTag(List.of(pos.getX(), pos.getY(), pos.getZ()));
    }

    public static BlockPos loadBlockPos(int[] array) {
        return new BlockPos(array[0], array[1], array[2]);
    }

    public static Tag saveItem(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null) id = new ResourceLocation("minecraft", "air");
        return StringTag.valueOf(id.toString());
    }

    public static Item loadItem(Tag tag) {
        try {
            if (tag instanceof StringTag stringTag) {
                ResourceLocation id = new ResourceLocation(stringTag.getAsString());
                Item item = ForgeRegistries.ITEMS.getValue(id);
                if (item != null) return item;
            }
        }
        catch (ResourceLocationException ignored) {}
        return Items.AIR;
    }
}
