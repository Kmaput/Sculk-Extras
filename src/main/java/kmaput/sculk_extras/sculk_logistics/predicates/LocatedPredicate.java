package kmaput.sculk_extras.sculk_logistics.predicates;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public record LocatedPredicate<T>(Predicate<T> predicate, ResourceLocation resourceLocation) {}