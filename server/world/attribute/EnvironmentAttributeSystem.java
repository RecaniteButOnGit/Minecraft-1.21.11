package net.minecraft.world.attribute;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongSupplier;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.timeline.Timeline;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttributeSystem implements EnvironmentAttributeReader {
   private final Map<EnvironmentAttribute<?>, EnvironmentAttributeSystem.ValueSampler<?>> attributeSamplers = new Reference2ObjectOpenHashMap();

   EnvironmentAttributeSystem(Map<EnvironmentAttribute<?>, List<EnvironmentAttributeLayer<?>>> var1) {
      super();
      var1.forEach((var1x, var2) -> {
         this.attributeSamplers.put(var1x, this.bakeLayerSampler(var1x, var2));
      });
   }

   private <Value> EnvironmentAttributeSystem.ValueSampler<Value> bakeLayerSampler(EnvironmentAttribute<Value> var1, List<? extends EnvironmentAttributeLayer<?>> var2) {
      ArrayList var3 = new ArrayList(var2);
      Object var4 = var1.defaultValue();

      while(!var3.isEmpty()) {
         Object var6 = var3.getFirst();
         if (!(var6 instanceof EnvironmentAttributeLayer.Constant)) {
            break;
         }

         EnvironmentAttributeLayer.Constant var5 = (EnvironmentAttributeLayer.Constant)var6;
         var4 = var5.applyConstant(var4);
         var3.removeFirst();
      }

      boolean var7 = var3.stream().anyMatch((var0) -> {
         return var0 instanceof EnvironmentAttributeLayer.Positional;
      });
      return new EnvironmentAttributeSystem.ValueSampler(var1, var4, List.copyOf(var3), var7);
   }

   public static EnvironmentAttributeSystem.Builder builder() {
      return new EnvironmentAttributeSystem.Builder();
   }

   static void addDefaultLayers(EnvironmentAttributeSystem.Builder var0, Level var1) {
      RegistryAccess var2 = var1.registryAccess();
      BiomeManager var3 = var1.getBiomeManager();
      Objects.requireNonNull(var1);
      LongSupplier var4 = var1::getDayTime;
      addDimensionLayer(var0, var1.dimensionType());
      addBiomeLayer(var0, var2.lookupOrThrow(Registries.BIOME), var3);
      var1.dimensionType().timelines().forEach((var2x) -> {
         var0.addTimelineLayer(var2x, var4);
      });
      if (var1.canHaveWeather()) {
         WeatherAttributes.addBuiltinLayers(var0, WeatherAttributes.WeatherAccess.from(var1));
      }

   }

   private static void addDimensionLayer(EnvironmentAttributeSystem.Builder var0, DimensionType var1) {
      var0.addConstantLayer(var1.attributes());
   }

   private static void addBiomeLayer(EnvironmentAttributeSystem.Builder var0, HolderLookup<Biome> var1, BiomeManager var2) {
      Stream var3 = var1.listElements().flatMap((var0x) -> {
         return ((Biome)var0x.value()).getAttributes().keySet().stream();
      }).distinct();
      var3.forEach((var2x) -> {
         addBiomeLayerForAttribute(var0, var2x, var2);
      });
   }

   private static <Value> void addBiomeLayerForAttribute(EnvironmentAttributeSystem.Builder var0, EnvironmentAttribute<Value> var1, BiomeManager var2) {
      var0.addPositionalLayer(var1, (var2x, var3, var4) -> {
         if (var4 != null && var1.isSpatiallyInterpolated()) {
            return var4.applyAttributeLayer(var1, var2x);
         } else {
            Holder var5 = var2.getNoiseBiomeAtPosition(var3.x, var3.y, var3.z);
            return ((Biome)var5.value()).getAttributes().applyModifier(var1, var2x);
         }
      });
   }

   public void invalidateTickCache() {
      this.attributeSamplers.values().forEach(EnvironmentAttributeSystem.ValueSampler::invalidateTickCache);
   }

   @Nullable
   private <Value> EnvironmentAttributeSystem.ValueSampler<Value> getValueSampler(EnvironmentAttribute<Value> var1) {
      return (EnvironmentAttributeSystem.ValueSampler)this.attributeSamplers.get(var1);
   }

   public <Value> Value getDimensionValue(EnvironmentAttribute<Value> var1) {
      if (SharedConstants.IS_RUNNING_IN_IDE && var1.isPositional()) {
         throw new IllegalStateException("Position must always be provided for positional attribute " + String.valueOf(var1));
      } else {
         EnvironmentAttributeSystem.ValueSampler var2 = this.getValueSampler(var1);
         return var2 == null ? var1.defaultValue() : var2.getDimensionValue();
      }
   }

   public <Value> Value getValue(EnvironmentAttribute<Value> var1, Vec3 var2, @Nullable SpatialAttributeInterpolator var3) {
      EnvironmentAttributeSystem.ValueSampler var4 = this.getValueSampler(var1);
      return var4 == null ? var1.defaultValue() : var4.getValue(var2, var3);
   }

   @VisibleForTesting
   <Value> Value getConstantBaseValue(EnvironmentAttribute<Value> var1) {
      EnvironmentAttributeSystem.ValueSampler var2 = this.getValueSampler(var1);
      return var2 != null ? var2.baseValue : var1.defaultValue();
   }

   @VisibleForTesting
   boolean isAffectedByPosition(EnvironmentAttribute<?> var1) {
      EnvironmentAttributeSystem.ValueSampler var2 = this.getValueSampler(var1);
      return var2 != null && var2.isAffectedByPosition;
   }

   private static class ValueSampler<Value> {
      private final EnvironmentAttribute<Value> attribute;
      final Value baseValue;
      private final List<EnvironmentAttributeLayer<Value>> layers;
      final boolean isAffectedByPosition;
      @Nullable
      private Value cachedTickValue;
      private int cacheTickId;

      ValueSampler(EnvironmentAttribute<Value> var1, Value var2, List<EnvironmentAttributeLayer<Value>> var3, boolean var4) {
         super();
         this.attribute = var1;
         this.baseValue = var2;
         this.layers = var3;
         this.isAffectedByPosition = var4;
      }

      public void invalidateTickCache() {
         this.cachedTickValue = null;
         ++this.cacheTickId;
      }

      public Value getDimensionValue() {
         if (this.cachedTickValue != null) {
            return this.cachedTickValue;
         } else {
            Object var1 = this.computeValueNotPositional();
            this.cachedTickValue = var1;
            return var1;
         }
      }

      public Value getValue(Vec3 var1, @Nullable SpatialAttributeInterpolator var2) {
         return !this.isAffectedByPosition ? this.getDimensionValue() : this.computeValuePositional(var1, var2);
      }

      private Value computeValuePositional(Vec3 var1, @Nullable SpatialAttributeInterpolator var2) {
         Object var3 = this.baseValue;

         Object var10000;
         for(Iterator var4 = this.layers.iterator(); var4.hasNext(); var3 = var10000) {
            EnvironmentAttributeLayer var5 = (EnvironmentAttributeLayer)var4.next();
            Objects.requireNonNull(var5);
            byte var7 = 0;
            switch(var5.typeSwitch<invokedynamic>(var5, var7)) {
            case 0:
               EnvironmentAttributeLayer.Constant var8 = (EnvironmentAttributeLayer.Constant)var5;
               var10000 = (Object)var8.applyConstant(var3);
               break;
            case 1:
               EnvironmentAttributeLayer.TimeBased var9 = (EnvironmentAttributeLayer.TimeBased)var5;
               var10000 = (Object)var9.applyTimeBased(var3, this.cacheTickId);
               break;
            case 2:
               EnvironmentAttributeLayer.Positional var10 = (EnvironmentAttributeLayer.Positional)var5;
               var10000 = (Object)var10.applyPositional(var3, (Vec3)Objects.requireNonNull(var1), var2);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
            }
         }

         return this.attribute.sanitizeValue(var3);
      }

      private Value computeValueNotPositional() {
         Object var1 = this.baseValue;

         Object var10000;
         for(Iterator var2 = this.layers.iterator(); var2.hasNext(); var1 = var10000) {
            EnvironmentAttributeLayer var3 = (EnvironmentAttributeLayer)var2.next();
            Objects.requireNonNull(var3);
            byte var5 = 0;
            switch(var3.typeSwitch<invokedynamic>(var3, var5)) {
            case 0:
               EnvironmentAttributeLayer.Constant var6 = (EnvironmentAttributeLayer.Constant)var3;
               var10000 = (Object)var6.applyConstant(var1);
               break;
            case 1:
               EnvironmentAttributeLayer.TimeBased var7 = (EnvironmentAttributeLayer.TimeBased)var3;
               var10000 = (Object)var7.applyTimeBased(var1, this.cacheTickId);
               break;
            case 2:
               EnvironmentAttributeLayer.Positional var8 = (EnvironmentAttributeLayer.Positional)var3;
               var10000 = (Object)var1;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
            }
         }

         return this.attribute.sanitizeValue(var1);
      }
   }

   public static class Builder {
      private final Map<EnvironmentAttribute<?>, List<EnvironmentAttributeLayer<?>>> layersByAttribute = new HashMap();

      Builder() {
         super();
      }

      public EnvironmentAttributeSystem.Builder addDefaultLayers(Level var1) {
         EnvironmentAttributeSystem.addDefaultLayers(this, var1);
         return this;
      }

      public EnvironmentAttributeSystem.Builder addConstantLayer(EnvironmentAttributeMap var1) {
         Iterator var2 = var1.keySet().iterator();

         while(var2.hasNext()) {
            EnvironmentAttribute var3 = (EnvironmentAttribute)var2.next();
            this.addConstantEntry(var3, var1);
         }

         return this;
      }

      private <Value> EnvironmentAttributeSystem.Builder addConstantEntry(EnvironmentAttribute<Value> var1, EnvironmentAttributeMap var2) {
         EnvironmentAttributeMap.Entry var3 = var2.get(var1);
         if (var3 == null) {
            throw new IllegalArgumentException("Missing attribute " + String.valueOf(var1));
         } else {
            Objects.requireNonNull(var3);
            return this.addConstantLayer(var1, var3::applyModifier);
         }
      }

      public <Value> EnvironmentAttributeSystem.Builder addConstantLayer(EnvironmentAttribute<Value> var1, EnvironmentAttributeLayer.Constant<Value> var2) {
         return this.addLayer(var1, var2);
      }

      public <Value> EnvironmentAttributeSystem.Builder addTimeBasedLayer(EnvironmentAttribute<Value> var1, EnvironmentAttributeLayer.TimeBased<Value> var2) {
         return this.addLayer(var1, var2);
      }

      public <Value> EnvironmentAttributeSystem.Builder addPositionalLayer(EnvironmentAttribute<Value> var1, EnvironmentAttributeLayer.Positional<Value> var2) {
         return this.addLayer(var1, var2);
      }

      private <Value> EnvironmentAttributeSystem.Builder addLayer(EnvironmentAttribute<Value> var1, EnvironmentAttributeLayer<Value> var2) {
         ((List)this.layersByAttribute.computeIfAbsent(var1, (var0) -> {
            return new ArrayList();
         })).add(var2);
         return this;
      }

      public EnvironmentAttributeSystem.Builder addTimelineLayer(Holder<Timeline> var1, LongSupplier var2) {
         Iterator var3 = ((Timeline)var1.value()).attributes().iterator();

         while(var3.hasNext()) {
            EnvironmentAttribute var4 = (EnvironmentAttribute)var3.next();
            this.addTimelineLayerForAttribute(var1, var4, var2);
         }

         return this;
      }

      private <Value> void addTimelineLayerForAttribute(Holder<Timeline> var1, EnvironmentAttribute<Value> var2, LongSupplier var3) {
         this.addTimeBasedLayer(var2, ((Timeline)var1.value()).createTrackSampler(var2, var3));
      }

      public EnvironmentAttributeSystem build() {
         return new EnvironmentAttributeSystem(this.layersByAttribute);
      }
   }
}
