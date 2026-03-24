package net.minecraft.world.attribute;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttributeProbe {
   private final Map<EnvironmentAttribute<?>, EnvironmentAttributeProbe.ValueProbe<?>> valueProbes = new Reference2ObjectOpenHashMap();
   private final Function<EnvironmentAttribute<?>, EnvironmentAttributeProbe.ValueProbe<?>> valueProbeFactory = (var1) -> {
      return new EnvironmentAttributeProbe.ValueProbe(var1);
   };
   @Nullable
   Level level;
   @Nullable
   Vec3 position;
   final SpatialAttributeInterpolator biomeInterpolator = new SpatialAttributeInterpolator();

   public EnvironmentAttributeProbe() {
      super();
   }

   public void reset() {
      this.level = null;
      this.position = null;
      this.biomeInterpolator.clear();
      this.valueProbes.clear();
   }

   public void tick(Level var1, Vec3 var2) {
      this.level = var1;
      this.position = var2;
      this.valueProbes.values().removeIf(EnvironmentAttributeProbe.ValueProbe::tick);
      this.biomeInterpolator.clear();
      Vec3 var10000 = var2.scale(0.25D);
      BiomeManager var10001 = var1.getBiomeManager();
      Objects.requireNonNull(var10001);
      GaussianSampler.sample(var10000, var10001::getNoiseBiomeAtQuart, (var1x, var3) -> {
         this.biomeInterpolator.accumulate(var1x, ((Biome)var3.value()).getAttributes());
      });
   }

   public <Value> Value getValue(EnvironmentAttribute<Value> var1, float var2) {
      EnvironmentAttributeProbe.ValueProbe var3 = (EnvironmentAttributeProbe.ValueProbe)this.valueProbes.computeIfAbsent(var1, this.valueProbeFactory);
      return var3.get(var1, var2);
   }

   class ValueProbe<Value> {
      private Value lastValue;
      @Nullable
      private Value newValue;

      public ValueProbe(final EnvironmentAttribute<Value> param2) {
         super();
         Object var3 = this.getValueFromLevel(var2);
         this.lastValue = var3;
         this.newValue = var3;
      }

      private Value getValueFromLevel(EnvironmentAttribute<Value> var1) {
         return EnvironmentAttributeProbe.this.level != null && EnvironmentAttributeProbe.this.position != null ? EnvironmentAttributeProbe.this.level.environmentAttributes().getValue(var1, EnvironmentAttributeProbe.this.position, EnvironmentAttributeProbe.this.biomeInterpolator) : var1.defaultValue();
      }

      public boolean tick() {
         if (this.newValue == null) {
            return true;
         } else {
            this.lastValue = this.newValue;
            this.newValue = null;
            return false;
         }
      }

      public Value get(EnvironmentAttribute<Value> var1, float var2) {
         if (this.newValue == null) {
            this.newValue = this.getValueFromLevel(var1);
         }

         return var1.type().partialTickLerp().apply(var2, this.lastValue, this.newValue);
      }
   }
}
