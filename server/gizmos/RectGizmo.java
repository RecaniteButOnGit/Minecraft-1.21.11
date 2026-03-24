package net.minecraft.gizmos;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public record RectGizmo(Vec3 a, Vec3 b, Vec3 c, Vec3 d, GizmoStyle style) implements Gizmo {
   public RectGizmo(Vec3 param1, Vec3 param2, Vec3 param3, Vec3 param4, GizmoStyle param5) {
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.style = var5;
   }

   public static RectGizmo fromCuboidFace(Vec3 var0, Vec3 var1, Direction var2, GizmoStyle var3) {
      RectGizmo var10000;
      switch(var2) {
      case DOWN:
         var10000 = new RectGizmo(new Vec3(var0.x, var0.y, var0.z), new Vec3(var1.x, var0.y, var0.z), new Vec3(var1.x, var0.y, var1.z), new Vec3(var0.x, var0.y, var1.z), var3);
         break;
      case UP:
         var10000 = new RectGizmo(new Vec3(var0.x, var1.y, var0.z), new Vec3(var0.x, var1.y, var1.z), new Vec3(var1.x, var1.y, var1.z), new Vec3(var1.x, var1.y, var0.z), var3);
         break;
      case NORTH:
         var10000 = new RectGizmo(new Vec3(var0.x, var0.y, var0.z), new Vec3(var0.x, var1.y, var0.z), new Vec3(var1.x, var1.y, var0.z), new Vec3(var1.x, var0.y, var0.z), var3);
         break;
      case SOUTH:
         var10000 = new RectGizmo(new Vec3(var0.x, var0.y, var1.z), new Vec3(var1.x, var0.y, var1.z), new Vec3(var1.x, var1.y, var1.z), new Vec3(var0.x, var1.y, var1.z), var3);
         break;
      case WEST:
         var10000 = new RectGizmo(new Vec3(var0.x, var0.y, var0.z), new Vec3(var0.x, var0.y, var1.z), new Vec3(var0.x, var1.y, var1.z), new Vec3(var0.x, var1.y, var0.z), var3);
         break;
      case EAST:
         var10000 = new RectGizmo(new Vec3(var1.x, var0.y, var0.z), new Vec3(var1.x, var1.y, var0.z), new Vec3(var1.x, var1.y, var1.z), new Vec3(var1.x, var0.y, var1.z), var3);
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      int var3;
      if (this.style.hasFill()) {
         var3 = this.style.multipliedFill(var2);
         var1.addQuad(this.a, this.b, this.c, this.d, var3);
      }

      if (this.style.hasStroke()) {
         var3 = this.style.multipliedStroke(var2);
         var1.addLine(this.a, this.b, var3, this.style.strokeWidth());
         var1.addLine(this.b, this.c, var3, this.style.strokeWidth());
         var1.addLine(this.c, this.d, var3, this.style.strokeWidth());
         var1.addLine(this.d, this.a, var3, this.style.strokeWidth());
      }

   }

   public Vec3 a() {
      return this.a;
   }

   public Vec3 b() {
      return this.b;
   }

   public Vec3 c() {
      return this.c;
   }

   public Vec3 d() {
      return this.d;
   }

   public GizmoStyle style() {
      return this.style;
   }
}
