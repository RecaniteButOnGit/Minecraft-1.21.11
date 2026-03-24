package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ChunkBorderRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float THICK_WIDTH = 4.0F;
   private static final float THIN_WIDTH = 1.0F;
   private final Minecraft minecraft;
   private static final int CELL_BORDER = ARGB.color(255, 0, 155, 155);
   private static final int YELLOW = ARGB.color(255, 255, 255, 0);
   private static final int MAJOR_LINES = ARGB.colorFromFloat(1.0F, 0.25F, 0.25F, 1.0F);

   public ChunkBorderRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      Entity var10 = this.minecraft.gameRenderer.getMainCamera().entity();
      float var11 = (float)this.minecraft.level.getMinY();
      float var12 = (float)(this.minecraft.level.getMaxY() + 1);
      SectionPos var13 = SectionPos.of(var10.blockPosition());
      double var14 = (double)var13.minBlockX();
      double var16 = (double)var13.minBlockZ();

      int var18;
      int var19;
      for(var18 = -16; var18 <= 32; var18 += 16) {
         for(var19 = -16; var19 <= 32; var19 += 16) {
            Gizmos.line(new Vec3(var14 + (double)var18, (double)var11, var16 + (double)var19), new Vec3(var14 + (double)var18, (double)var12, var16 + (double)var19), ARGB.colorFromFloat(0.5F, 1.0F, 0.0F, 0.0F), 4.0F);
         }
      }

      for(var18 = 2; var18 < 16; var18 += 2) {
         var19 = var18 % 4 == 0 ? CELL_BORDER : YELLOW;
         Gizmos.line(new Vec3(var14 + (double)var18, (double)var11, var16), new Vec3(var14 + (double)var18, (double)var12, var16), var19, 1.0F);
         Gizmos.line(new Vec3(var14 + (double)var18, (double)var11, var16 + 16.0D), new Vec3(var14 + (double)var18, (double)var12, var16 + 16.0D), var19, 1.0F);
      }

      for(var18 = 2; var18 < 16; var18 += 2) {
         var19 = var18 % 4 == 0 ? CELL_BORDER : YELLOW;
         Gizmos.line(new Vec3(var14, (double)var11, var16 + (double)var18), new Vec3(var14, (double)var12, var16 + (double)var18), var19, 1.0F);
         Gizmos.line(new Vec3(var14 + 16.0D, (double)var11, var16 + (double)var18), new Vec3(var14 + 16.0D, (double)var12, var16 + (double)var18), var19, 1.0F);
      }

      for(var18 = this.minecraft.level.getMinY(); var18 <= this.minecraft.level.getMaxY() + 1; var18 += 2) {
         float var21 = (float)var18;
         int var20 = var18 % 8 == 0 ? CELL_BORDER : YELLOW;
         Gizmos.line(new Vec3(var14, (double)var21, var16), new Vec3(var14, (double)var21, var16 + 16.0D), var20, 1.0F);
         Gizmos.line(new Vec3(var14, (double)var21, var16 + 16.0D), new Vec3(var14 + 16.0D, (double)var21, var16 + 16.0D), var20, 1.0F);
         Gizmos.line(new Vec3(var14 + 16.0D, (double)var21, var16 + 16.0D), new Vec3(var14 + 16.0D, (double)var21, var16), var20, 1.0F);
         Gizmos.line(new Vec3(var14 + 16.0D, (double)var21, var16), new Vec3(var14, (double)var21, var16), var20, 1.0F);
      }

      for(var18 = 0; var18 <= 16; var18 += 16) {
         for(var19 = 0; var19 <= 16; var19 += 16) {
            Gizmos.line(new Vec3(var14 + (double)var18, (double)var11, var16 + (double)var19), new Vec3(var14 + (double)var18, (double)var12, var16 + (double)var19), MAJOR_LINES, 4.0F);
         }
      }

      Gizmos.cuboid(new AABB((double)var13.minBlockX(), (double)var13.minBlockY(), (double)var13.minBlockZ(), (double)(var13.maxBlockX() + 1), (double)(var13.maxBlockY() + 1), (double)(var13.maxBlockZ() + 1)), GizmoStyle.stroke(MAJOR_LINES, 1.0F)).setAlwaysOnTop();

      for(var18 = this.minecraft.level.getMinY(); var18 <= this.minecraft.level.getMaxY() + 1; var18 += 16) {
         Gizmos.line(new Vec3(var14, (double)var18, var16), new Vec3(var14, (double)var18, var16 + 16.0D), MAJOR_LINES, 4.0F);
         Gizmos.line(new Vec3(var14, (double)var18, var16 + 16.0D), new Vec3(var14 + 16.0D, (double)var18, var16 + 16.0D), MAJOR_LINES, 4.0F);
         Gizmos.line(new Vec3(var14 + 16.0D, (double)var18, var16 + 16.0D), new Vec3(var14 + 16.0D, (double)var18, var16), MAJOR_LINES, 4.0F);
         Gizmos.line(new Vec3(var14 + 16.0D, (double)var18, var16), new Vec3(var14, (double)var18, var16), MAJOR_LINES, 4.0F);
      }

   }
}
