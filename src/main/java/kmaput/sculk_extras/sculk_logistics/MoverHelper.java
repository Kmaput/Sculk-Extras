package kmaput.sculk_extras.sculk_logistics;

import kmaput.sculk_extras.util.Directions;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MoverHelper {
    private static final Vec3i[][] NEIGHBOURS;

    static {
        Vec3i[][] builder = new Vec3i[64][];
        List<DirectionConditionedPos> conditionedPositions = BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1)).filter((pos) -> {
            int dist = pos.distManhattan(Vec3i.ZERO);
            return dist == 1 || dist == 2;
        }).map(BlockPos::immutable).map(DirectionConditionedPos::new).toList();
        for (int i = 0; i < 64; i++) {
            ArrayList<Vec3i> elementBuilder = new ArrayList<>();
            Directions directions = Directions.fromMask(i);
            for (DirectionConditionedPos conditionedPosition : conditionedPositions) {
                if (conditionedPosition.check(directions)) {
                    elementBuilder.add(conditionedPosition.pos);
                }
            }
            builder[i] = elementBuilder.toArray(Vec3i[]::new);
        }
        NEIGHBOURS = builder;
    }

    public static List<Vec3i> getShuffledNeighbours(Directions availableSides, RandomSource random) {
        return Util.shuffledCopy(NEIGHBOURS[availableSides.getMask()], random);
    }

    public static void handleEjectItem(SculkItemMover.ItemCursor cursor, Level level, Direction[] validDirections) {
        BlockPos pos = cursor.getPos();
        BlockState state = level.getBlockState(pos);
        Direction foundDirection = null;
        for (Direction dir : Util.shuffledCopy(validDirections, level.random)) {
            BlockPos target = pos.relative(dir);
            if (state.isFaceSturdy(level, pos, dir) && checkSpaceEjectable(level, target, dir.getOpposite())) {
                foundDirection = dir;
                break;
            }
        }
        ejectItem(cursor, level, foundDirection);
    }

    public static void ejectItem(SculkItemMover.ItemCursor cursor, Level level, @Nullable Direction dir) {
        BlockPos pos = cursor.getPos();
        Vec3 spawnPos = getEjectPosition(pos, dir);
        ItemEntity entity = new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, cursor.getItem());
        entity.setDeltaMovement(0, 0, 0);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
        entity.lifespan = cursor.getItemLifespan();
    }

    // TODO this seems computationally expensive for complex blocks, maybe some easier check?
    public static boolean checkSpaceEjectable(Level level, BlockPos targetPos, Direction fromDir) {
        if (!level.isInWorldBounds(targetPos)) return true;
        Vec3 ejectPosition = getEjectPosition(targetPos.relative(fromDir), fromDir.getOpposite());
        AABB ejectSpace = new AABB(ejectPosition, ejectPosition).inflate(0.125);
        VoxelShape collisionShape = level.getBlockState(targetPos).getCollisionShape(level, targetPos);
        return collisionShape.toAabbs().stream().noneMatch(ejectSpace::intersects);
    }

    public static Vec3 getEjectPosition(BlockPos pos, @Nullable Direction dir) {
        Vec3 result = Vec3.atCenterOf(pos).subtract(0, 0.125, 0);
        if (dir != null) {
            Vec3 offset = Vec3.atLowerCornerOf(dir.getNormal());
            result = result.add(offset.scale(0.625));
        }
        return result;
    }

    public static Directions getUnobstructedSides(Level level, BlockPos pos) {
        Directions result = Directions.none();
        for (Direction dir : Direction.values()) {
            if (isUnobstructed(level, pos, dir)) {
                result = result.union(Directions.from(dir));
            }
        }
        return result;
    }

    public static boolean isUnobstructed(Level level, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.relative(direction);
        return !level.getBlockState(blockpos).isFaceSturdy(level, blockpos, direction.getOpposite());
    }

    private static class DirectionConditionedPos {
        BlockPos pos;
        Directions conditions;

        public DirectionConditionedPos(BlockPos offset) {
            this.pos = offset;
            if (offset.distManhattan(Vec3i.ZERO) == 2) {
                Directions builder = Directions.none();
                if (offset.getX() != 0) builder = builder.union(Directions.from(Direction.fromAxisAndDirection(Direction.Axis.X, offset.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE)));
                if (offset.getY() != 0) builder = builder.union(Directions.from(Direction.fromAxisAndDirection(Direction.Axis.Y, offset.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE)));
                if (offset.getZ() != 0) builder = builder.union(Directions.from(Direction.fromAxisAndDirection(Direction.Axis.Z, offset.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE)));
                this.conditions = builder;
            }
            else {
                this.conditions = Directions.none();
            }
        }

        public boolean check(Directions available) {
            return this.pos.distManhattan(Vec3i.ZERO) == 1 || !conditions.intersect(available).equals(Directions.none());
        }
    }
}
