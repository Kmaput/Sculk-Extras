package kmaput.sculk_extras;

import com.mojang.logging.LogUtils;
import kmaput.sculk_extras.sculk_crafting.SculkCrafting;
import kmaput.sculk_extras.util.SculkVeinSpreaderConfigWrapper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import kmaput.sculk_extras.init.*;

@Mod(SculkExtras.MODID)
public class SculkExtras {
    public static final String MODID = "sculk_extras";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SculkExtras() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(SERegistries::onNewRegistry);
        SEBlocks.BLOCKS.register(modBus);
        SEItems.ITEMS.register(modBus);
        SEBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modBus);
        SEItemPredicateSerializers.ITEM_PREDICATE_SERIALIZERS.register(modBus);
        SEWorldGen.FEATURES.register(modBus);
        SEWorldGen.CONFIGURED_FEATURES.register(modBus);
        SEWorldGen.PLACED_FEATURES.register(modBus);
        SEEventListeners.init();

        modBus.addListener(this::onSetup);
    }

    public void onSetup(FMLCommonSetupEvent event) {
        SculkCrafting.init();
        // Using access transformer to make a wrapper around Sculk Vein spreader
        if (Blocks.SCULK_VEIN instanceof SculkVeinBlock veins) {
            veins.getSpreader().config = new SculkVeinSpreaderConfigWrapper(veins.getSpreader().config);
            veins.getSameSpaceSpreader().config = new SculkVeinSpreaderConfigWrapper(veins.getSameSpaceSpreader().config);
        }
    }
}
