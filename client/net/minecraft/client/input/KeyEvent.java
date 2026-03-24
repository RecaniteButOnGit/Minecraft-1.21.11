package net.minecraft.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public record KeyEvent(@InputConstants.Value int key, int scancode, @InputWithModifiers.Modifiers int modifiers) implements InputWithModifiers {
   public KeyEvent(@InputConstants.Value int param1, int param2, @InputWithModifiers.Modifiers int param3) {
      super();
      this.key = var1;
      this.scancode = var2;
      this.modifiers = var3;
   }

   public int input() {
      return this.key;
   }

   @InputConstants.Value
   public int key() {
      return this.key;
   }

   public int scancode() {
      return this.scancode;
   }

   @InputWithModifiers.Modifiers
   public int modifiers() {
      return this.modifiers;
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface Action {
   }
}
