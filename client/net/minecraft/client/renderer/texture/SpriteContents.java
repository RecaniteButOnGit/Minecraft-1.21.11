package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class SpriteContents implements Stitcher.Entry, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int UBO_SIZE = (new Std140SizeCalculator()).putMat4f().putMat4f().putFloat().putFloat().putInt().get();
   final Identifier name;
   final int width;
   final int height;
   private final NativeImage originalImage;
   NativeImage[] byMipLevel;
   private final SpriteContents.AnimatedTexture animatedTexture;
   private final List<MetadataSectionType.WithValue<?>> additionalMetadata;
   private final MipmapStrategy mipmapStrategy;
   private final float alphaCutoffBias;

   public SpriteContents(Identifier var1, FrameSize var2, NativeImage var3) {
      this(var1, var2, var3, Optional.empty(), List.of(), Optional.empty());
   }

   public SpriteContents(Identifier var1, FrameSize var2, NativeImage var3, Optional<AnimationMetadataSection> var4, List<MetadataSectionType.WithValue<?>> var5, Optional<TextureMetadataSection> var6) {
      super();
      this.name = var1;
      this.width = var2.width();
      this.height = var2.height();
      this.additionalMetadata = var5;
      this.animatedTexture = (SpriteContents.AnimatedTexture)var4.map((var3x) -> {
         return this.createAnimatedTexture(var2, var3.getWidth(), var3.getHeight(), var3x);
      }).orElse((Object)null);
      this.originalImage = var3;
      this.byMipLevel = new NativeImage[]{this.originalImage};
      this.mipmapStrategy = (MipmapStrategy)var6.map(TextureMetadataSection::mipmapStrategy).orElse(MipmapStrategy.AUTO);
      this.alphaCutoffBias = (Float)var6.map(TextureMetadataSection::alphaCutoffBias).orElse(0.0F);
   }

   public void increaseMipLevel(int var1) {
      try {
         this.byMipLevel = MipmapGenerator.generateMipLevels(this.name, this.byMipLevel, var1, this.mipmapStrategy, this.alphaCutoffBias);
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.forThrowable(var5, "Generating mipmaps for frame");
         CrashReportCategory var4 = var3.addCategory("Frame being iterated");
         var4.setDetail("Sprite name", (Object)this.name);
         var4.setDetail("Sprite size", () -> {
            return this.width + " x " + this.height;
         });
         var4.setDetail("Sprite frames", () -> {
            return this.getFrameCount() + " frames";
         });
         var4.setDetail("Mipmap levels", (Object)var1);
         var4.setDetail("Original image size", () -> {
            int var10000 = this.originalImage.getWidth();
            return var10000 + "x" + this.originalImage.getHeight();
         });
         throw new ReportedException(var3);
      }
   }

   private int getFrameCount() {
      return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
   }

   public boolean isAnimated() {
      return this.getFrameCount() > 1;
   }

   private SpriteContents.AnimatedTexture createAnimatedTexture(FrameSize var1, int var2, int var3, AnimationMetadataSection var4) {
      int var5 = var2 / var1.width();
      int var6 = var3 / var1.height();
      int var7 = var5 * var6;
      int var8 = var4.defaultFrameTime();
      ArrayList var9;
      if (var4.frames().isEmpty()) {
         var9 = new ArrayList(var7);

         for(int var10 = 0; var10 < var7; ++var10) {
            var9.add(new SpriteContents.FrameInfo(var10, var8));
         }
      } else {
         List var16 = (List)var4.frames().get();
         var9 = new ArrayList(var16.size());
         Iterator var11 = var16.iterator();

         while(var11.hasNext()) {
            AnimationFrame var12 = (AnimationFrame)var11.next();
            var9.add(new SpriteContents.FrameInfo(var12.index(), var12.timeOr(var8)));
         }

         int var17 = 0;
         IntOpenHashSet var18 = new IntOpenHashSet();

         for(Iterator var13 = var9.iterator(); var13.hasNext(); ++var17) {
            SpriteContents.FrameInfo var14 = (SpriteContents.FrameInfo)var13.next();
            boolean var15 = true;
            if (var14.time <= 0) {
               LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.name, var17, var14.time});
               var15 = false;
            }

            if (var14.index < 0 || var14.index >= var7) {
               LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.name, var17, var14.index});
               var15 = false;
            }

            if (var15) {
               var18.add(var14.index);
            } else {
               var13.remove();
            }
         }

         int[] var19 = IntStream.range(0, var7).filter((var1x) -> {
            return !var18.contains(var1x);
         }).toArray();
         if (var19.length > 0) {
            LOGGER.warn("Unused frames in sprite {}: {}", this.name, Arrays.toString(var19));
         }
      }

      return var9.size() <= 1 ? null : new SpriteContents.AnimatedTexture(List.copyOf(var9), var5, var4.interpolatedFrames());
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public Identifier name() {
      return this.name;
   }

   public IntStream getUniqueFrames() {
      return this.animatedTexture != null ? this.animatedTexture.getUniqueFrames() : IntStream.of(1);
   }

   public SpriteContents.AnimationState createAnimationState(GpuBufferSlice var1, int var2) {
      return this.animatedTexture != null ? this.animatedTexture.createAnimationState(var1, var2) : null;
   }

   public <T> Optional<T> getAdditionalMetadata(MetadataSectionType<T> var1) {
      Iterator var2 = this.additionalMetadata.iterator();

      Optional var4;
      do {
         if (!var2.hasNext()) {
            return Optional.empty();
         }

         MetadataSectionType.WithValue var3 = (MetadataSectionType.WithValue)var2.next();
         var4 = var3.unwrapToType(var1);
      } while(!var4.isPresent());

      return var4;
   }

   public void close() {
      NativeImage[] var1 = this.byMipLevel;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         NativeImage var4 = var1[var3];
         var4.close();
      }

   }

   public String toString() {
      String var10000 = String.valueOf(this.name);
      return "SpriteContents{name=" + var10000 + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
   }

   public boolean isTransparent(int var1, int var2, int var3) {
      int var4 = var2;
      int var5 = var3;
      if (this.animatedTexture != null) {
         var4 = var2 + this.animatedTexture.getFrameX(var1) * this.width;
         var5 = var3 + this.animatedTexture.getFrameY(var1) * this.height;
      }

      return ARGB.alpha(this.originalImage.getPixel(var4, var5)) == 0;
   }

   public void uploadFirstFrame(GpuTexture var1, int var2) {
      RenderSystem.getDevice().createCommandEncoder().writeToTexture(var1, this.byMipLevel[var2], var2, 0, 0, 0, this.width >> var2, this.height >> var2, 0, 0);
   }

   private class AnimatedTexture {
      final List<SpriteContents.FrameInfo> frames;
      private final int frameRowSize;
      final boolean interpolateFrames;

      AnimatedTexture(final List<SpriteContents.FrameInfo> param2, final int param3, final boolean param4) {
         super();
         this.frames = var2;
         this.frameRowSize = var3;
         this.interpolateFrames = var4;
      }

      int getFrameX(int var1) {
         return var1 % this.frameRowSize;
      }

      int getFrameY(int var1) {
         return var1 / this.frameRowSize;
      }

      public SpriteContents.AnimationState createAnimationState(GpuBufferSlice var1, int var2) {
         GpuDevice var3 = RenderSystem.getDevice();
         Int2ObjectOpenHashMap var4 = new Int2ObjectOpenHashMap();
         GpuBufferSlice[] var5 = new GpuBufferSlice[SpriteContents.this.byMipLevel.length];
         int[] var6 = this.getUniqueFrames().toArray();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            int var9 = var6[var8];
            GpuTexture var10 = var3.createTexture((Supplier)(() -> {
               String var10000 = String.valueOf(SpriteContents.this.name);
               return var10000 + " animation frame " + var9;
            }), 5, TextureFormat.RGBA8, SpriteContents.this.width, SpriteContents.this.height, 1, SpriteContents.this.byMipLevel.length + 1);
            int var11 = this.getFrameX(var9) * SpriteContents.this.width;
            int var12 = this.getFrameY(var9) * SpriteContents.this.height;

            for(int var13 = 0; var13 < SpriteContents.this.byMipLevel.length; ++var13) {
               RenderSystem.getDevice().createCommandEncoder().writeToTexture(var10, SpriteContents.this.byMipLevel[var13], var13, 0, 0, 0, SpriteContents.this.width >> var13, SpriteContents.this.height >> var13, var11 >> var13, var12 >> var13);
            }

            var4.put(var9, RenderSystem.getDevice().createTextureView(var10));
         }

         for(int var14 = 0; var14 < SpriteContents.this.byMipLevel.length; ++var14) {
            var5[var14] = var1.slice((long)(var14 * var2), (long)var2);
         }

         return SpriteContents.this.new AnimationState(SpriteContents.this, this, var4, var5);
      }

      public IntStream getUniqueFrames() {
         return this.frames.stream().mapToInt((var0) -> {
            return var0.index;
         }).distinct();
      }
   }

   private static record FrameInfo(int index, int time) {
      final int index;
      final int time;

      FrameInfo(int param1, int param2) {
         super();
         this.index = var1;
         this.time = var2;
      }

      public int index() {
         return this.index;
      }

      public int time() {
         return this.time;
      }
   }

   public class AnimationState implements AutoCloseable {
      private int frame;
      private int subFrame;
      private final SpriteContents.AnimatedTexture animationInfo;
      private final Int2ObjectMap<GpuTextureView> frameTexturesByIndex;
      private final GpuBufferSlice[] spriteUbosByMip;
      private boolean isDirty = true;

      AnimationState(final SpriteContents param1, final SpriteContents.AnimatedTexture param2, final Int2ObjectMap param3, final GpuBufferSlice[] param4) {
         super();
         this.animationInfo = var2;
         this.frameTexturesByIndex = var3;
         this.spriteUbosByMip = var4;
      }

      public void tick() {
         ++this.subFrame;
         this.isDirty = false;
         SpriteContents.FrameInfo var1 = (SpriteContents.FrameInfo)this.animationInfo.frames.get(this.frame);
         if (this.subFrame >= var1.time) {
            int var2 = var1.index;
            this.frame = (this.frame + 1) % this.animationInfo.frames.size();
            this.subFrame = 0;
            int var3 = ((SpriteContents.FrameInfo)this.animationInfo.frames.get(this.frame)).index;
            if (var2 != var3) {
               this.isDirty = true;
            }
         }

      }

      public GpuBufferSlice getDrawUbo(int var1) {
         return this.spriteUbosByMip[var1];
      }

      public boolean needsToDraw() {
         return this.animationInfo.interpolateFrames || this.isDirty;
      }

      public void drawToAtlas(RenderPass var1, GpuBufferSlice var2) {
         GpuSampler var3 = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST, true);
         List var4 = this.animationInfo.frames;
         int var5 = ((SpriteContents.FrameInfo)var4.get(this.frame)).index;
         float var6 = (float)this.subFrame / (float)((SpriteContents.FrameInfo)this.animationInfo.frames.get(this.frame)).time;
         int var7 = (int)(var6 * 1000.0F);
         if (this.animationInfo.interpolateFrames) {
            int var8 = ((SpriteContents.FrameInfo)var4.get((this.frame + 1) % var4.size())).index;
            var1.setPipeline(RenderPipelines.ANIMATE_SPRITE_INTERPOLATE);
            var1.bindTexture("CurrentSprite", (GpuTextureView)this.frameTexturesByIndex.get(var5), var3);
            var1.bindTexture("NextSprite", (GpuTextureView)this.frameTexturesByIndex.get(var8), var3);
         } else if (this.isDirty) {
            var1.setPipeline(RenderPipelines.ANIMATE_SPRITE_BLIT);
            var1.bindTexture("Sprite", (GpuTextureView)this.frameTexturesByIndex.get(var5), var3);
         }

         var1.setUniform("SpriteAnimationInfo", var2);
         var1.draw(var7 << 3, 6);
      }

      public void close() {
         ObjectIterator var1 = this.frameTexturesByIndex.values().iterator();

         while(var1.hasNext()) {
            GpuTextureView var2 = (GpuTextureView)var1.next();
            var2.texture().close();
            var2.close();
         }

      }
   }
}
