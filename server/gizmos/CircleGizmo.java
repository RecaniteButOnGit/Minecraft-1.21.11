package net.minecraft.gizmos;

import net.minecraft.world.phys.Vec3;

public record CircleGizmo(Vec3 pos, float radius, GizmoStyle style) implements Gizmo {
   private static final int CIRCLE_VERTICES = 20;
   private static final float SEGMENT_SIZE_RADIANS = 0.31415927F;

   public CircleGizmo(Vec3 param1, float param2, GizmoStyle param3) {
      super();
      this.pos = var1;
      this.radius = var2;
      this.style = var3;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      if (this.style.hasStroke() || this.style.hasFill()) {
         Vec3[] var3 = new Vec3[21];

         int var4;
         for(var4 = 0; var4 < 20; ++var4) {
            float var5 = (float)var4 * 0.31415927F;
            Vec3 var6 = this.pos.add((double)((float)((double)this.radius * Math.cos((double)var5))), 0.0D, (double)((float)((double)this.radius * Math.sin((double)var5))));
            var3[var4] = var6;
         }

         var3[20] = var3[0];
         if (this.style.hasFill()) {
            var4 = this.style.multipliedFill(var2);
            var1.addTriangleFan(var3, var4);
         }

         if (this.style.hasStroke()) {
            var4 = this.style.multipliedStroke(var2);

            for(int var7 = 0; var7 < 20; ++var7) {
               var1.addLine(var3[var7], var3[var7 + 1], var4, this.style.strokeWidth());
            }
         }

      }
   }

   public Vec3 pos() {
      return this.pos;
   }

   public float radius() {
      return this.radius;
   }

   public GizmoStyle style() {
      return this.style;
   }
}
