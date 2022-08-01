package kmaput.sculk_extras.init;

import kmaput.sculk_extras.SculkExtras;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class SETags {
    public static final TagKey<Block> INCUBATOR_POPPABLE      = createBlockTag("incubator_poppable");
    public static final TagKey<Block> TRASPORTS_ITEM_CHARGE   = createBlockTag("transports_item_charge");
    public static final TagKey<Block> INCUBATOR_ENERGY_SOURCE = createBlockTag("incubator_energy_source");
    public static final TagKey<Block> SCULK_VEIN_IGNORE       = createBlockTag("sculk_vein_ignore");

    public static final TagKey<Item> COCOON_DISALLOWED   = createItemTag("cocoon_disallowed");
    public static final TagKey<Item> INCUBATOR_PLACEABLE = createItemTag("incubator_placeable");


    private static TagKey<Block> createBlockTag(String id) {
        return BlockTags.create(new ResourceLocation(SculkExtras.MODID, id));
    }

    private static TagKey<Item> createItemTag(String id) {
        return ItemTags.create(new ResourceLocation(SculkExtras.MODID, id));
    }
}
