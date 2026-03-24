package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface FloatModifier<Argument> extends AttributeModifier<Float, Argument> {
   FloatModifier<FloatWithAlpha> ALPHA_BLEND = new FloatModifier<FloatWithAlpha>() {
      public Float apply(Float var1, FloatWithAlpha var2) {
         return Mth.lerp(var2.alpha(), var1, var2.value());
      }

      public Codec<FloatWithAlpha> argumentCodec(EnvironmentAttribute<Float> var1) {
         return FloatWithAlpha.CODEC;
      }

      public LerpFunction<FloatWithAlpha> argumentKeyframeLerp(EnvironmentAttribute<Float> var1) {
         return (var0, var1x, var2) -> {
            return new FloatWithAlpha(Mth.lerp(var0, var1x.value(), var2.value()), Mth.lerp(var0, var1x.alpha(), var2.alpha()));
         };
      }

      // $FF: synthetic method
      public Object apply(final Object param1, final Object param2) {
         return this.apply((Float)var1, (FloatWithAlpha)var2);
      }
   };
   FloatModifier<Float> ADD = Float::sum;
   FloatModifier<Float> SUBTRACT = (var0, var1) -> {
      return var0 - var1;
   };
   FloatModifier<Float> MULTIPLY = (var0, var1) -> {
      return var0 * var1;
   };
   FloatModifier<Float> MINIMUM = Math::min;
   FloatModifier<Float> MAXIMUM = Math::max;

   @FunctionalInterface
   public interface Simple extends FloatModifier<Float> {
      default Codec<Float> argumentCodec(EnvironmentAttribute<Float> var1) {
         return Codec.FLOAT;
      }

      default LerpFunction<Float> argumentKeyframeLerp(EnvironmentAttribute<Float> var1) {
         return LerpFunction.ofFloat();
      }
   }
}
