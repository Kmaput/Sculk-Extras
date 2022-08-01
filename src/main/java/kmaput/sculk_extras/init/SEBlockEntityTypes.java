package kmaput.sculk_extras.init;

import kmaput.sculk_extras.SculkExtras;
import kmaput.sculk_extras.block.cocoon.SculkCocoonBlockEntity;
import kmaput.sculk_extras.block.incubator.EchoingSculkIncubatorBlockEntity;
import kmaput.sculk_extras.block.jaw.EchoingSculkJawBlockEntity;
import kmaput.sculk_extras.block.incubator.SculkIncubatorBlockEntity;
import kmaput.sculk_extras.block.jaw.SculkJawBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class SEBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SculkExtras.MODID);

    public static final RegistryObject<BlockEntityType<SculkJawBlockEntity>> SCULK_JAW = registerBlockEntityType(SEBlocks.SCULK_JAW, SculkJawBlockEntity::create);
    public static final RegistryObject<BlockEntityType<SculkIncubatorBlockEntity>> SCULK_INCUBATOR = registerBlockEntityType(SEBlocks.SCULK_INCUBATOR, SculkIncubatorBlockEntity::create);
    public static final RegistryObject<BlockEntityType<SculkCocoonBlockEntity>> SCULK_COCOON = registerBlockEntityType(SEBlocks.SCULK_COCOON, SculkCocoonBlockEntity::new);
    public static final RegistryObject<BlockEntityType<EchoingSculkJawBlockEntity>> ECHOING_SCULK_JAW = registerBlockEntityType(SEBlocks.ECHOING_SCULK_JAW, EchoingSculkJawBlockEntity::create);
    public static final RegistryObject<BlockEntityType<EchoingSculkIncubatorBlockEntity>> ECHOING_SCULK_INCUBATOR = registerBlockEntityType(SEBlocks.ECHOING_SCULK_INCUBATOR, EchoingSculkIncubatorBlockEntity::create);


    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntityType(RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier) {
        return BLOCK_ENTITY_TYPES.register(block.getId().getPath(), () ->
                BlockEntityType.Builder.of(blockEntitySupplier, block.get()).build(null)
        );
    }
}
