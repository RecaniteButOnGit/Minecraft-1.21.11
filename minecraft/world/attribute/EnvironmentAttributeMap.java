package net.minecraft.world.attribute;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import org.jspecify.annotations.Nullable;

public final class EnvironmentAttributeMap {
   public static final EnvironmentAttributeMap EMPTY = new EnvironmentAttributeMap(Map.of());
   public static final Codec<EnvironmentAttributeMap> CODEC = Codec.lazyInitialized(() -> {
      return Codec.dispatchedMap(EnvironmentAttributes.CODEC, Util.memoize(EnvironmentAttributeMap.Entry::createCodec)).xmap(EnvironmentAttributeMap::new, (var0) -> {
         return var0.entries;
      });
   });
   public static final Codec<EnvironmentAttributeMap> NETWORK_CODEC;
   public static final Codec<EnvironmentAttributeMap> CODEC_ONLY_POSITIONAL;
   final Map<EnvironmentAttribute<?>, EnvironmentAttributeMap.Entry<?, ?>> entries;

   private static EnvironmentAttributeMap filterSyncable(EnvironmentAttributeMap var0) {
      return new EnvironmentAttributeMap(Map.copyOf(Maps.filterKeys(var0.entries, EnvironmentAttribute::isSyncable)));
   }

   EnvironmentAttributeMap(Map<EnvironmentAttribute<?>, EnvironmentAttributeMap.Entry<?, ?>> var1) {
      super();
      this.entries = var1;
   }

   public static EnvironmentAttributeMap.Builder builder() {
      return new EnvironmentAttributeMap.Builder();
   }

   @Nullable
   public <Value> EnvironmentAttributeMap.Entry<Value, ?> get(EnvironmentAttribute<Value> var1) {
      return (EnvironmentAttributeMap.Entry)this.entries.get(var1);
   }

   public <Value> Value applyModifier(EnvironmentAttribute<Value> var1, Value var2) {
      EnvironmentAttributeMap.Entry var3 = this.get(var1);
      return var3 != null ? var3.applyModifier(var2) : var2;
   }

   public boolean contains(EnvironmentAttribute<?> var1) {
      return this.entries.containsKey(var1);
   }

   public Set<EnvironmentAttribute<?>> keySet() {
      return this.entries.keySet();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof EnvironmentAttributeMap) {
            EnvironmentAttributeMap var2 = (EnvironmentAttributeMap)var1;
            if (this.entries.equals(var2.entries)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.entries.hashCode();
   }

   public String toString() {
      return this.entries.toString();
   }

   static {
      NETWORK_CODEC = CODEC.xmap(EnvironmentAttributeMap::filterSyncable, EnvironmentAttributeMap::filterSyncable);
      CODEC_ONLY_POSITIONAL = CODEC.validate((var0) -> {
         List var1 = var0.keySet().stream().filter((var0x) -> {
            return !var0x.isPositional();
         }).toList();
         return !var1.isEmpty() ? DataResult.error(() -> {
            return "The following attributes cannot be positional: " + String.valueOf(var1);
         }) : DataResult.success(var0);
      });
   }

   public static class Builder {
      private final Map<EnvironmentAttribute<?>, EnvironmentAttributeMap.Entry<?, ?>> entries = new HashMap();

      Builder() {
         super();
      }

      public EnvironmentAttributeMap.Builder putAll(EnvironmentAttributeMap var1) {
         this.entries.putAll(var1.entries);
         return this;
      }

      public <Value, Parameter> EnvironmentAttributeMap.Builder modify(EnvironmentAttribute<Value> var1, AttributeModifier<Value, Parameter> var2, Parameter var3) {
         var1.type().checkAllowedModifier(var2);
         this.entries.put(var1, new EnvironmentAttributeMap.Entry(var3, var2));
         return this;
      }

      public <Value> EnvironmentAttributeMap.Builder set(EnvironmentAttribute<Value> var1, Value var2) {
         return this.modify(var1, AttributeModifier.override(), var2);
      }

      public EnvironmentAttributeMap build() {
         return this.entries.isEmpty() ? EnvironmentAttributeMap.EMPTY : new EnvironmentAttributeMap(Map.copyOf(this.entries));
      }
   }

   public static record Entry<Value, Argument>(Argument argument, AttributeModifier<Value, Argument> modifier) {
      public Entry(Argument param1, AttributeModifier<Value, Argument> param2) {
         super();
         this.argument = var1;
         this.modifier = var2;
      }

      private static <Value> Codec<EnvironmentAttributeMap.Entry<Value, ?>> createCodec(EnvironmentAttribute<Value> var0) {
         Codec var1 = var0.type().modifierCodec().dispatch("modifier", EnvironmentAttributeMap.Entry::modifier, Util.memoize((var1x) -> {
            return createFullCodec(var0, var1x);
         }));
         return Codec.either(var0.valueCodec(), var1).xmap((var0x) -> {
            return (EnvironmentAttributeMap.Entry)var0x.map((var0) -> {
               return new EnvironmentAttributeMap.Entry(var0, AttributeModifier.override());
            }, (var0) -> {
               return var0;
            });
         }, (var0x) -> {
            return var0x.modifier == AttributeModifier.override() ? Either.left(var0x.argument()) : Either.right(var0x);
         });
      }

      private static <Value, Argument> MapCodec<EnvironmentAttributeMap.Entry<Value, Argument>> createFullCodec(EnvironmentAttribute<Value> var0, AttributeModifier<Value, Argument> var1) {
         return RecordCodecBuilder.mapCodec((var2) -> {
            return var2.group(var1.argumentCodec(var0).fieldOf("argument").forGetter(EnvironmentAttributeMap.Entry::argument)).apply(var2, (var1x) -> {
               return new EnvironmentAttributeMap.Entry(var1x, var1);
            });
         });
      }

      public Value applyModifier(Value var1) {
         return this.modifier.apply(var1, this.argument);
      }

      public Argument argument() {
         return this.argument;
      }

      public AttributeModifier<Value, Argument> modifier() {
         return this.modifier;
      }
   }
}
