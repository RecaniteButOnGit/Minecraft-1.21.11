package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import net.minecraft.SharedConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EntityHitboxDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   final Minecraft minecraft;

   public EntityHitboxDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      if (this.minecraft.level != null) {
         Iterator var10 = this.minecraft.level.entitiesForRendering().iterator();

         while(true) {
            Entity var11;
            do {
               do {
                  do {
                     if (!var10.hasNext()) {
                        return;
                     }

                     var11 = (Entity)var10.next();
                  } while(var11.isInvisible());
               } while(!var8.isVisible(var11.getBoundingBox()));
            } while(var11 == this.minecraft.getCameraEntity() && this.minecraft.options.getCameraType() == CameraType.FIRST_PERSON);

            this.showHitboxes(var11, var9, false);
            if (SharedConstants.DEBUG_SHOW_LOCAL_SERVER_ENTITY_HIT_BOXES) {
               Entity var12 = this.getServerEntity(var11);
               if (var12 != null) {
                  this.showHitboxes(var11, var9, true);
               } else {
                  Gizmos.billboardText("Missing Server Entity", var11.getPosition(var9).add(0.0D, var11.getBoundingBox().getYsize() + 1.5D, 0.0D), TextGizmo.Style.forColorAndCentered(-65536));
               }
            }
         }
      }
   }

   @Nullable
   private Entity getServerEntity(Entity var1) {
      IntegratedServer var2 = this.minecraft.getSingleplayerServer();
      if (var2 != null) {
         ServerLevel var3 = var2.getLevel(var1.level().dimension());
         if (var3 != null) {
            return var3.getEntity(var1.getId());
         }
      }

      return null;
   }

   private void showHitboxes(Entity var1, float var2, boolean var3) {
      Vec3 var4 = var1.position();
      Vec3 var5 = var1.getPosition(var2);
      Vec3 var6 = var5.subtract(var4);
      int var7 = var3 ? -16711936 : -1;
      Gizmos.cuboid(var1.getBoundingBox().move(var6), GizmoStyle.stroke(var7));
      Gizmos.point(var5, var7, 2.0F);
      Entity var8 = var1.getVehicle();
      float var10;
      Vec3 var11;
      if (var8 != null) {
         float var9 = Math.min(var8.getBbWidth(), var1.getBbWidth()) / 2.0F;
         var10 = 0.0625F;
         var11 = var8.getPassengerRidingPosition(var1).add(var6);
         Gizmos.cuboid(new AABB(var11.x - (double)var9, var11.y, var11.z - (double)var9, var11.x + (double)var9, var11.y + 0.0625D, var11.z + (double)var9), GizmoStyle.stroke(-256));
      }

      if (var1 instanceof LivingEntity) {
         AABB var17 = var1.getBoundingBox().move(var6);
         var10 = 0.01F;
         Gizmos.cuboid(new AABB(var17.minX, var17.minY + (double)var1.getEyeHeight() - 0.009999999776482582D, var17.minZ, var17.maxX, var17.minY + (double)var1.getEyeHeight() + 0.009999999776482582D, var17.maxZ), GizmoStyle.stroke(-65536));
      }

      if (var1 instanceof EnderDragon) {
         EnderDragon var18 = (EnderDragon)var1;
         EnderDragonPart[] var20 = var18.getSubEntities();
         int var22 = var20.length;

         for(int var12 = 0; var12 < var22; ++var12) {
            EnderDragonPart var13 = var20[var12];
            Vec3 var14 = var13.position();
            Vec3 var15 = var13.getPosition(var2);
            Vec3 var16 = var15.subtract(var14);
            Gizmos.cuboid(var13.getBoundingBox().move(var16), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.25F, 1.0F, 0.0F)));
         }
      }

      Vec3 var19 = var5.add(0.0D, (double)var1.getEyeHeight(), 0.0D);
      Vec3 var21 = var1.getViewVector(var2);
      Gizmos.arrow(var19, var19.add(var21.scale(2.0D)), -16776961);
      if (var3) {
         var11 = var1.getDeltaMovement();
         Gizmos.arrow(var5, var5.add(var11), -256);
      }

   }
}
