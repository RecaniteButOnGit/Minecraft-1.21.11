package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.Util;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollisionBoxRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = 4.9E-324D;
   private List<VoxelShape> shapes = Collections.emptyList();

   public CollisionBoxRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      double var10 = (double)Util.getNanos();
      if (var10 - this.lastUpdateTime > 1.0E8D) {
         this.lastUpdateTime = var10;
         Entity var12 = this.minecraft.gameRenderer.getMainCamera().entity();
         this.shapes = ImmutableList.copyOf(var12.level().getCollisions(var12, var12.getBoundingBox().inflate(6.0D)));
      }

      Iterator var17 = this.shapes.iterator();

      while(var17.hasNext()) {
         VoxelShape var13 = (VoxelShape)var17.next();
         GizmoStyle var14 = GizmoStyle.stroke(-1);
         Iterator var15 = var13.toAabbs().iterator();

         while(var15.hasNext()) {
            AABB var16 = (AABB)var15.next();
            Gizmos.cuboid(var16, var14);
         }
      }

   }
}
