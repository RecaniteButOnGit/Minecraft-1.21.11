package net.minecraft.gizmos;

import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

public record PointGizmo(Vec3 pos, int color, float size) implements Gizmo {
   public PointGizmo(Vec3 param1, int param2, float param3) {
      super();
      this.pos = var1;
      this.color = var2;
      this.size = var3;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      var1.addPoint(this.pos, ARGB.multiplyAlpha(this.color, var2), this.size);
   }

   public Vec3 pos() {
      return this.pos;
   }

   public int color() {
      return this.color;
   }

   public float size() {
      return this.size;
   }
}
