package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class ChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   final Minecraft minecraft;
   private double lastUpdateTime = 4.9E-324D;
   private final int radius = 12;
   private ChunkDebugRenderer.ChunkData data;

   public ChunkDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      double var10 = (double)Util.getNanos();
      if (var10 - this.lastUpdateTime > 3.0E9D) {
         this.lastUpdateTime = var10;
         IntegratedServer var12 = this.minecraft.getSingleplayerServer();
         if (var12 != null) {
            this.data = new ChunkDebugRenderer.ChunkData(this, var12, var1, var5);
         } else {
            this.data = null;
         }
      }

      if (this.data != null) {
         Map var25 = (Map)this.data.serverData.getNow((Object)null);
         double var13 = this.minecraft.gameRenderer.getMainCamera().position().y * 0.85D;
         Iterator var15 = this.data.clientData.entrySet().iterator();

         while(var15.hasNext()) {
            Entry var16 = (Entry)var15.next();
            ChunkPos var17 = (ChunkPos)var16.getKey();
            String var18 = (String)var16.getValue();
            if (var25 != null) {
               var18 = var18 + (String)var25.get(var17);
            }

            String[] var19 = var18.split("\n");
            int var20 = 0;
            String[] var21 = var19;
            int var22 = var19.length;

            for(int var23 = 0; var23 < var22; ++var23) {
               String var24 = var21[var23];
               Gizmos.billboardText(var24, new Vec3((double)SectionPos.sectionToBlockCoord(var17.x, 8), var13 + (double)var20, (double)SectionPos.sectionToBlockCoord(var17.z, 8)), TextGizmo.Style.whiteAndCentered().withScale(2.4F)).setAlwaysOnTop();
               var20 -= 2;
            }
         }
      }

   }

   private final class ChunkData {
      final Map<ChunkPos, String> clientData;
      final CompletableFuture<Map<ChunkPos, String>> serverData;

      ChunkData(final ChunkDebugRenderer param1, final IntegratedServer param2, final double param3, final double param5) {
         super();
         ClientLevel var7 = var1.minecraft.level;
         ResourceKey var8 = var7.dimension();
         int var9 = SectionPos.posToSectionCoord(var3);
         int var10 = SectionPos.posToSectionCoord(var5);
         Builder var11 = ImmutableMap.builder();
         ClientChunkCache var12 = var7.getChunkSource();

         for(int var13 = var9 - 12; var13 <= var9 + 12; ++var13) {
            for(int var14 = var10 - 12; var14 <= var10 + 12; ++var14) {
               ChunkPos var15 = new ChunkPos(var13, var14);
               String var16 = "";
               LevelChunk var17 = var12.getChunk(var13, var14, false);
               var16 = var16 + "Client: ";
               if (var17 == null) {
                  var16 = var16 + "0n/a\n";
               } else {
                  var16 = var16 + (var17.isEmpty() ? " E" : "");
                  var16 = var16 + "\n";
               }

               var11.put(var15, var16);
            }
         }

         this.clientData = var11.build();
         this.serverData = var2.submit(() -> {
            ServerLevel var5 = var2.getLevel(var8);
            if (var5 == null) {
               return ImmutableMap.of();
            } else {
               Builder var6 = ImmutableMap.builder();
               ServerChunkCache var7 = var5.getChunkSource();

               for(int var8x = var9 - 12; var8x <= var9 + 12; ++var8x) {
                  for(int var9x = var10 - 12; var9x <= var10 + 12; ++var9x) {
                     ChunkPos var10x = new ChunkPos(var8x, var9x);
                     String var10002 = var7.getChunkDebugData(var10x);
                     var6.put(var10x, "Server: " + var10002);
                  }
               }

               return var6.build();
            }
         });
      }
   }
}
