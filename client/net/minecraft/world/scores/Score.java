package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import org.jspecify.annotations.Nullable;

public class Score implements ReadOnlyScoreInfo {
   private int value;
   private boolean locked = true;
   @Nullable
   private Component display;
   @Nullable
   private NumberFormat numberFormat;

   public Score() {
      super();
   }

   public Score(Score.Packed var1) {
      super();
      this.value = var1.value;
      this.locked = var1.locked;
      this.display = (Component)var1.display.orElse((Object)null);
      this.numberFormat = (NumberFormat)var1.numberFormat.orElse((Object)null);
   }

   public Score.Packed pack() {
      return new Score.Packed(this.value, this.locked, Optional.ofNullable(this.display), Optional.ofNullable(this.numberFormat));
   }

   public int value() {
      return this.value;
   }

   public void value(int var1) {
      this.value = var1;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean var1) {
      this.locked = var1;
   }

   @Nullable
   public Component display() {
      return this.display;
   }

   public void display(@Nullable Component var1) {
      this.display = var1;
   }

   @Nullable
   public NumberFormat numberFormat() {
      return this.numberFormat;
   }

   public void numberFormat(@Nullable NumberFormat var1) {
      this.numberFormat = var1;
   }

   public static record Packed(int value, boolean locked, Optional<Component> display, Optional<NumberFormat> numberFormat) {
      final int value;
      final boolean locked;
      final Optional<Component> display;
      final Optional<NumberFormat> numberFormat;
      public static final MapCodec<Score.Packed> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.INT.optionalFieldOf("Score", 0).forGetter(Score.Packed::value), Codec.BOOL.optionalFieldOf("Locked", false).forGetter(Score.Packed::locked), ComponentSerialization.CODEC.optionalFieldOf("display").forGetter(Score.Packed::display), NumberFormatTypes.CODEC.optionalFieldOf("format").forGetter(Score.Packed::numberFormat)).apply(var0, Score.Packed::new);
      });

      public Packed(int param1, boolean param2, Optional<Component> param3, Optional<NumberFormat> param4) {
         super();
         this.value = var1;
         this.locked = var2;
         this.display = var3;
         this.numberFormat = var4;
      }

      public int value() {
         return this.value;
      }

      public boolean locked() {
         return this.locked;
      }

      public Optional<Component> display() {
         return this.display;
      }

      public Optional<NumberFormat> numberFormat() {
         return this.numberFormat;
      }
   }
}
