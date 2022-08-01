package kmaput.sculk_extras.sculk_crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import kmaput.sculk_extras.init.SEItems;
import kmaput.sculk_extras.util.Trie;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

// TODO maybe try to fit this into datapacks? (or at least make a registry)
public class SculkCrafting {
    private static final Trie<Item, SculkRecipe> RECIPE_TRIE = new Trie<>();

    public static void addRecipe(List<Item> filter, SculkRecipe recipe) {
        RECIPE_TRIE.put(filter, recipe);
    }

    public static void init() {
        SculkCrafting.addRecipe(List.of(SEItems.ECHO_SHARD_FRAGMENT.get(), Items.CHEST), new ItemMatchRecipe(false));
        SculkCrafting.addRecipe(List.of(SEItems.ECHO_SHARD_FRAGMENT.get(), Items.TRAPPED_CHEST), new ItemMatchRecipe(true));
        SculkCrafting.addRecipe(List.of(SEItems.ECHO_SHARD_FRAGMENT.get(), Items.AMETHYST_SHARD), new CopyFragmentRecipe());
        SculkCrafting.addRecipe(List.of(SEItems.ECHO_SHARD_FRAGMENT.get(), Items.LIGHT_WEIGHTED_PRESSURE_PLATE), new ItemCountRecipe());
        SculkCrafting.addRecipe(List.of(Items.SCULK_VEIN, Items.LAPIS_BLOCK), new BlockHatchRecipe(32, BlockHatchRecipe.SCULK_MATERIAL*8, 20, (BlockItem) Items.SCULK_CATALYST));
        SculkCrafting.addRecipe(List.of(Items.SCULK_VEIN, Items.SOUL_SAND), new BlockHatchRecipe(16, BlockHatchRecipe.SCULK_MATERIAL*4, 4, (BlockItem) SEItems.SCULK_INCUBATOR.get()));
        SculkCrafting.addRecipe(List.of(Items.SCULK_VEIN, Items.FEATHER), new BlockHatchRecipe(0, BlockHatchRecipe.SCULK_MATERIAL, 0, (BlockItem) Items.SCULK_SENSOR));
        SculkCrafting.addRecipe(List.of(Items.SCULK_VEIN, Items.GOAT_HORN), new BlockHatchRecipe(8, BlockHatchRecipe.SCULK_MATERIAL, 0, (BlockItem) Items.SCULK_SHRIEKER));
        SculkCrafting.addRecipe(List.of(Items.SCULK_VEIN, Items.IRON_SWORD), new BlockHatchRecipe(8, BlockHatchRecipe.SCULK_MATERIAL*4, 0, (BlockItem) SEItems.SCULK_JAW.get()));
    }

    @Nullable
    public static SculkRecipe getRecipe(List<ItemStack> items) {
        return RECIPE_TRIE.getBestMatching(() -> Streams.concat(Lists.reverse(items).stream().map(ItemStack::getItem), Stream.of(Items.AIR)).iterator());
    }
}
