package kmaput.sculk_extras.init;

import kmaput.sculk_extras.SculkExtras;
import kmaput.sculk_extras.block.cocoon.SculkCocoonBlock;
import kmaput.sculk_extras.block.incubator.EchoingSculkIncubatorBlock;
import kmaput.sculk_extras.block.jaw.EchoingSculkJawBlock;
import kmaput.sculk_extras.block.incubator.SculkIncubatorBlock;
import kmaput.sculk_extras.block.jaw.SculkJawBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class SEBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SculkExtras.MODID);

    public static final RegistryObject<Block> SCULK_JAW = BLOCKS.register("sculk_jaw", SculkJawBlock::create);
    public static final RegistryObject<Block> SCULK_INCUBATOR = BLOCKS.register("sculk_incubator", SculkIncubatorBlock::create);
    public static final RegistryObject<Block> SCULK_COCOON = BLOCKS.register("sculk_cocoon", SculkCocoonBlock::create);
    public static final RegistryObject<Block> ECHOING_SCULK_JAW = BLOCKS.register("echoing_sculk_jaw", EchoingSculkJawBlock::create);
    public static final RegistryObject<Block> ECHOING_SCULK_INCUBATOR = BLOCKS.register("echoing_sculk_incubator", EchoingSculkIncubatorBlock::create);

}
