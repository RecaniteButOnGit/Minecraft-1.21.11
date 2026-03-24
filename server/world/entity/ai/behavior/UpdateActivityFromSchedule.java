package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;

public class UpdateActivityFromSchedule {
   public UpdateActivityFromSchedule() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.point((var0x, var1, var2) -> {
            var1.getBrain().updateActivityFromSchedule(var0x.environmentAttributes(), var0x.getGameTime(), var1.position());
            return true;
         });
      });
   }
}
