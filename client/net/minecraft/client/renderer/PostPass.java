package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.SamplerCache;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.lwjgl.system.MemoryStack;

public class PostPass implements AutoCloseable {
   private static final int UBO_SIZE_PER_SAMPLER = (new Std140SizeCalculator()).putVec2().get();
   private final String name;
   private final RenderPipeline pipeline;
   private final Identifier outputTargetId;
   private final Map<String, GpuBuffer> customUniforms = new HashMap();
   private final MappableRingBuffer infoUbo;
   private final List<PostPass.Input> inputs;

   public PostPass(RenderPipeline var1, Identifier var2, Map<String, List<UniformValue>> var3, List<PostPass.Input> var4) {
      super();
      this.pipeline = var1;
      this.name = var1.getLocation().toString();
      this.outputTargetId = var2;
      this.inputs = var4;
      Iterator var5 = var3.entrySet().iterator();

      while(true) {
         Entry var6;
         List var7;
         do {
            if (!var5.hasNext()) {
               this.infoUbo = new MappableRingBuffer(() -> {
                  return this.name + " SamplerInfo";
               }, 130, (var4.size() + 1) * UBO_SIZE_PER_SAMPLER);
               return;
            }

            var6 = (Entry)var5.next();
            var7 = (List)var6.getValue();
         } while(var7.isEmpty());

         Std140SizeCalculator var8 = new Std140SizeCalculator();
         Iterator var9 = var7.iterator();

         while(var9.hasNext()) {
            UniformValue var10 = (UniformValue)var9.next();
            var10.addSize(var8);
         }

         int var16 = var8.get();
         MemoryStack var17 = MemoryStack.stackPush();

         try {
            Std140Builder var11 = Std140Builder.onStack(var17, var16);
            Iterator var12 = var7.iterator();

            while(true) {
               if (!var12.hasNext()) {
                  this.customUniforms.put((String)var6.getKey(), RenderSystem.getDevice().createBuffer(() -> {
                     String var10000 = this.name;
                     return var10000 + " / " + (String)var6.getKey();
                  }, 128, var11.get()));
                  break;
               }

               UniformValue var13 = (UniformValue)var12.next();
               var13.writeTo(var11);
            }
         } catch (Throwable var15) {
            if (var17 != null) {
               try {
                  var17.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }
            }

            throw var15;
         }

         if (var17 != null) {
            var17.close();
         }
      }
   }

