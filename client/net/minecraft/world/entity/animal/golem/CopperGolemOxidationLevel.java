package net.minecraft.world.entity.animal.golem;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public record CopperGolemOxidationLevel(SoundEvent spinHeadSound, SoundEvent hurtSound, SoundEvent deathSound, SoundEvent stepSound, Identifier texture, Identifier eyeTexture) {
   public CopperGolemOxidationLevel(SoundEvent param1, SoundEvent param2, SoundEvent param3, SoundEvent param4, Identifier param5, Identifier param6) {
      super();
      this.spinHeadSound = var1;
      this.hurtSound = var2;
      this.deathSound = var3;
      this.stepSound = var4;
      this.texture = var5;
      this.eyeTexture = var6;
   }

   public SoundEvent spinHeadSound() {
      return this.spinHeadSound;
   }

   public SoundEvent hurtSound() {
      return this.hurtSound;
   }

   public SoundEvent deathSound() {
      return this.deathSound;
   }

   public SoundEvent stepSound() {
      return this.stepSound;
   }

   public Identifier texture() {
      return this.texture;
   }

   public Identifier eyeTexture() {
      return this.eyeTexture;
   }
}
