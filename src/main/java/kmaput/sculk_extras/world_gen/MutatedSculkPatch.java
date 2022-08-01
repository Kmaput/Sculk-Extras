package kmaput.sculk_extras.world_gen;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kmaput.sculk_extras.SculkExtras;
import kmaput.sculk_extras.block.cocoon.SculkCocoonBlockEntity;
import kmaput.sculk_extras.init.SEBlocks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SculkPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class MutatedSculkPatch extends SculkPatchFeature {
    public static final ResourceLocation DEEP_DARK_COCOON_LOOT = new ResourceLocation(SculkExtras.MODID, "chests/ancient_city_cocoon_loot");

    public MutatedSculkPatch(Codec<SculkPatchConfiguration> configuration) {
        super(configuration);
    }

    public static final ObjectArrayList<Vec3i> ADDITIONAL_SHRIEKER_POSES = Util.make(new ObjectArrayList<>(120), (list) -> {
        BlockPos.betweenClosedStream(new BlockPos(-3, -1, -3), new BlockPos(3, 1, 3))
                .filter((pos) -> !(Math.abs(pos.getX()) <= 1 && Math.abs(pos.getZ()) <= 1))
                .map(BlockPos::immutable).forEach(list::add);
    });

    public static final ObjectArrayList<Vec3i> ADDITIONAL_JAW_POSES = Util.make(new ObjectArrayList<>(72), (list) -> {
        BlockPos.betweenClosedStream(new BlockPos(-4, -1, -4), new BlockPos(4, -1, 4))
                .filter((pos) -> !(Math.abs(pos.getX()) <= 1 && Math.abs(pos.getZ()) <= 1))
                .map(BlockPos::immutable).forEach(list::add);
    });

    @Override
    public boolean place(FeaturePlaceContext<SculkPatchConfiguration> context) {
        if (super.place(context)) {
            WorldGenLevel level = context.level();
            BlockPos pos = context.origin();
            RandomSource random = context.random();

            level.setBlock(pos, SEBlocks.SCULK_COCOON.get().defaultBlockState(), Block.UPDATE_ALL);
            level.setBlock(pos.below(), SEBlocks.SCULK_INCUBATOR.get().defaultBlockState(), Block.UPDATE_ALL);
            if (level.getBlockEntity(pos) instanceof SculkCocoonBlockEntity cocoon) {
                if (level.getServer() != null) {
                    LootTable loottable = level.getServer().getLootTables().get(DEEP_DARK_COCOON_LOOT);
                    LootContext lootContext = new LootContext.Builder(level.getLevel()).withRandom(random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).create(LootContextParamSets.CHEST);
                    loottable.getRandomItems(lootContext).forEach(cocoon::addItem);
                }
            }
            for (Vec3i offset : Util.shuffledCopy(ADDITIONAL_SHRIEKER_POSES, random)) {
                BlockPos target = pos.offset(offset);
                BlockState state = level.getBlockState(target);
                BlockPos floor = target.below();
                if ((state.isAir() || (state.is(Blocks.WATER) && state.getFluidState().isSource())) && level.getBlockState(floor).isCollisionShapeFullBlock(level, floor)) {
                    level.setBlock(target, Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true), Block.UPDATE_ALL);
                    break;
                }
            }
            int jawLimit = random.nextIntBetweenInclusive(3, 5);
            for (Vec3i offset : Util.shuffledCopy(ADDITIONAL_JAW_POSES, random)) {
                BlockPos target = pos.offset(offset);
                if (level.getBlockState(target).is(Blocks.SCULK)) {
                    level.setBlock(target, SEBlocks.SCULK_JAW.get().defaultBlockState(), Block.UPDATE_ALL);
                    jawLimit--;
                    if (jawLimit == 0) break;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
}