   public void addToFrame(FrameGraphBuilder var1, Map<Identifier, ResourceHandle<RenderTarget>> var2, GpuBufferSlice var3) {
      FramePass var4 = var1.addPass(this.name);
      Iterator var5 = this.inputs.iterator();

      while(var5.hasNext()) {
         PostPass.Input var6 = (PostPass.Input)var5.next();
         var6.addToPass(var4, var2);
      }

      ResourceHandle var7 = (ResourceHandle)var2.computeIfPresent(this.outputTargetId, (var1x, var2x) -> {
         return var4.readsAndWrites(var2x);
      });
      if (var7 == null) {
         throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
      } else {
         var4.executes(() -> {
            RenderTarget var4 = (RenderTarget)var7.get();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(var3, ProjectionType.ORTHOGRAPHIC);
            CommandEncoder var5 = RenderSystem.getDevice().createCommandEncoder();
            SamplerCache var6 = RenderSystem.getSamplerCache();
            List var7x = this.inputs.stream().map((var2x) -> {
               return new PostPass.InputTexture(var2x.samplerName(), var2x.texture(var2), var6.getClampToEdge(var2x.bilinear() ? FilterMode.LINEAR : FilterMode.NEAREST));
            }).toList();
            GpuBuffer.MappedView var8 = var5.mapBuffer(this.infoUbo.currentBuffer(), false, true);

            try {
               Std140Builder var9 = Std140Builder.intoBuffer(var8.data());
               var9.putVec2((float)var4.width, (float)var4.height);
               Iterator var10 = var7x.iterator();

               while(var10.hasNext()) {
                  PostPass.InputTexture var11 = (PostPass.InputTexture)var10.next();
                  var9.putVec2((float)var11.view.getWidth(0), (float)var11.view.getHeight(0));
               }
            } catch (Throwable var15) {
               if (var8 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var13) {
                     var15.addSuppressed(var13);
                  }
               }

               throw var15;
            }

            if (var8 != null) {
               var8.close();
            }

            RenderPass var16 = var5.createRenderPass(() -> {
               return "Post pass " + this.name;
            }, var4.getColorTextureView(), OptionalInt.empty(), var4.useDepth ? var4.getDepthTextureView() : null, OptionalDouble.empty());

            try {
               var16.setPipeline(this.pipeline);
               RenderSystem.bindDefaultUniforms(var16);
               var16.setUniform("SamplerInfo", this.infoUbo.currentBuffer());
               Iterator var18 = this.customUniforms.entrySet().iterator();

               while(var18.hasNext()) {
                  Entry var20 = (Entry)var18.next();
                  var16.setUniform((String)var20.getKey(), (GpuBuffer)var20.getValue());
               }

               var18 = var7x.iterator();

               while(true) {
                  if (!var18.hasNext()) {
                     var16.draw(0, 3);
                     break;
                  }

                  PostPass.InputTexture var21 = (PostPass.InputTexture)var18.next();
                  var16.bindTexture(var21.samplerName() + "Sampler", var21.view(), var21.sampler());
               }
            } catch (Throwable var14) {
               if (var16 != null) {
                  try {
                     var16.close();
                  } catch (Throwable var12) {
                     var14.addSuppressed(var12);
                  }
               }

               throw var14;
            }

            if (var16 != null) {
               var16.close();
            }

            this.infoUbo.rotate();
            RenderSystem.restoreProjectionMatrix();
            Iterator var17 = this.inputs.iterator();

            while(var17.hasNext()) {
               PostPass.Input var19 = (PostPass.Input)var17.next();
               var19.cleanup(var2);
            }

         });
      }
   }

   public void close() {
      Iterator var1 = this.customUniforms.values().iterator();

      while(var1.hasNext()) {
         GpuBuffer var2 = (GpuBuffer)var1.next();
         var2.close();
      }

      this.infoUbo.close();
   }

   public interface Input {
      void addToPass(FramePass var1, Map<Identifier, ResourceHandle<RenderTarget>> var2);

      default void cleanup(Map<Identifier, ResourceHandle<RenderTarget>> var1) {
      }

      GpuTextureView texture(Map<Identifier, ResourceHandle<RenderTarget>> var1);

      String samplerName();

      boolean bilinear();
   }

   static record InputTexture(String samplerName, GpuTextureView view, GpuSampler sampler) {
      final GpuTextureView view;

      InputTexture(String param1, GpuTextureView param2, GpuSampler param3) {
         super();
         this.samplerName = var1;
         this.view = var2;
         this.sampler = var3;
      }

      public String samplerName() {
         return this.samplerName;
      }

      public GpuTextureView view() {
         return this.view;
      }

      public GpuSampler sampler() {
         return this.sampler;
      }
   }

   public static record TargetInput(String samplerName, Identifier targetId, boolean depthBuffer, boolean bilinear) implements PostPass.Input {
      public TargetInput(String param1, Identifier param2, boolean param3, boolean param4) {
         super();
         this.samplerName = var1;
         this.targetId = var2;
         this.depthBuffer = var3;
         this.bilinear = var4;
      }

      private ResourceHandle<RenderTarget> getHandle(Map<Identifier, ResourceHandle<RenderTarget>> var1) {
         ResourceHandle var2 = (ResourceHandle)var1.get(this.targetId);
         if (var2 == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
         } else {
            return var2;
         }
      }

      public void addToPass(FramePass var1, Map<Identifier, ResourceHandle<RenderTarget>> var2) {
         var1.reads(this.getHandle(var2));
      }

      public GpuTextureView texture(Map<Identifier, ResourceHandle<RenderTarget>> var1) {
         ResourceHandle var2 = this.getHandle(var1);
         RenderTarget var3 = (RenderTarget)var2.get();
         GpuTextureView var4 = this.depthBuffer ? var3.getDepthTextureView() : var3.getColorTextureView();
         if (var4 == null) {
            String var10002 = this.depthBuffer ? "depth" : "color";
            throw new IllegalStateException("Missing " + var10002 + "texture for target " + String.valueOf(this.targetId));
         } else {
            return var4;
         }
      }

      public String samplerName() {
         return this.samplerName;
      }

      public Identifier targetId() {
         return this.targetId;
      }

      public boolean depthBuffer() {
         return this.depthBuffer;
      }

      public boolean bilinear() {
         return this.bilinear;
      }
   }

   public static record TextureInput(String samplerName, AbstractTexture texture, int width, int height, boolean bilinear) implements PostPass.Input {
      public TextureInput(String param1, AbstractTexture param2, int param3, int param4, boolean param5) {
         super();
         this.samplerName = var1;
         this.texture = var2;
         this.width = var3;
         this.height = var4;
         this.bilinear = var5;
      }

      public void addToPass(FramePass var1, Map<Identifier, ResourceHandle<RenderTarget>> var2) {
      }

      public GpuTextureView texture(Map<Identifier, ResourceHandle<RenderTarget>> var1) {
         return this.texture.getTextureView();
      }

      public boolean bilinear() {
         return this.bilinear;
      }

      public String samplerName() {
         return this.samplerName;
      }

      public AbstractTexture texture() {
         return this.texture;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }
   }
}
