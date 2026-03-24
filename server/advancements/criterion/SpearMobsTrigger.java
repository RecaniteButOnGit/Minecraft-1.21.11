package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

public class SpearMobsTrigger extends SimpleCriterionTrigger<SpearMobsTrigger.TriggerInstance> {
   public SpearMobsTrigger() {
      super();
   }

   public Codec<SpearMobsTrigger.TriggerInstance> codec() {
      return SpearMobsTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, int var2) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var2);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Integer> count) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<SpearMobsTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SpearMobsTrigger.TriggerInstance::player), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count").forGetter(SpearMobsTrigger.TriggerInstance::count)).apply(var0, SpearMobsTrigger.TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> param1, Optional<Integer> param2) {
         super();
         this.player = var1;
         this.count = var2;
      }

      public static Criterion<SpearMobsTrigger.TriggerInstance> spearMobs(int var0) {
         return CriteriaTriggers.SPEAR_MOBS_TRIGGER.createCriterion(new SpearMobsTrigger.TriggerInstance(Optional.empty(), Optional.of(var0)));
      }

      public boolean matches(int var1) {
         return this.count.isEmpty() || var1 >= (Integer)this.count.get();
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<Integer> count() {
         return this.count;
      }
   }
}
