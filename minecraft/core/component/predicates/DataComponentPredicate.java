package net.minecraft.core.component.predicates;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DataComponentPredicate {
   Codec<Map<DataComponentPredicate.Type<?>, DataComponentPredicate>> CODEC = Codec.dispatchedMap(DataComponentPredicate.Type.CODEC, DataComponentPredicate.Type::codec);
   StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Single<?>> SINGLE_STREAM_CODEC = DataComponentPredicate.Type.STREAM_CODEC.dispatch(DataComponentPredicate.Single::type, DataComponentPredicate.Type::singleStreamCodec);
   StreamCodec<RegistryFriendlyByteBuf, Map<DataComponentPredicate.Type<?>, DataComponentPredicate>> STREAM_CODEC = SINGLE_STREAM_CODEC.apply(ByteBufCodecs.list(64)).map((var0) -> {
      return (Map)var0.stream().collect(Collectors.toMap(DataComponentPredicate.Single::type, DataComponentPredicate.Single::predicate));
   }, (var0) -> {
      return var0.entrySet().stream().map(DataComponentPredicate.Single::fromEntry).toList();
   });

   static MapCodec<DataComponentPredicate.Single<?>> singleCodec(String var0) {
      return DataComponentPredicate.Type.CODEC.dispatchMap(var0, DataComponentPredicate.Single::type, DataComponentPredicate.Type::wrappedCodec);
   }

   boolean matches(DataComponentGetter var1);

   public interface Type<T extends DataComponentPredicate> {
      Codec<DataComponentPredicate.Type<?>> CODEC = Codec.either(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec(), BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec()).xmap(DataComponentPredicate.Type::copyOrCreateType, DataComponentPredicate.Type::unpackType);
      StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Type<?>> STREAM_CODEC = ByteBufCodecs.either(ByteBufCodecs.registry(Registries.DATA_COMPONENT_PREDICATE_TYPE), ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE)).map(DataComponentPredicate.Type::copyOrCreateType, DataComponentPredicate.Type::unpackType);

      private static <T extends DataComponentPredicate.Type<?>> Either<T, DataComponentType<?>> unpackType(T var0) {
         Either var10000;
         if (var0 instanceof DataComponentPredicate.AnyValueType) {
            DataComponentPredicate.AnyValueType var1 = (DataComponentPredicate.AnyValueType)var0;
            var10000 = Either.right(var1.componentType());
         } else {
            var10000 = Either.left(var0);
         }

         return var10000;
      }

      private static DataComponentPredicate.Type<?> copyOrCreateType(Either<DataComponentPredicate.Type<?>, DataComponentType<?>> var0) {
         return (DataComponentPredicate.Type)var0.map((var0x) -> {
            return var0x;
         }, DataComponentPredicate.AnyValueType::create);
      }

      Codec<T> codec();

      MapCodec<DataComponentPredicate.Single<T>> wrappedCodec();

      StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Single<T>> singleStreamCodec();
   }

   public static record Single<T extends DataComponentPredicate>(DataComponentPredicate.Type<T> type, T predicate) {
      public Single(DataComponentPredicate.Type<T> param1, T param2) {
         super();
         this.type = var1;
         this.predicate = var2;
      }

      static <T extends DataComponentPredicate> MapCodec<DataComponentPredicate.Single<T>> wrapCodec(DataComponentPredicate.Type<T> var0, Codec<T> var1) {
         return RecordCodecBuilder.mapCodec((var2) -> {
            return var2.group(var1.fieldOf("value").forGetter(DataComponentPredicate.Single::predicate)).apply(var2, (var1x) -> {
               return new DataComponentPredicate.Single(var0, var1x);
            });
         });
      }

      private static <T extends DataComponentPredicate> DataComponentPredicate.Single<T> fromEntry(Entry<DataComponentPredicate.Type<?>, T> var0) {
         return new DataComponentPredicate.Single((DataComponentPredicate.Type)var0.getKey(), (DataComponentPredicate)var0.getValue());
      }

      public DataComponentPredicate.Type<T> type() {
         return this.type;
      }

      public T predicate() {
         return this.predicate;
      }
   }

   public static final class AnyValueType extends DataComponentPredicate.TypeBase<AnyValue> {
      private final AnyValue predicate;

      public AnyValueType(AnyValue var1) {
         super(MapCodec.unitCodec(var1));
         this.predicate = var1;
      }

      public AnyValue predicate() {
         return this.predicate;
      }

      public DataComponentType<?> componentType() {
         return this.predicate.type();
      }

      public static DataComponentPredicate.AnyValueType create(DataComponentType<?> var0) {
         return new DataComponentPredicate.AnyValueType(new AnyValue(var0));
      }
   }

   public static final class ConcreteType<T extends DataComponentPredicate> extends DataComponentPredicate.TypeBase<T> {
      public ConcreteType(Codec<T> var1) {
         super(var1);
      }
   }

   public abstract static class TypeBase<T extends DataComponentPredicate> implements DataComponentPredicate.Type<T> {
      private final Codec<T> codec;
      private final MapCodec<DataComponentPredicate.Single<T>> wrappedCodec;
      private final StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Single<T>> singleStreamCodec;

      public TypeBase(Codec<T> var1) {
         super();
         this.codec = var1;
         this.wrappedCodec = DataComponentPredicate.Single.wrapCodec(this, var1);
         this.singleStreamCodec = ByteBufCodecs.fromCodecWithRegistries(var1).map((var1x) -> {
            return new DataComponentPredicate.Single(this, var1x);
         }, DataComponentPredicate.Single::predicate);
      }

      public Codec<T> codec() {
         return this.codec;
      }

      public MapCodec<DataComponentPredicate.Single<T>> wrappedCodec() {
         return this.wrappedCodec;
      }

      public StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Single<T>> singleStreamCodec() {
         return this.singleStreamCodec;
      }
   }
}
