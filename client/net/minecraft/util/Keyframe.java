package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Keyframe<T>(int ticks, T value) {
   public Keyframe(int param1, T param2) {
      super();
      this.ticks = var1;
      this.value = var2;
   }

   public static <T> Codec<Keyframe<T>> codec(Codec<T> var0) {
      return RecordCodecBuilder.create((var1) -> {
         return var1.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks").forGetter(Keyframe::ticks), var0.fieldOf("value").forGetter(Keyframe::value)).apply(var1, Keyframe::new);
      });
   }

   public int ticks() {
      return this.ticks;
   }

   public T value() {
      return this.value;
   }
}
