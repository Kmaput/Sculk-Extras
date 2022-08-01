package kmaput.sculk_extras.init;

import kmaput.sculk_extras.client.EchoShardInventoryOverlay;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkPunishable;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;

public class SEEventListeners {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(SEEventListeners::punishSculk);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, EchoShardInventoryOverlay::new);
    }

    public static void punishSculk(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) return;
        InteractionResult result = SculkPunishable.punish(event.getLevel(), event.getPos(), event.getEntity(), event.getItemStack(), event.getHitVec());
        if (result == InteractionResult.SUCCESS) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            event.setUseItem(Event.Result.DENY);
        }
    }
}
