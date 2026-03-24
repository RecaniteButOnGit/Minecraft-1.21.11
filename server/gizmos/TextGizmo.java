package net.minecraft.gizmos;

import java.util.OptionalDouble;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

public record TextGizmo(Vec3 pos, String text, TextGizmo.Style style) implements Gizmo {
   public TextGizmo(Vec3 param1, String param2, TextGizmo.Style param3) {
      super();
      this.pos = var1;
      this.text = var2;
      this.style = var3;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      TextGizmo.Style var3;
      if (var2 < 1.0F) {
         var3 = new TextGizmo.Style(ARGB.multiplyAlpha(this.style.color, var2), this.style.scale, this.style.adjustLeft);
      } else {
         var3 = this.style;
      }

      var1.addText(this.pos, this.text, var3);
   }

   public Vec3 pos() {
      return this.pos;
   }

   public String text() {
      return this.text;
   }

   public TextGizmo.Style style() {
      return this.style;
   }

   public static record Style(int color, float scale, OptionalDouble adjustLeft) {
      final int color;
      final float scale;
      final OptionalDouble adjustLeft;
      public static final float DEFAULT_SCALE = 0.32F;

      public Style(int param1, float param2, OptionalDouble param3) {
         super();
         this.color = var1;
         this.scale = var2;
         this.adjustLeft = var3;
      }

      public static TextGizmo.Style whiteAndCentered() {
         return new TextGizmo.Style(-1, 0.32F, OptionalDouble.empty());
      }

      public static TextGizmo.Style forColorAndCentered(int var0) {
         return new TextGizmo.Style(var0, 0.32F, OptionalDouble.empty());
      }

      public static TextGizmo.Style forColor(int var0) {
         return new TextGizmo.Style(var0, 0.32F, OptionalDouble.of(0.0D));
      }

      public TextGizmo.Style withScale(float var1) {
         return new TextGizmo.Style(this.color, var1, this.adjustLeft);
      }

      public TextGizmo.Style withLeftAlignment(float var1) {
         return new TextGizmo.Style(this.color, this.scale, OptionalDouble.of((double)var1));
      }

      public int color() {
         return this.color;
      }

      public float scale() {
         return this.scale;
      }

      public OptionalDouble adjustLeft() {
         return this.adjustLeft;
      }
   }
}
