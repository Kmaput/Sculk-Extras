package kmaput.sculk_extras.world_gen;

import kmaput.sculk_extras.block.jaw.SculkJawBlock;
import kmaput.sculk_extras.init.SEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class JawFeature extends Feature<NoneFeatureConfiguration> {
    public JawFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        for (Direction direction : Direction.allShuffled(context.random())) {
            BlockPos targeted = origin.relative(direction.getOpposite());
            BlockPos pos = origin.relative(direction);
            if (level.getBlockState(pos).is(Blocks.SCULK) && level.getBlockState(targeted).isAir()) {
                level.setBlock(pos, SEBlocks.SCULK_JAW.get().defaultBlockState().setValue(SculkJawBlock.FACING, direction.getOpposite()), Block.UPDATE_ALL);
                return true;
            }
        }
        return false;
    }
}
