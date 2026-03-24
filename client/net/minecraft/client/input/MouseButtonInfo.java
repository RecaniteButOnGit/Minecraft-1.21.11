package net.minecraft.client.input;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public record MouseButtonInfo(@MouseButtonInfo.MouseButton int button, @InputWithModifiers.Modifiers int modifiers) implements InputWithModifiers {
   public MouseButtonInfo(@MouseButtonInfo.MouseButton int param1, @InputWithModifiers.Modifiers int param2) {
      super();
      this.button = var1;
      this.modifiers = var2;
   }

   @MouseButtonInfo.MouseButton
   public int input() {
      return this.button;
   }

   @MouseButtonInfo.MouseButton
   public int button() {
      return this.button;
   }

   @InputWithModifiers.Modifiers
   public int modifiers() {
      return this.modifiers;
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface MouseButton {
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface Action {
   }
}
