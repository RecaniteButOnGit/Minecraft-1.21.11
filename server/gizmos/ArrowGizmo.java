package net.minecraft.gizmos;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record ArrowGizmo(Vec3 start, Vec3 end, int color, float width) implements Gizmo {
   public static final float DEFAULT_WIDTH = 2.5F;

   public ArrowGizmo(Vec3 param1, Vec3 param2, int param3, float param4) {
      super();
      this.start = var1;
      this.end = var2;
      this.color = var3;
      this.width = var4;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      int var3 = ARGB.multiplyAlpha(this.color, var2);
      var1.addLine(this.start, this.end, var3, this.width);
      Quaternionf var4 = (new Quaternionf()).rotationTo(new Vector3f(1.0F, 0.0F, 0.0F), this.end.subtract(this.start).toVector3f().normalize());
      float var5 = (float)Mth.clamp(this.end.distanceTo(this.start) * 0.10000000149011612D, 0.10000000149011612D, 1.0D);
      Vector3f[] var6 = new Vector3f[]{var4.transform(-var5, var5, 0.0F, new Vector3f()), var4.transform(-var5, 0.0F, var5, new Vector3f()), var4.transform(-var5, -var5, 0.0F, new Vector3f()), var4.transform(-var5, 0.0F, -var5, new Vector3f())};
      Vector3f[] var7 = var6;
      int var8 = var6.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         Vector3f var10 = var7[var9];
         var1.addLine(this.end.add((double)var10.x, (double)var10.y, (double)var10.z), this.end, var3, this.width);
      }

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
