package net.minecraft.client.gui.screens.advancements;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.resources.Identifier;

public enum AdvancementWidgetType {
   OBTAINED(Identifier.withDefaultNamespace("advancements/box_obtained"), Identifier.withDefaultNamespace("advancements/task_frame_obtained"), Identifier.withDefaultNamespace("advancements/challenge_frame_obtained"), Identifier.withDefaultNamespace("advancements/goal_frame_obtained")),
   UNOBTAINED(Identifier.withDefaultNamespace("advancements/box_unobtained"), Identifier.withDefaultNamespace("advancements/task_frame_unobtained"), Identifier.withDefaultNamespace("advancements/challenge_frame_unobtained"), Identifier.withDefaultNamespace("advancements/goal_frame_unobtained"));

   private final Identifier boxSprite;
   private final Identifier taskFrameSprite;
   private final Identifier challengeFrameSprite;
   private final Identifier goalFrameSprite;

   private AdvancementWidgetType(final Identifier param3, final Identifier param4, final Identifier param5, final Identifier param6) {
      this.boxSprite = var3;
      this.taskFrameSprite = var4;
      this.challengeFrameSprite = var5;
      this.goalFrameSprite = var6;
   }

   public Identifier boxSprite() {
      return this.boxSprite;
   }

   public Identifier frameSprite(AdvancementType var1) {
      Identifier var10000;
      switch(var1) {
      case TASK:
         var10000 = this.taskFrameSprite;
         break;
      case CHALLENGE:
         var10000 = this.challengeFrameSprite;
         break;
      case GOAL:
         var10000 = this.goalFrameSprite;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static AdvancementWidgetType[] $values() {
      return new AdvancementWidgetType[]{OBTAINED, UNOBTAINED};
   }
}
