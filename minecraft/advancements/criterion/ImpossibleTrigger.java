package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.PlayerAdvancements;

public class ImpossibleTrigger implements CriterionTrigger<ImpossibleTrigger.TriggerInstance> {
   public ImpossibleTrigger() {
      super();
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ImpossibleTrigger.TriggerInstance> var2) {
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ImpossibleTrigger.TriggerInstance> var2) {
   }

   public void removePlayerListeners(PlayerAdvancements var1) {
   }

   public Codec<ImpossibleTrigger.TriggerInstance> codec() {
      return ImpossibleTrigger.TriggerInstance.CODEC;
   }

   public static record TriggerInstance() implements CriterionTriggerInstance {
      public static final Codec<ImpossibleTrigger.TriggerInstance> CODEC = MapCodec.unitCodec(new ImpossibleTrigger.TriggerInstance());

      public TriggerInstance() {
         super();
      }

      public void validate(CriterionValidator var1) {
      }
   }
}
