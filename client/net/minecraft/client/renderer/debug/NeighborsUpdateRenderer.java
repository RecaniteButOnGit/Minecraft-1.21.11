package net.minecraft.client.renderer.debug;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
   public NeighborsUpdateRenderer() {
      super();
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      int var10 = DebugSubscriptions.NEIGHBOR_UPDATES.expireAfterTicks();
      double var11 = 1.0D / (double)(var10 * 2);
      HashMap var13 = new HashMap();
      var7.forEachEvent(DebugSubscriptions.NEIGHBOR_UPDATES, (var1x, var2, var3x) -> {
         long var4 = (long)(var3x - var2);
         NeighborsUpdateRenderer.LastUpdate var6 = (NeighborsUpdateRenderer.LastUpdate)var13.getOrDefault(var1x, NeighborsUpdateRenderer.LastUpdate.NONE);
         var13.put(var1x, var6.tryCount((int)var4));
      });
      Iterator var14 = var13.entrySet().iterator();

      BlockPos var16;
      NeighborsUpdateRenderer.LastUpdate var17;
      Entry var15;
      while(var14.hasNext()) {
         var15 = (Entry)var14.next();
         var16 = (BlockPos)var15.getKey();
         var17 = (NeighborsUpdateRenderer.LastUpdate)var15.getValue();
         AABB var18 = (new AABB(var16)).inflate(0.002D).deflate(var11 * (double)var17.age);
         Gizmos.cuboid(var18, GizmoStyle.stroke(-1));
      }

      var14 = var13.entrySet().iterator();

      while(var14.hasNext()) {
         var15 = (Entry)var14.next();
         var16 = (BlockPos)var15.getKey();
         var17 = (NeighborsUpdateRenderer.LastUpdate)var15.getValue();
         Gizmos.billboardText(String.valueOf(var17.count), Vec3.atCenterOf(var16), TextGizmo.Style.whiteAndCentered());
      }

   }

   private static record LastUpdate(int count, int age) {
      final int count;
      final int age;
      static final NeighborsUpdateRenderer.LastUpdate NONE = new NeighborsUpdateRenderer.LastUpdate(0, 2147483647);

      private LastUpdate(int param1, int param2) {
         super();
         this.count = var1;
         this.age = var2;
      }

      public NeighborsUpdateRenderer.LastUpdate tryCount(int var1) {
         if (var1 == this.age) {
            return new NeighborsUpdateRenderer.LastUpdate(this.count + 1, var1);
         } else {
            return var1 < this.age ? new NeighborsUpdateRenderer.LastUpdate(1, var1) : this;
         }
      }

      public int count() {
         return this.count;
      }

      public int age() {
         return this.age;
      }
   }
}
