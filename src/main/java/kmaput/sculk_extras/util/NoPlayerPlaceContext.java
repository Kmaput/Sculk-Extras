package kmaput.sculk_extras.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class NoPlayerPlaceContext extends BlockPlaceContext {
    protected Direction direction;
    protected BlockPos pos;

    public NoPlayerPlaceContext(Level level, BlockPos pos, Direction direction, ItemStack item) {
        super(level, null, InteractionHand.MAIN_HAND, item, new BlockHitResult(Vec3.atBottomCenterOf(pos), direction, pos, false));
        this.direction = direction;
        this.pos = pos;
    }

    public BlockPos getClickedPos() {
        return this.pos;
    }

    public boolean canPlace() {
        return this.getLevel().getBlockState(this.pos).canBeReplaced(this);
    }

    public boolean replacingClickedOnBlock() {
        return this.canPlace();
    }

    public Direction getNearestLookingDirection() {
        return direction;
    }

    public Direction getNearestLookingVerticalDirection() {
        return this.direction.getAxis() == Direction.Axis.Y ? this.direction : Direction.UP;
    }

    public Direction getHorizontalDirection() {
        return this.direction.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.direction;
    }

    public Direction[] getNearestLookingDirections() {
        return switch (this.direction) {
            case DOWN -> new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN};
            case UP -> new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN};
            case NORTH -> new Direction[]{Direction.NORTH, Direction.UP, Direction.WEST, Direction.DOWN, Direction.EAST, Direction.SOUTH};
            case SOUTH -> new Direction[]{Direction.SOUTH, Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST, Direction.NORTH};
            case WEST -> new Direction[]{Direction.WEST, Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH, Direction.EAST};
            case EAST -> new Direction[]{Direction.EAST, Direction.UP, Direction.SOUTH, Direction.DOWN, Direction.NORTH, Direction.WEST};
        };
    }

    public boolean isSecondaryUseActive() {
        return false;
    }

    public float getRotation() {
        return (float)(this.direction.get2DDataValue() * 90);
    }
}