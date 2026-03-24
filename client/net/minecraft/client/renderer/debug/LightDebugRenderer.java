package net.minecraft.client.renderer.debug;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class LightDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final boolean showBlockLight;
   private final boolean showSkyLight;
   private static final int MAX_RENDER_DIST = 10;

   public LightDebugRenderer(Minecraft var1, boolean var2, boolean var3) {
      super();
      this.minecraft = var1;
      this.showBlockLight = var2;
      this.showSkyLight = var3;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      ClientLevel var10 = this.minecraft.level;
      BlockPos var11 = BlockPos.containing(var1, var3, var5);
      LongOpenHashSet var12 = new LongOpenHashSet();
      Iterator var13 = BlockPos.betweenClosed(var11.offset(-10, -10, -10), var11.offset(10, 10, 10)).iterator();

      while(var13.hasNext()) {
         BlockPos var14 = (BlockPos)var13.next();
         int var15 = var10.getBrightness(LightLayer.SKY, var14);
         long var16 = SectionPos.blockToSection(var14.asLong());
         if (var12.add(var16)) {
            Gizmos.billboardText(var10.getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of(var16)), new Vec3((double)SectionPos.sectionToBlockCoord(SectionPos.x(var16), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.y(var16), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.z(var16), 8)), TextGizmo.Style.forColorAndCentered(-65536).withScale(4.8F));
         }

         int var18;
         if (var15 != 15 && this.showSkyLight) {
            var18 = ARGB.srgbLerp((float)var15 / 15.0F, -16776961, -16711681);
            Gizmos.billboardText(String.valueOf(var15), Vec3.atLowerCornerWithOffset(var14, 0.5D, 0.25D, 0.5D), TextGizmo.Style.forColorAndCentered(var18));
         }

         if (this.showBlockLight) {
            var18 = var10.getBrightness(LightLayer.BLOCK, var14);
            if (var18 != 0) {
               int var19 = ARGB.srgbLerp((float)var18 / 15.0F, -5636096, -256);
               Gizmos.billboardText(String.valueOf(var10.getBrightness(LightLayer.BLOCK, var14)), Vec3.atCenterOf(var14), TextGizmo.Style.forColorAndCentered(var19));
            }
         }
      }

   }
}
