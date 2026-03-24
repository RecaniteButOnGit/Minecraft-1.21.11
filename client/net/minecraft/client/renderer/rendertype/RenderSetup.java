package net.minecraft.client.renderer.rendertype;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class RenderSetup {
   final RenderPipeline pipeline;
   final Map<String, RenderSetup.TextureBinding> textures;
   final TextureTransform textureTransform;
   final OutputTarget outputTarget;
   final RenderSetup.OutlineProperty outlineProperty;
   final boolean useLightmap;
   final boolean useOverlay;
   final boolean affectsCrumbling;
   final boolean sortOnUpload;
   final int bufferSize;
   final LayeringTransform layeringTransform;

   RenderSetup(RenderPipeline var1, Map<String, RenderSetup.TextureBinding> var2, boolean var3, boolean var4, LayeringTransform var5, OutputTarget var6, TextureTransform var7, RenderSetup.OutlineProperty var8, boolean var9, boolean var10, int var11) {
      super();
      this.pipeline = var1;
      this.textures = var2;
      this.outputTarget = var6;
      this.textureTransform = var7;
      this.useLightmap = var3;
      this.useOverlay = var4;
      this.outlineProperty = var8;
      this.layeringTransform = var5;
      this.affectsCrumbling = var9;
      this.sortOnUpload = var10;
      this.bufferSize = var11;
   }

   public String toString() {
      String var10000 = String.valueOf(this.layeringTransform);
      return "RenderSetup[layeringTransform=" + var10000 + ", textureTransform=" + String.valueOf(this.textureTransform) + ", textures=" + String.valueOf(this.textures) + ", outlineProperty=" + String.valueOf(this.outlineProperty) + ", useLightmap=" + this.useLightmap + ", useOverlay=" + this.useOverlay + "]";
   }

   public static RenderSetup.RenderSetupBuilder builder(RenderPipeline var0) {
      return new RenderSetup.RenderSetupBuilder(var0);
   }

   public Map<String, RenderSetup.TextureAndSampler> getTextures() {
      if (this.textures.isEmpty() && !this.useOverlay && !this.useLightmap) {
         return Collections.emptyMap();
      } else {
         HashMap var1 = new HashMap();
         if (this.useOverlay) {
            var1.put("Sampler1", new RenderSetup.TextureAndSampler(Minecraft.getInstance().gameRenderer.overlayTexture().getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)));
         }

         if (this.useLightmap) {
            var1.put("Sampler2", new RenderSetup.TextureAndSampler(Minecraft.getInstance().gameRenderer.lightTexture().getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)));
         }

         TextureManager var2 = Minecraft.getInstance().getTextureManager();
         Iterator var3 = this.textures.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            AbstractTexture var5 = var2.getTexture(((RenderSetup.TextureBinding)var4.getValue()).location);
            GpuSampler var6 = (GpuSampler)((RenderSetup.TextureBinding)var4.getValue()).sampler().get();
            var1.put((String)var4.getKey(), new RenderSetup.TextureAndSampler(var5.getTextureView(), var6 != null ? var6 : var5.getSampler()));
         }

         return var1;
      }
   }

   public static enum OutlineProperty {
      NONE("none"),
      IS_OUTLINE("is_outline"),
      AFFECTS_OUTLINE("affects_outline");

      private final String name;

      private OutlineProperty(final String param3) {
         this.name = var3;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static RenderSetup.OutlineProperty[] $values() {
         return new RenderSetup.OutlineProperty[]{NONE, IS_OUTLINE, AFFECTS_OUTLINE};
      }
   }

   public static class RenderSetupBuilder {
      private final RenderPipeline pipeline;
      private boolean useLightmap = false;
      private boolean useOverlay = false;
      private LayeringTransform layeringTransform;
      private OutputTarget outputTarget;
      private TextureTransform textureTransform;
      private boolean affectsCrumbling;
      private boolean sortOnUpload;
      private int bufferSize;
      private RenderSetup.OutlineProperty outlineProperty;
      private final Map<String, RenderSetup.TextureBinding> textures;

      RenderSetupBuilder(RenderPipeline var1) {
         super();
         this.layeringTransform = LayeringTransform.NO_LAYERING;
         this.outputTarget = OutputTarget.MAIN_TARGET;
         this.textureTransform = TextureTransform.DEFAULT_TEXTURING;
         this.affectsCrumbling = false;
         this.sortOnUpload = false;
         this.bufferSize = 1536;
         this.outlineProperty = RenderSetup.OutlineProperty.NONE;
         this.textures = new HashMap();
         this.pipeline = var1;
      }

      public RenderSetup.RenderSetupBuilder withTexture(String var1, Identifier var2) {
         this.textures.put(var1, new RenderSetup.TextureBinding(var2, () -> {
            return null;
         }));
         return this;
      }

      public RenderSetup.RenderSetupBuilder withTexture(String var1, Identifier var2, @Nullable Supplier<GpuSampler> var3) {
         this.textures.put(var1, new RenderSetup.TextureBinding(var2, Suppliers.memoize(() -> {
            return var3 == null ? null : (GpuSampler)var3.get();
         })));
         return this;
      }

      public RenderSetup.RenderSetupBuilder useLightmap() {
         this.useLightmap = true;
         return this;
      }

      public RenderSetup.RenderSetupBuilder useOverlay() {
         this.useOverlay = true;
         return this;
      }

      public RenderSetup.RenderSetupBuilder affectsCrumbling() {
         this.affectsCrumbling = true;
         return this;
      }

      public RenderSetup.RenderSetupBuilder sortOnUpload() {
         this.sortOnUpload = true;
         return this;
      }

      public RenderSetup.RenderSetupBuilder bufferSize(int var1) {
         this.bufferSize = var1;
         return this;
      }

      public RenderSetup.RenderSetupBuilder setLayeringTransform(LayeringTransform var1) {
         this.layeringTransform = var1;
         return this;
      }

      public RenderSetup.RenderSetupBuilder setOutputTarget(OutputTarget var1) {
         this.outputTarget = var1;
         return this;
      }

      public RenderSetup.RenderSetupBuilder setTextureTransform(TextureTransform var1) {
         this.textureTransform = var1;
         return this;
      }

      public RenderSetup.RenderSetupBuilder setOutline(RenderSetup.OutlineProperty var1) {
         this.outlineProperty = var1;
         return this;
      }

      public RenderSetup createRenderSetup() {
         return new RenderSetup(this.pipeline, this.textures, this.useLightmap, this.useOverlay, this.layeringTransform, this.outputTarget, this.textureTransform, this.outlineProperty, this.affectsCrumbling, this.sortOnUpload, this.bufferSize);
      }
   }

   public static record TextureAndSampler(GpuTextureView textureView, GpuSampler sampler) {
      public TextureAndSampler(GpuTextureView param1, GpuSampler param2) {
         super();
         this.textureView = var1;
         this.sampler = var2;
      }

      public GpuTextureView textureView() {
         return this.textureView;
      }

      public GpuSampler sampler() {
         return this.sampler;
      }
   }

   static record TextureBinding(Identifier location, Supplier<GpuSampler> sampler) {
      final Identifier location;

      TextureBinding(Identifier param1, Supplier<GpuSampler> param2) {
         super();
         this.location = var1;
         this.sampler = var2;
      }

      public Identifier location() {
         return this.location;
      }

      public Supplier<GpuSampler> sampler() {
         return this.sampler;
      }
   }
}
