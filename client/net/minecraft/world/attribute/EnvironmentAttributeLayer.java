package net.minecraft.world.attribute;

import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface EnvironmentAttributeLayer<Value> {
   @FunctionalInterface
   public interface Positional<Value> extends EnvironmentAttributeLayer<Value> {
      Value applyPositional(Value var1, Vec3 var2, @Nullable SpatialAttributeInterpolator var3);
   }

   @FunctionalInterface
   public interface TimeBased<Value> extends EnvironmentAttributeLayer<Value> {
      Value applyTimeBased(Value var1, int var2);
   }

   @FunctionalInterface
   public interface Constant<Value> extends EnvironmentAttributeLayer<Value> {
      Value applyConstant(Value var1);
   }
}
