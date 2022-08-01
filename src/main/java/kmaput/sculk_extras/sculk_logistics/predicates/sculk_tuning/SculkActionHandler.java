package kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SculkActionHandler<T> {
    public static final int ACTION_TIMEOUT = 600;

    private SculkAction<T> last = null;
    private long lastActionTimestamp;

    private SculkAction<T> current = new SculkAction<>(new ArrayList<>(), SculkAction.Type.ACTION);
    private long currentActionTimestamp;

    public SculkAction<T> getLastAction(long timestamp) {
        if (timestamp != currentActionTimestamp) {
            last = current;
            lastActionTimestamp = currentActionTimestamp;
        }
        if (timestamp-lastActionTimestamp >= ACTION_TIMEOUT) last = null;
        return last;
    }

    public void clear() {
        this.last = this.current = null;
    }

    public void executeAction(T target, SculkAction.Type type, long timestamp) {
        if (timestamp != currentActionTimestamp || current == null) {
            last = current;
            lastActionTimestamp = currentActionTimestamp;
            currentActionTimestamp = timestamp;
            current = new SculkAction<>(new ArrayList<>(), type);
        }
        if (current.type() == SculkAction.Type.IGNORE && type == SculkAction.Type.ACTION) {
            current = new SculkAction<>(new ArrayList<>(), type);
        }
        if (current.type() == type) {
            current.targets().add(target);
        }
    }

    public static <T> CompoundTag save(SculkActionHandler<T> handler, Function<T, Tag> serializer) {
        CompoundTag tag = new CompoundTag();
        if (handler.current != null) {
            tag.putLong("actionTimestamp", handler.currentActionTimestamp);
            tag.putByte("type", (byte)handler.current.type().ordinal());
            ListTag targetsList = new ListTag();
            for (T target : handler.current.targets()) {
                targetsList.add(serializer.apply(target));
            }
            tag.put("targets", targetsList);
        }
        return tag;
    }

    public static <T> SculkActionHandler<T> load(CompoundTag tag, Function<Tag, T> deserializer, int tagType) {
        SculkActionHandler<T> handler = new SculkActionHandler<>();
        if (tag.contains("targets")) {
            handler.lastActionTimestamp = handler.currentActionTimestamp = tag.getLong("actionTimestamp");
            SculkAction.Type type = SculkAction.Type.values()[tag.getByte("type")];
            ListTag targetsTag = tag.getList("targets", tagType);
            List<T> targets = new ArrayList<>();
            for (Tag value : targetsTag) {
                targets.add(deserializer.apply(value));
            }
            handler.last = handler.current = new SculkAction<>(targets, type);
        }
        return handler;
    }
}
