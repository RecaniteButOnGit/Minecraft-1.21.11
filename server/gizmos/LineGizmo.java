package net.minecraft.gizmos;

import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

public record LineGizmo(Vec3 start, Vec3 end, int color, float width) implements Gizmo {
   public static final float DEFAULT_WIDTH = 3.0F;

   public LineGizmo(Vec3 param1, Vec3 param2, int param3, float param4) {
      super();
      this.start = var1;
      this.end = var2;
      this.color = var3;
      this.width = var4;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      var1.addLine(this.start, this.end, ARGB.multiplyAlpha(this.color, var2), this.width);
   }

   public Vec3 start() {
      return this.start;
   }

   public Vec3 end() {
      return this.end;
   }

   public int color() {
      return this.color;
   }

   public float width() {
      return this.width;
   }
}
