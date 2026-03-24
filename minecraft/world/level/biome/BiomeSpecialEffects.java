package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public record BiomeSpecialEffects(int waterColor, Optional<Integer> foliageColorOverride, Optional<Integer> dryFoliageColorOverride, Optional<Integer> grassColorOverride, BiomeSpecialEffects.GrassColorModifier grassColorModifier) {
   public static final Codec<BiomeSpecialEffects> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.STRING_RGB_COLOR.fieldOf("water_color").forGetter(BiomeSpecialEffects::waterColor), ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf("foliage_color").forGetter(BiomeSpecialEffects::foliageColorOverride), ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf("dry_foliage_color").forGetter(BiomeSpecialEffects::dryFoliageColorOverride), ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf("grass_color").forGetter(BiomeSpecialEffects::grassColorOverride), BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", BiomeSpecialEffects.GrassColorModifier.NONE).forGetter(BiomeSpecialEffects::grassColorModifier)).apply(var0, BiomeSpecialEffects::new);
   });

   public BiomeSpecialEffects(int param1, Optional<Integer> param2, Optional<Integer> param3, Optional<Integer> param4, BiomeSpecialEffects.GrassColorModifier param5) {
      super();
      this.waterColor = var1;
      this.foliageColorOverride = var2;
      this.dryFoliageColorOverride = var3;
      this.grassColorOverride = var4;
      this.grassColorModifier = var5;
   }

   public int waterColor() {
      return this.waterColor;
   }

   public Optional<Integer> foliageColorOverride() {
      return this.foliageColorOverride;
   }

   public Optional<Integer> dryFoliageColorOverride() {
      return this.dryFoliageColorOverride;
   }

   public Optional<Integer> grassColorOverride() {
      return this.grassColorOverride;
   }

   public BiomeSpecialEffects.GrassColorModifier grassColorModifier() {
      return this.grassColorModifier;
   }

   public static enum GrassColorModifier implements StringRepresentable {
      NONE("none") {
         public int modifyColor(double var1, double var3, int var5) {
            return var5;
         }
      },
      DARK_FOREST("dark_forest") {
         public int modifyColor(double var1, double var3, int var5) {
            return (var5 & 16711422) + 2634762 >> 1;
         }
      },
      SWAMP("swamp") {
         public int modifyColor(double var1, double var3, int var5) {
            double var6 = Biome.BIOME_INFO_NOISE.getValue(var1 * 0.0225D, var3 * 0.0225D, false);
            return var6 < -0.1D ? 5011004 : 6975545;
         }
      };

      private final String name;
      public static final Codec<BiomeSpecialEffects.GrassColorModifier> CODEC = StringRepresentable.fromEnum(BiomeSpecialEffects.GrassColorModifier::values);

      public abstract int modifyColor(double var1, double var3, int var5);

      GrassColorModifier(final String param3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static BiomeSpecialEffects.GrassColorModifier[] $values() {
         return new BiomeSpecialEffects.GrassColorModifier[]{NONE, DARK_FOREST, SWAMP};
      }
   }

   public static class Builder {
      private OptionalInt waterColor = OptionalInt.empty();
      private Optional<Integer> foliageColorOverride = Optional.empty();
      private Optional<Integer> dryFoliageColorOverride = Optional.empty();
      private Optional<Integer> grassColorOverride = Optional.empty();
      private BiomeSpecialEffects.GrassColorModifier grassColorModifier;

      public Builder() {
         super();
         this.grassColorModifier = BiomeSpecialEffects.GrassColorModifier.NONE;
      }

      public BiomeSpecialEffects.Builder waterColor(int var1) {
         this.waterColor = OptionalInt.of(var1);
         return this;
      }

      public BiomeSpecialEffects.Builder foliageColorOverride(int var1) {
         this.foliageColorOverride = Optional.of(var1);
         return this;
      }

      public BiomeSpecialEffects.Builder dryFoliageColorOverride(int var1) {
         this.dryFoliageColorOverride = Optional.of(var1);
         return this;
      }

      public BiomeSpecialEffects.Builder grassColorOverride(int var1) {
         this.grassColorOverride = Optional.of(var1);
         return this;
      }

      public BiomeSpecialEffects.Builder grassColorModifier(BiomeSpecialEffects.GrassColorModifier var1) {
         this.grassColorModifier = var1;
         return this;
      }

      public BiomeSpecialEffects build() {
         return new BiomeSpecialEffects(this.waterColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water' color.");
         }), this.foliageColorOverride, this.dryFoliageColorOverride, this.grassColorOverride, this.grassColorModifier);
      }
   }
}
