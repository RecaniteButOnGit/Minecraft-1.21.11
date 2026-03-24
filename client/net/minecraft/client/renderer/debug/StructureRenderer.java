package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugStructureInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;

public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
   public StructureRenderer() {
      super();
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      var7.forEachChunk(DebugSubscriptions.STRUCTURES, (var0, var1x) -> {
         Iterator var2 = var1x.iterator();

         while(var2.hasNext()) {
            DebugStructureInfo var3 = (DebugStructureInfo)var2.next();
            Gizmos.cuboid(AABB.of(var3.boundingBox()), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F)));
            Iterator var4 = var3.pieces().iterator();

            while(var4.hasNext()) {
               DebugStructureInfo.Piece var5 = (DebugStructureInfo.Piece)var4.next();
               if (var5.isStart()) {
                  Gizmos.cuboid(AABB.of(var5.boundingBox()), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.0F, 1.0F, 0.0F)));
               } else {
                  Gizmos.cuboid(AABB.of(var5.boundingBox()), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.0F, 0.0F, 1.0F)));
               }
            }
         }

      });
   }
}
