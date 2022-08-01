package kmaput.sculk_extras.util;

import kmaput.sculk_extras.init.SETags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SculkVeinSpreaderConfigWrapper implements MultifaceSpreader.SpreadConfig {
    private final MultifaceSpreader.SpreadConfig wrapped;

    public SculkVeinSpreaderConfigWrapper(MultifaceSpreader.SpreadConfig wrapped) {
        this.wrapped = wrapped;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return wrapped.getStateForPlacement(state, level, pos, direction);
    }

    @Override
    public boolean canSpreadInto(BlockGetter level, BlockPos pos, MultifaceSpreader.SpreadPos spreadPos) {
        if(level.getBlockState(spreadPos.pos().relative(spreadPos.face())).is(SETags.SCULK_VEIN_IGNORE)) return false;
        return wrapped.canSpreadInto(level, pos, spreadPos);
    }

    @Override
    public MultifaceSpreader.SpreadType[] getSpreadTypes() {
        return wrapped.getSpreadTypes();
    }

    @Override
    public boolean hasFace(BlockState state, Direction direction) {
        return wrapped.hasFace(state, direction);
    }

    @Override
    public boolean isOtherBlockValidAsSource(BlockState state) {
        return wrapped.isOtherBlockValidAsSource(state);
    }

    @Override
    public boolean canSpreadFrom(BlockState state, Direction direction) {
        return wrapped.canSpreadFrom(state, direction);
    }

    @Override
    public boolean placeBlock(LevelAccessor level, MultifaceSpreader.SpreadPos spreadPos, BlockState state, boolean flag) {
        return wrapped.placeBlock(level, spreadPos, state, flag);
    }
}
