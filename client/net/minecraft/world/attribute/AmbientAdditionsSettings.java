package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public record AmbientAdditionsSettings(Holder<SoundEvent> soundEvent, double tickChance) {
   public static final Codec<AmbientAdditionsSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(SoundEvent.CODEC.fieldOf("sound").forGetter((var0x) -> {
         return var0x.soundEvent;
      }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((var0x) -> {
         return var0x.tickChance;
      })).apply(var0, AmbientAdditionsSettings::new);
   });

   public AmbientAdditionsSettings(Holder<SoundEvent> param1, double param2) {
      super();
      this.soundEvent = var1;
      this.tickChance = var2;
   }

   public Holder<SoundEvent> soundEvent() {
      return this.soundEvent;
   }

   public double tickChance() {
      return this.tickChance;
   }
}
