package kmaput.sculk_extras.init;

import kmaput.sculk_extras.SculkExtras;
import kmaput.sculk_extras.item.EchoShardFragment;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class SEItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SculkExtras.MODID);

    public static final RegistryObject<Item> SCULK_JAW = registerItemBlock(SEBlocks.SCULK_JAW, p -> p.tab(CreativeModeTab.TAB_DECORATIONS));
    public static final RegistryObject<Item> SCULK_INCUBATOR = registerItemBlock(SEBlocks.SCULK_INCUBATOR, p -> p.tab(CreativeModeTab.TAB_DECORATIONS));
    public static final RegistryObject<Item> SCULK_COCOON = registerItemBlock(SEBlocks.SCULK_COCOON, p -> p.tab(CreativeModeTab.TAB_DECORATIONS).stacksTo(1));
    public static final RegistryObject<Item> ECHOING_SCULK_JAW = registerItemBlock(SEBlocks.ECHOING_SCULK_JAW, p -> p.tab(CreativeModeTab.TAB_DECORATIONS));
    public static final RegistryObject<Item> ECHOING_SCULK_INCUBATOR = registerItemBlock(SEBlocks.ECHOING_SCULK_INCUBATOR, p -> p.tab(CreativeModeTab.TAB_DECORATIONS));


    public static final RegistryObject<Item> ECHO_SHARD_FRAGMENT = ITEMS.register("echo_shard_fragment", EchoShardFragment::create);


    private static RegistryObject<Item> registerItemBlock(RegistryObject<Block> block, UnaryOperator<Item.Properties> propertiesConstructor) {
        return ITEMS.register(block.getId().getPath(), () ->
                new BlockItem(block.get(), propertiesConstructor.apply(new Item.Properties()))
        );
    }
}
