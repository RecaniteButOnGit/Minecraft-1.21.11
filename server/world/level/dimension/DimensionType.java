package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.timeline.Timeline;

public record DimensionType(boolean hasFixedTime, boolean hasSkyLight, boolean hasCeiling, double coordinateScale, int minY, int height, int logicalHeight, TagKey<Block> infiniburn, float ambientLight, DimensionType.MonsterSettings monsterSettings, DimensionType.Skybox skybox, DimensionType.CardinalLightType cardinalLightType, EnvironmentAttributeMap attributes, HolderSet<Timeline> timelines) {
   public static final int BITS_FOR_Y;
   public static final int MIN_HEIGHT = 16;
   public static final int Y_SIZE;
   public static final int MAX_Y;
   public static final int MIN_Y;
   public static final int WAY_ABOVE_MAX_Y;
   public static final int WAY_BELOW_MIN_Y;
   public static final Codec<DimensionType> DIRECT_CODEC;
   public static final Codec<DimensionType> NETWORK_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DimensionType>> STREAM_CODEC;
   public static final float[] MOON_BRIGHTNESS_PER_PHASE;
   public static final Codec<Holder<DimensionType>> CODEC;

   public DimensionType(boolean param1, boolean param2, boolean param3, double param4, int param6, int param7, int param8, TagKey<Block> param9, float param10, DimensionType.MonsterSettings param11, DimensionType.Skybox param12, DimensionType.CardinalLightType param13, EnvironmentAttributeMap param14, HolderSet<Timeline> param15) {
      super();
      if (var7 < 16) {
         throw new IllegalStateException("height has to be at least 16");
      } else if (var6 + var7 > MAX_Y + 1) {
         throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_Y + 1));
      } else if (var8 > var7) {
         throw new IllegalStateException("logical_height cannot be higher than height");
      } else if (var7 % 16 != 0) {
         throw new IllegalStateException("height has to be multiple of 16");
      } else if (var6 % 16 != 0) {
         throw new IllegalStateException("min_y has to be a multiple of 16");
      } else {
         this.hasFixedTime = var1;
         this.hasSkyLight = var2;
         this.hasCeiling = var3;
         this.coordinateScale = var4;
         this.minY = var6;
         this.height = var7;
         this.logicalHeight = var8;
         this.infiniburn = var9;
         this.ambientLight = var10;
         this.monsterSettings = var11;
         this.skybox = var12;
         this.cardinalLightType = var13;
         this.attributes = var14;
         this.timelines = var15;
      }
   }

   private static Codec<DimensionType> createDirectCodec(Codec<EnvironmentAttributeMap> var0) {
      return ExtraCodecs.catchDecoderException(RecordCodecBuilder.create((var1) -> {
         return var1.group(Codec.BOOL.optionalFieldOf("has_fixed_time", false).forGetter(DimensionType::hasFixedTime), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.doubleRange(9.999999747378752E-6D, 3.0E7D).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.intRange(MIN_Y, MAX_Y).fieldOf("min_y").forGetter(DimensionType::minY), Codec.intRange(16, Y_SIZE).fieldOf("height").forGetter(DimensionType::height), Codec.intRange(0, Y_SIZE).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), TagKey.hashedCodec(Registries.BLOCK).fieldOf("infiniburn").forGetter(DimensionType::infiniburn), Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionType::ambientLight), DimensionType.MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings), DimensionType.Skybox.CODEC.optionalFieldOf("skybox", DimensionType.Skybox.OVERWORLD).forGetter(DimensionType::skybox), DimensionType.CardinalLightType.CODEC.optionalFieldOf("cardinal_light", DimensionType.CardinalLightType.DEFAULT).forGetter(DimensionType::cardinalLightType), var0.optionalFieldOf("attributes", EnvironmentAttributeMap.EMPTY).forGetter(DimensionType::attributes), RegistryCodecs.homogeneousList(Registries.TIMELINE).optionalFieldOf("timelines", HolderSet.empty()).forGetter(DimensionType::timelines)).apply(var1, DimensionType::new);
      }));
   }

   public static double getTeleportationScale(DimensionType var0, DimensionType var1) {
      double var2 = var0.coordinateScale();
      double var4 = var1.coordinateScale();
      return var2 / var4;
   }

   public static Path getStorageFolder(ResourceKey<Level> var0, Path var1) {
      if (var0 == Level.OVERWORLD) {
         return var1;
      } else if (var0 == Level.END) {
         return var1.resolve("DIM1");
      } else {
         return var0 == Level.NETHER ? var1.resolve("DIM-1") : var1.resolve("dimensions").resolve(var0.identifier().getNamespace()).resolve(var0.identifier().getPath());
      }
   }

   public IntProvider monsterSpawnLightTest() {
      return this.monsterSettings.monsterSpawnLightTest();
   }

   public int monsterSpawnBlockLightLimit() {
      return this.monsterSettings.monsterSpawnBlockLightLimit();
   }

   public boolean hasEndFlashes() {
      return this.skybox == DimensionType.Skybox.END;
   }

   public boolean hasFixedTime() {
      return this.hasFixedTime;
   }

   public boolean hasSkyLight() {
      return this.hasSkyLight;
   }

   public boolean hasCeiling() {
      return this.hasCeiling;
   }

   public double coordinateScale() {
      return this.coordinateScale;
   }

   public int minY() {
      return this.minY;
   }

   public int height() {
      return this.height;
   }

   public int logicalHeight() {
      return this.logicalHeight;
   }

   public TagKey<Block> infiniburn() {
      return this.infiniburn;
   }

   public float ambientLight() {
      return this.ambientLight;
   }

   public DimensionType.MonsterSettings monsterSettings() {
      return this.monsterSettings;
   }

   public DimensionType.Skybox skybox() {
      return this.skybox;
   }

   public DimensionType.CardinalLightType cardinalLightType() {
      return this.cardinalLightType;
   }

   public EnvironmentAttributeMap attributes() {
      return this.attributes;
   }

   public HolderSet<Timeline> timelines() {
      return this.timelines;
   }

   static {
      BITS_FOR_Y = BlockPos.PACKED_Y_LENGTH;
      Y_SIZE = (1 << BITS_FOR_Y) - 32;
      MAX_Y = (Y_SIZE >> 1) - 1;
      MIN_Y = MAX_Y - Y_SIZE + 1;
      WAY_ABOVE_MAX_Y = MAX_Y << 4;
      WAY_BELOW_MIN_Y = MIN_Y << 4;
      DIRECT_CODEC = createDirectCodec(EnvironmentAttributeMap.CODEC);
      NETWORK_CODEC = createDirectCodec(EnvironmentAttributeMap.NETWORK_CODEC);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.DIMENSION_TYPE);
      MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
      CODEC = RegistryFileCodec.create(Registries.DIMENSION_TYPE, DIRECT_CODEC);
   }

   public static record MonsterSettings(IntProvider monsterSpawnLightTest, int monsterSpawnBlockLightLimit) {
      public static final MapCodec<DimensionType.MonsterSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(IntProvider.codec(0, 15).fieldOf("monster_spawn_light_level").forGetter(DimensionType.MonsterSettings::monsterSpawnLightTest), Codec.intRange(0, 15).fieldOf("monster_spawn_block_light_limit").forGetter(DimensionType.MonsterSettings::monsterSpawnBlockLightLimit)).apply(var0, DimensionType.MonsterSettings::new);
      });

      public MonsterSettings(IntProvider param1, int param2) {
         super();
         this.monsterSpawnLightTest = var1;
         this.monsterSpawnBlockLightLimit = var2;
      }

      public IntProvider monsterSpawnLightTest() {
         return this.monsterSpawnLightTest;
      }

      public int monsterSpawnBlockLightLimit() {
         return this.monsterSpawnBlockLightLimit;
      }
   }

   public static enum Skybox implements StringRepresentable {
      NONE("none"),
      OVERWORLD("overworld"),
      END("end");

      public static final Codec<DimensionType.Skybox> CODEC = StringRepresentable.fromEnum(DimensionType.Skybox::values);
      private final String name;

      private Skybox(final String param3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static DimensionType.Skybox[] $values() {
         return new DimensionType.Skybox[]{NONE, OVERWORLD, END};
      }
   }

   public static enum CardinalLightType implements StringRepresentable {
      DEFAULT("default"),
      NETHER("nether");

      public static final Codec<DimensionType.CardinalLightType> CODEC = StringRepresentable.fromEnum(DimensionType.CardinalLightType::values);
      private final String name;

      private CardinalLightType(final String param3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static DimensionType.CardinalLightType[] $values() {
         return new DimensionType.CardinalLightType[]{DEFAULT, NETHER};
      }
   }
}
