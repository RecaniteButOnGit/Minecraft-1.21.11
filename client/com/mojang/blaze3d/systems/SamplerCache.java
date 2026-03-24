package com.mojang.blaze3d.systems;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import java.util.OptionalDouble;

public class SamplerCache {
   private final GpuSampler[] samplers = new GpuSampler[32];

   public SamplerCache() {
      super();
   }

   public void initialize() {
      GpuDevice var1 = RenderSystem.getDevice();
      if (AddressMode.values().length == 2 && FilterMode.values().length == 2) {
         AddressMode[] var2 = AddressMode.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            AddressMode var5 = var2[var4];
            AddressMode[] var6 = AddressMode.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               AddressMode var9 = var6[var8];
               FilterMode[] var10 = FilterMode.values();
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  FilterMode var13 = var10[var12];
                  FilterMode[] var14 = FilterMode.values();
                  int var15 = var14.length;

                  for(int var16 = 0; var16 < var15; ++var16) {
                     FilterMode var17 = var14[var16];
                     boolean[] var18 = new boolean[]{true, false};
                     int var19 = var18.length;

                     for(int var20 = 0; var20 < var19; ++var20) {
                        boolean var21 = var18[var20];
                        this.samplers[encode(var5, var9, var13, var17, var21)] = var1.createSampler(var5, var9, var13, var17, 1, var21 ? OptionalDouble.empty() : OptionalDouble.of(0.0D));
                     }
                  }
               }
            }
         }

      } else {
         throw new IllegalStateException("AddressMode and FilterMode enum sizes must be 2 - if you expanded them, please update SamplerCache");
      }
   }

   public GpuSampler getSampler(AddressMode var1, AddressMode var2, FilterMode var3, FilterMode var4, boolean var5) {
      return this.samplers[encode(var1, var2, var3, var4, var5)];
   }

   public GpuSampler getClampToEdge(FilterMode var1) {
      return this.getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, var1, var1, false);
   }

   public GpuSampler getClampToEdge(FilterMode var1, boolean var2) {
      return this.getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, var1, var1, var2);
   }

   public GpuSampler getRepeat(FilterMode var1) {
      return this.getSampler(AddressMode.REPEAT, AddressMode.REPEAT, var1, var1, false);
   }

   public GpuSampler getRepeat(FilterMode var1, boolean var2) {
      return this.getSampler(AddressMode.REPEAT, AddressMode.REPEAT, var1, var1, var2);
   }

   public void close() {
      GpuSampler[] var1 = this.samplers;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GpuSampler var4 = var1[var3];
         var4.close();
      }

   }

   @VisibleForTesting
   static int encode(AddressMode var0, AddressMode var1, FilterMode var2, FilterMode var3, boolean var4) {
      byte var5 = 0;
      int var6 = var5 | var0.ordinal() & 1;
      var6 |= (var1.ordinal() & 1) << 1;
      var6 |= (var2.ordinal() & 1) << 2;
      var6 |= (var3.ordinal() & 1) << 3;
      if (var4) {
         var6 |= 16;
      }

      return var6;
   }
}
