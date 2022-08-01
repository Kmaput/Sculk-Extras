package kmaput.sculk_extras.client;

import kmaput.sculk_extras.init.SEItems;
import kmaput.sculk_extras.item.EchoShardFragment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;

import java.util.List;

public class EchoShardInventoryOverlay implements DistExecutor.SafeRunnable {
    @Override
    public void run() {
        MinecraftForge.EVENT_BUS.addListener(EchoShardInventoryOverlay::onDrawScreen);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onDrawScreen(ScreenEvent.Render.Post event) {
        Screen screen = Minecraft.getInstance().screen;
        if (Minecraft.getInstance().player != null && screen instanceof AbstractContainerScreen<?> containerScreen) {
            ItemStack carried = containerScreen.getMenu().getCarried();
            if (carried.is(SEItems.ECHO_SHARD_FRAGMENT.get()) && carried.hasTag()) {
                Slot hovered = containerScreen.getSlotUnderMouse();
                if (hovered != null)  {
                    ItemStack hoveredItem = hovered.getItem();
                    if (!hoveredItem.isEmpty()) {
                        boolean matches = EchoShardFragment.getItemPredicate(carried).predicate().test(hovered.getItem());
                        ChatFormatting color = matches ? ChatFormatting.GREEN : ChatFormatting.RED;
                        String message = matches ? "item.sculk_extras.echo_shard_fragment.matching" : "item.sculk_extras.echo_shard_fragment.not_matching";
                        screen.renderComponentTooltip(event.getPoseStack(), List.of(Component.translatable(message).withStyle(color)), event.getMouseX(), event.getMouseY());
                    }
                }
            }
        }
    }
}
