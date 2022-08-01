package kmaput.sculk_extras.init;

import kmaput.sculk_extras.SculkExtras;
import kmaput.sculk_extras.world_gen.JawFeature;
import kmaput.sculk_extras.world_gen.MutatedSculkPatch;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

// TODO find a way to change data/minecraft/worldgen/template_pool/ancient_city/sculk.json without replacing entire file
@SuppressWarnings("unused")
public class SEWorldGen {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, SculkExtras.MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, SculkExtras.MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, SculkExtras.MODID);

    public static final RegistryObject<MutatedSculkPatch> MUTATED_SCULK_PATCH = FEATURES.register("mutated_sculk_patch", () -> new MutatedSculkPatch(SculkPatchConfiguration.CODEC));
    public static final RegistryObject<JawFeature> JAW = FEATURES.register("sculk_jaw", JawFeature::new);

    public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_MUTATED_SCULK_PATCH_ANCIENT_CITY = CONFIGURED_FEATURES.register("mutated_sculk_patch_ancient_city", () -> new ConfiguredFeature<>(MUTATED_SCULK_PATCH.get(), new SculkPatchConfiguration(10, 32, 64, 0, 1, UniformInt.of(1, 3), 0.5f)));
    public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_JAW = CONFIGURED_FEATURES.register("sculk_jaw", () -> new ConfiguredFeature<>(JAW.get(), NoneFeatureConfiguration.NONE));

    public static final RegistryObject<PlacedFeature> PLACED_MUTATED_SCULK_PATCH_ANCIENT_CITY = PLACED_FEATURES.register("mutated_sculk_patch_ancient_city", () -> new PlacedFeature(CONFIGURED_MUTATED_SCULK_PATCH_ANCIENT_CITY.getHolder().get(), List.of()));
    // TODO placing basically hoppers at worldgen to lag your game might be a bad idea, but for now it will stay
    public static final RegistryObject<PlacedFeature> PLACED_JAW = PLACED_FEATURES.register("sculk_jaw", () -> new PlacedFeature(CONFIGURED_JAW.getHolder().get(), List.of(CountPlacement.of(ConstantInt.of(128)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome())));

}
