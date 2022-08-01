package kmaput.sculk_extras.init;

import com.google.common.base.Function;
import com.google.common.base.Suppliers;
import kmaput.sculk_extras.SculkExtras;
import kmaput.sculk_extras.sculk_logistics.predicates.*;
import kmaput.sculk_extras.sculk_logistics.predicates.item.*;
import kmaput.sculk_extras.sculk_logistics.predicates.universal.AndPredicate;
import kmaput.sculk_extras.sculk_logistics.predicates.universal.ConstantPredicate;
import kmaput.sculk_extras.sculk_logistics.predicates.universal.NotPredicate;
import kmaput.sculk_extras.sculk_logistics.predicates.universal.OrPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class SEItemPredicateSerializers {
    public static final DeferredRegister<PredicateSerializer<ItemStack>> ITEM_PREDICATE_SERIALIZERS = DeferredRegister.create(SERegistries.ITEM_PREDICATE_SERIALIZERS_NAME, SculkExtras.MODID);

    public static final RegistryObject<PredicateSerializer<ItemStack>> CONSTANT = register("constant", ConstantPredicate::getSerializer);
    public static final RegistryObject<PredicateSerializer<ItemStack>> ITEM_MATCH = register("item", ItemMatchPredicate::getSerializer);
    public static final RegistryObject<PredicateSerializer<ItemStack>> TAG_MATCH = register("tag", ItemTagPredicate::getSerializer);
    public static final RegistryObject<PredicateSerializer<ItemStack>> COUNT = register("count", ItemCountPredicate::getSerializer);
    public static final RegistryObject<PredicateSerializer<ItemStack>> NOT = register("not", NotPredicate::getSerializer);
    public static final RegistryObject<PredicateSerializer<ItemStack>> OR = register("or", OrPredicate::getSerializer);
    public static final RegistryObject<PredicateSerializer<ItemStack>> AND = register("and", AndPredicate::getSerializer);
    public static final RegistryObject<PredicateSerializer<ItemStack>> SCULK_MEMORY = register("sculk_memory", SculkMemoryItemPredicate::getSerializer);

    public static RegistryObject<PredicateSerializer<ItemStack>> register(String id, Function<GlobalPredicateSerializer<ItemStack>, PredicateSerializer<ItemStack>> serializerConstructor) {
        return ITEM_PREDICATE_SERIALIZERS.register(id, Suppliers.compose(serializerConstructor, GlobalPredicateSerializers::item));
    }
}
