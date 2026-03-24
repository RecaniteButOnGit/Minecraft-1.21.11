package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record BedRule(BedRule.Rule canSleep, BedRule.Rule canSetSpawn, boolean explodes, Optional<Component> errorMessage) {
   public static final BedRule CAN_SLEEP_WHEN_DARK;
   public static final BedRule EXPLODES;
   public static final Codec<BedRule> CODEC;

   public BedRule(BedRule.Rule param1, BedRule.Rule param2, boolean param3, Optional<Component> param4) {
      super();
      this.canSleep = var1;
      this.canSetSpawn = var2;
      this.explodes = var3;
      this.errorMessage = var4;
   }

   public boolean canSleep(Level var1) {
      return this.canSleep.test(var1);
   }

   public boolean canSetSpawn(Level var1) {
      return this.canSetSpawn.test(var1);
   }

   public Player.BedSleepingProblem asProblem() {
      return new Player.BedSleepingProblem((Component)this.errorMessage.orElse((Object)null));
   }

   public BedRule.Rule canSleep() {
      return this.canSleep;
   }

   public BedRule.Rule canSetSpawn() {
      return this.canSetSpawn;
   }

   public boolean explodes() {
      return this.explodes;
   }

   public Optional<Component> errorMessage() {
      return this.errorMessage;
   }

   static {
      CAN_SLEEP_WHEN_DARK = new BedRule(BedRule.Rule.WHEN_DARK, BedRule.Rule.ALWAYS, false, Optional.of(Component.translatable("block.minecraft.bed.no_sleep")));
      EXPLODES = new BedRule(BedRule.Rule.NEVER, BedRule.Rule.NEVER, true, Optional.empty());
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(BedRule.Rule.CODEC.fieldOf("can_sleep").forGetter(BedRule::canSleep), BedRule.Rule.CODEC.fieldOf("can_set_spawn").forGetter(BedRule::canSetSpawn), Codec.BOOL.optionalFieldOf("explodes", false).forGetter(BedRule::explodes), ComponentSerialization.CODEC.optionalFieldOf("error_message").forGetter(BedRule::errorMessage)).apply(var0, BedRule::new);
      });
   }

   public static enum Rule implements StringRepresentable {
      ALWAYS("always"),
      WHEN_DARK("when_dark"),
      NEVER("never");

      public static final Codec<BedRule.Rule> CODEC = StringRepresentable.fromEnum(BedRule.Rule::values);
      private final String name;

      private Rule(final String param3) {
         this.name = var3;
      }

      public boolean test(Level var1) {
         boolean var10000;
         switch(this.ordinal()) {
         case 0:
            var10000 = true;
            break;
         case 1:
            var10000 = var1.isDarkOutside();
            break;
         case 2:
            var10000 = false;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static BedRule.Rule[] $values() {
         return new BedRule.Rule[]{ALWAYS, WHEN_DARK, NEVER};
      }
   }
}
