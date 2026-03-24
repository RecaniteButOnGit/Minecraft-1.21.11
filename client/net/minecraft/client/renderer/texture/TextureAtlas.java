package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class TextureAtlas extends AbstractTexture implements Dumpable, TickableTexture {
   private static final Logger LOGGER = LogUtils.getLogger();
   /** @deprecated */
   @Deprecated
   public static final Identifier LOCATION_BLOCKS = Identifier.withDefaultNamespace("textures/atlas/blocks.png");
   /** @deprecated */
   @Deprecated
   public static final Identifier LOCATION_ITEMS = Identifier.withDefaultNamespace("textures/atlas/items.png");
   /** @deprecated */
   @Deprecated
   public static final Identifier LOCATION_PARTICLES = Identifier.withDefaultNamespace("textures/atlas/particles.png");
   private List<TextureAtlasSprite> sprites = List.of();
   private List<SpriteContents.AnimationState> animatedTexturesStates = List.of();
   private Map<Identifier, TextureAtlasSprite> texturesByName = Map.of();
   @Nullable
   private TextureAtlasSprite missingSprite;
   private final Identifier location;
   private final int maxSupportedTextureSize;
   private int width;
   private int height;
   private int maxMipLevel;
   private int mipLevelCount;
   private GpuTextureView[] mipViews = new GpuTextureView[0];
   @Nullable
   private GpuBuffer spriteUbos;

   public TextureAtlas(Identifier var1) {
      super();
      this.location = var1;
      this.maxSupportedTextureSize = RenderSystem.getDevice().getMaxTextureSize();
   }

   private void createTexture(int var1, int var2, int var3) {
      LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{var1, var2, var3, this.location});
      GpuDevice var4 = RenderSystem.getDevice();
      this.close();
      Identifier var10002 = this.location;
      Objects.requireNonNull(var10002);
      this.texture = var4.createTexture((Supplier)(var10002::toString), 15, TextureFormat.RGBA8, var1, var2, 1, var3 + 1);
      this.textureView = var4.createTextureView(this.texture);
      this.width = var1;
      this.height = var2;
      this.maxMipLevel = var3;
      this.mipLevelCount = var3 + 1;
      this.mipViews = new GpuTextureView[this.mipLevelCount];

      for(int var5 = 0; var5 <= this.maxMipLevel; ++var5) {
         this.mipViews[var5] = var4.createTextureView(this.texture, var5, 1);
      }

   }

   public void upload(SpriteLoader.Preparations var1) {
      this.createTexture(var1.width(), var1.height(), var1.mipLevel());
      this.clearTextureData();
      this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
      this.texturesByName = Map.copyOf(var1.regions());
      this.missingSprite = (TextureAtlasSprite)this.texturesByName.get(MissingTextureAtlasSprite.getLocation());
      if (this.missingSprite == null) {
         String var10002 = String.valueOf(this.location);
         throw new IllegalStateException("Atlas '" + var10002 + "' (" + this.texturesByName.size() + " sprites) has no missing texture sprite");
      } else {
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();
         int var4 = (int)var1.regions().values().stream().filter(TextureAtlasSprite::isAnimated).count();
         int var5 = Mth.roundToward(SpriteContents.UBO_SIZE, RenderSystem.getDevice().getUniformOffsetAlignment());
         int var6 = var5 * this.mipLevelCount;
         ByteBuffer var7 = MemoryUtil.memAlloc(var4 * var6);
         int var8 = 0;
         Iterator var9 = var1.regions().values().iterator();

         while(var9.hasNext()) {
            TextureAtlasSprite var10 = (TextureAtlasSprite)var9.next();
            if (var10.isAnimated()) {
               var10.uploadSpriteUbo(var7, var8 * var6, this.maxMipLevel, this.width, this.height, var5);
               ++var8;
            }
         }

         GpuBuffer var14 = var8 > 0 ? RenderSystem.getDevice().createBuffer(() -> {
            return String.valueOf(this.location) + " sprite UBOs";
         }, 128, var7) : null;
         var8 = 0;
         Iterator var15 = var1.regions().values().iterator();

         while(var15.hasNext()) {
            TextureAtlasSprite var11 = (TextureAtlasSprite)var15.next();
            var2.add(var11);
            if (var11.isAnimated() && var14 != null) {
               SpriteContents.AnimationState var12 = var11.createAnimationState(var14.slice((long)(var8 * var6), (long)var6), var5);
               ++var8;
               if (var12 != null) {
                  var3.add(var12);
               }
            }
         }

         this.spriteUbos = var14;
         this.sprites = var2;
         this.animatedTexturesStates = List.copyOf(var3);
         this.uploadInitialContents();
         if (SharedConstants.DEBUG_DUMP_TEXTURE_ATLAS) {
            Path var16 = TextureUtil.getDebugTexturePath();

            try {
               Files.createDirectories(var16);
               this.dumpContents(this.location, var16);
            } catch (Exception var13) {
               LOGGER.warn("Failed to dump atlas contents to {}", var16);
            }
         }

      }
   }

   private void uploadInitialContents() {
      GpuDevice var1 = RenderSystem.getDevice();
      int var2 = Mth.roundToward(SpriteContents.UBO_SIZE, RenderSystem.getDevice().getUniformOffsetAlignment());
      int var3 = var2 * this.mipLevelCount;
      GpuSampler var4 = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST, true);
      List var5 = this.sprites.stream().filter((var0) -> {
         return !var0.isAnimated();
      }).toList();
      ArrayList var6 = new ArrayList();
      ByteBuffer var7 = MemoryUtil.memAlloc(var5.size() * var3);

      int var12;
      for(int var8 = 0; var8 < var5.size(); ++var8) {
         TextureAtlasSprite var9 = (TextureAtlasSprite)var5.get(var8);
         var9.uploadSpriteUbo(var7, var8 * var3, this.maxMipLevel, this.width, this.height, var2);
         GpuTexture var10 = var1.createTexture((Supplier)(() -> {
            return var9.contents().name().toString();
         }), 5, TextureFormat.RGBA8, var9.contents().width(), var9.contents().height(), 1, this.mipLevelCount);
         GpuTextureView[] var11 = new GpuTextureView[this.mipLevelCount];

         for(var12 = 0; var12 <= this.maxMipLevel; ++var12) {
            var9.uploadFirstFrame(var10, var12);
            var11[var12] = var1.createTextureView(var10);
         }

         var6.add(var11);
      }

      GpuBuffer var18 = var1.createBuffer(() -> {
         return "SpriteAnimationInfo";
      }, 128, var7);

      int var24;
      try {
         for(int var20 = 0; var20 < this.mipLevelCount; ++var20) {
            RenderPass var22 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
               return "Animate " + String.valueOf(this.location);
            }, this.mipViews[var20], OptionalInt.empty());

            try {
               var22.setPipeline(RenderPipelines.ANIMATE_SPRITE_BLIT);

               for(var24 = 0; var24 < var5.size(); ++var24) {
                  var22.bindTexture("Sprite", ((GpuTextureView[])var6.get(var24))[var20], var4);
                  var22.setUniform("SpriteAnimationInfo", var18.slice((long)(var24 * var3 + var20 * var2), (long)SpriteContents.UBO_SIZE));
                  var22.draw(0, 6);
               }
            } catch (Throwable var16) {
               if (var22 != null) {
                  try {
                     var22.close();
                  } catch (Throwable var15) {
                     var16.addSuppressed(var15);
                  }
               }

               throw var16;
            }

            if (var22 != null) {
               var22.close();
            }
         }
      } catch (Throwable var17) {
         if (var18 != null) {
            try {
               var18.close();
            } catch (Throwable var14) {
               var17.addSuppressed(var14);
            }
         }

         throw var17;
      }

      if (var18 != null) {
         var18.close();
      }

      Iterator var19 = var6.iterator();

      while(var19.hasNext()) {
         GpuTextureView[] var21 = (GpuTextureView[])var19.next();
         GpuTextureView[] var23 = var21;
         var24 = var21.length;

         for(var12 = 0; var12 < var24; ++var12) {
            GpuTextureView var13 = var23[var12];
            var13.close();
            var13.texture().close();
         }
      }

      MemoryUtil.memFree(var7);
      this.uploadAnimationFrames();
   }

   public void dumpContents(Identifier var1, Path var2) throws IOException {
      String var3 = var1.toDebugFileName();
      TextureUtil.writeAsPNG(var2, var3, this.getTexture(), this.maxMipLevel, (var0) -> {
         return var0;
      });
      dumpSpriteNames(var2, var3, this.texturesByName);
   }

   private static void dumpSpriteNames(Path var0, String var1, Map<Identifier, TextureAtlasSprite> var2) {
      Path var3 = var0.resolve(var1 + ".txt");

      try {
         BufferedWriter var4 = Files.newBufferedWriter(var3);

         try {
            Iterator var5 = var2.entrySet().stream().sorted(Entry.comparingByKey()).toList().iterator();

            while(var5.hasNext()) {
               Entry var6 = (Entry)var5.next();
               TextureAtlasSprite var7 = (TextureAtlasSprite)var6.getValue();
               var4.write(String.format(Locale.ROOT, "%s\tx=%d\ty=%d\tw=%d\th=%d%n", var6.getKey(), var7.getX(), var7.getY(), var7.contents().width(), var7.contents().height()));
            }
         } catch (Throwable var9) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (IOException var10) {
         LOGGER.warn("Failed to write file {}", var3, var10);
      }

   }

   public void cycleAnimationFrames() {
      if (this.texture != null) {
         Iterator var1 = this.animatedTexturesStates.iterator();

         while(var1.hasNext()) {
            SpriteContents.AnimationState var2 = (SpriteContents.AnimationState)var1.next();
            var2.tick();
         }

         this.uploadAnimationFrames();
      }
   }

   private void uploadAnimationFrames() {
      if (this.animatedTexturesStates.stream().anyMatch(SpriteContents.AnimationState::needsToDraw)) {
         for(int var1 = 0; var1 <= this.maxMipLevel; ++var1) {
            RenderPass var2 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
               return "Animate " + String.valueOf(this.location);
            }, this.mipViews[var1], OptionalInt.empty());

            try {
               Iterator var3 = this.animatedTexturesStates.iterator();

               while(var3.hasNext()) {
                  SpriteContents.AnimationState var4 = (SpriteContents.AnimationState)var3.next();
                  if (var4.needsToDraw()) {
                     var4.drawToAtlas(var2, var4.getDrawUbo(var1));
                  }
               }
            } catch (Throwable var6) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var2 != null) {
               var2.close();
            }
         }
      }

   }

   public void tick() {
      this.cycleAnimationFrames();
   }

   public TextureAtlasSprite getSprite(Identifier var1) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.texturesByName.getOrDefault(var1, this.missingSprite);
      if (var2 == null) {
         throw new IllegalStateException("Tried to lookup sprite, but atlas is not initialized");
      } else {
         return var2;
      }
   }

   public TextureAtlasSprite missingSprite() {
      return (TextureAtlasSprite)Objects.requireNonNull(this.missingSprite, "Atlas not initialized");
   }

   public void clearTextureData() {
      this.sprites.forEach(TextureAtlasSprite::close);
      this.sprites = List.of();
      this.animatedTexturesStates = List.of();
      this.texturesByName = Map.of();
      this.missingSprite = null;
   }

   public void close() {
      super.close();
      GpuTextureView[] var1 = this.mipViews;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GpuTextureView var4 = var1[var3];
         var4.close();
      }

      Iterator var5 = this.animatedTexturesStates.iterator();

      while(var5.hasNext()) {
         SpriteContents.AnimationState var6 = (SpriteContents.AnimationState)var5.next();
         var6.close();
      }

      if (this.spriteUbos != null) {
         this.spriteUbos.close();
         this.spriteUbos = null;
      }

   }

   public Identifier location() {
      return this.location;
   }

   public int maxSupportedTextureSize() {
      return this.maxSupportedTextureSize;
   }

   int getWidth() {
      return this.width;
   }

   int getHeight() {
      return this.height;
   }
}
