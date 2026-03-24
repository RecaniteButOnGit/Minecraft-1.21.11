package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SkyRenderer implements AutoCloseable {
   private static final Identifier SUN_SPRITE = Identifier.withDefaultNamespace("sun");
   private static final Identifier END_FLASH_SPRITE = Identifier.withDefaultNamespace("end_flash");
   private static final Identifier END_SKY_LOCATION = Identifier.withDefaultNamespace("textures/environment/end_sky.png");
   private static final float SKY_DISC_RADIUS = 512.0F;
   private static final int SKY_VERTICES = 10;
   private static final int STAR_COUNT = 1500;
   private static final float SUN_SIZE = 30.0F;
   private static final float SUN_HEIGHT = 100.0F;
   private static final float MOON_SIZE = 20.0F;
   private static final float MOON_HEIGHT = 100.0F;
   private static final int SUNRISE_STEPS = 16;
   private static final int END_SKY_QUAD_COUNT = 6;
   private static final float END_FLASH_HEIGHT = 100.0F;
   private static final float END_FLASH_SCALE = 60.0F;
   private final TextureAtlas celestialsAtlas;
   private final GpuBuffer starBuffer;
   private final GpuBuffer topSkyBuffer;
   private final GpuBuffer bottomSkyBuffer;
   private final GpuBuffer endSkyBuffer;
   private final GpuBuffer sunBuffer;
   private final GpuBuffer moonBuffer;
   private final GpuBuffer sunriseBuffer;
   private final GpuBuffer endFlashBuffer;
   private final RenderSystem.AutoStorageIndexBuffer quadIndices;
   private final AbstractTexture endSkyTexture;
   private int starIndexCount;

   public SkyRenderer(TextureManager var1, AtlasManager var2) {
      super();
      this.quadIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      this.celestialsAtlas = var2.getAtlasOrThrow(AtlasIds.CELESTIALS);
      this.starBuffer = this.buildStars();
      this.endSkyBuffer = buildEndSky();
      this.endSkyTexture = this.getTexture(var1, END_SKY_LOCATION);
      this.endFlashBuffer = buildEndFlashQuad(this.celestialsAtlas);
      this.sunBuffer = buildSunQuad(this.celestialsAtlas);
      this.moonBuffer = buildMoonPhases(this.celestialsAtlas);
      this.sunriseBuffer = this.buildSunriseFan();
      ByteBufferBuilder var3 = ByteBufferBuilder.exactlySized(10 * DefaultVertexFormat.POSITION.getVertexSize());

      try {
         BufferBuilder var4 = new BufferBuilder(var3, VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
         this.buildSkyDisc(var4, 16.0F);
         MeshData var5 = var4.buildOrThrow();

         try {
            this.topSkyBuffer = RenderSystem.getDevice().createBuffer(() -> {
               return "Top sky vertex buffer";
            }, 32, var5.vertexBuffer());
         } catch (Throwable var11) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var9) {
                  var11.addSuppressed(var9);
               }
            }

            throw var11;
         }

         if (var5 != null) {
            var5.close();
         }

         var4 = new BufferBuilder(var3, VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
         this.buildSkyDisc(var4, -16.0F);
         var5 = var4.buildOrThrow();

         try {
            this.bottomSkyBuffer = RenderSystem.getDevice().createBuffer(() -> {
               return "Bottom sky vertex buffer";
            }, 32, var5.vertexBuffer());
         } catch (Throwable var12) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var10) {
                  var12.addSuppressed(var10);
               }
            }

            throw var12;
         }

         if (var5 != null) {
            var5.close();
         }
      } catch (Throwable var13) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var8) {
               var13.addSuppressed(var8);
            }
         }

         throw var13;
      }

      if (var3 != null) {
         var3.close();
      }

   }

   private AbstractTexture getTexture(TextureManager var1, Identifier var2) {
      return var1.getTexture(var2);
   }

   private GpuBuffer buildSunriseFan() {
      boolean var1 = true;
      int var2 = DefaultVertexFormat.POSITION_COLOR.getVertexSize();
      ByteBufferBuilder var3 = ByteBufferBuilder.exactlySized(18 * var2);

      GpuBuffer var16;
      try {
         BufferBuilder var4 = new BufferBuilder(var3, VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
         int var5 = ARGB.white(1.0F);
         int var6 = ARGB.white(0.0F);
         var4.addVertex(0.0F, 100.0F, 0.0F).setColor(var5);

         for(int var7 = 0; var7 <= 16; ++var7) {
            float var8 = (float)var7 * 6.2831855F / 16.0F;
            float var9 = Mth.sin((double)var8);
            float var10 = Mth.cos((double)var8);
            var4.addVertex(var9 * 120.0F, var10 * 120.0F, -var10 * 40.0F).setColor(var6);
         }

         MeshData var15 = var4.buildOrThrow();

         try {
            var16 = RenderSystem.getDevice().createBuffer(() -> {
               return "Sunrise/Sunset fan";
            }, 32, var15.vertexBuffer());
         } catch (Throwable var13) {
            if (var15 != null) {
               try {
                  var15.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (var15 != null) {
            var15.close();
         }
      } catch (Throwable var14) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }
         }

         throw var14;
      }

      if (var3 != null) {
         var3.close();
      }

      return var16;
   }

   private static GpuBuffer buildSunQuad(TextureAtlas var0) {
      return buildCelestialQuad("Sun quad", var0.getSprite(SUN_SPRITE));
   }

   private static GpuBuffer buildEndFlashQuad(TextureAtlas var0) {
      return buildCelestialQuad("End flash quad", var0.getSprite(END_FLASH_SPRITE));
   }

   private static GpuBuffer buildCelestialQuad(String var0, TextureAtlasSprite var1) {
      VertexFormat var2 = DefaultVertexFormat.POSITION_TEX;
      ByteBufferBuilder var3 = ByteBufferBuilder.exactlySized(4 * var2.getVertexSize());

      GpuBuffer var6;
      try {
         BufferBuilder var4 = new BufferBuilder(var3, VertexFormat.Mode.QUADS, var2);
         var4.addVertex(-1.0F, 0.0F, -1.0F).setUv(var1.getU0(), var1.getV0());
         var4.addVertex(1.0F, 0.0F, -1.0F).setUv(var1.getU1(), var1.getV0());
         var4.addVertex(1.0F, 0.0F, 1.0F).setUv(var1.getU1(), var1.getV1());
         var4.addVertex(-1.0F, 0.0F, 1.0F).setUv(var1.getU0(), var1.getV1());
         MeshData var5 = var4.buildOrThrow();

         try {
            var6 = RenderSystem.getDevice().createBuffer(() -> {
               return var0;
            }, 32, var5.vertexBuffer());
         } catch (Throwable var10) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (var5 != null) {
            var5.close();
         }
      } catch (Throwable var11) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var8) {
               var11.addSuppressed(var8);
            }
         }

         throw var11;
      }

      if (var3 != null) {
         var3.close();
      }

      return var6;
   }

   private static GpuBuffer buildMoonPhases(TextureAtlas var0) {
      MoonPhase[] var1 = MoonPhase.values();
      VertexFormat var2 = DefaultVertexFormat.POSITION_TEX;
      ByteBufferBuilder var3 = ByteBufferBuilder.exactlySized(var1.length * 4 * var2.getVertexSize());

      GpuBuffer var15;
      try {
         BufferBuilder var4 = new BufferBuilder(var3, VertexFormat.Mode.QUADS, var2);
         MoonPhase[] var5 = var1;
         int var6 = var1.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            MoonPhase var8 = var5[var7];
            TextureAtlasSprite var9 = var0.getSprite(Identifier.withDefaultNamespace("moon/" + var8.getSerializedName()));
            var4.addVertex(-1.0F, 0.0F, -1.0F).setUv(var9.getU1(), var9.getV1());
            var4.addVertex(1.0F, 0.0F, -1.0F).setUv(var9.getU0(), var9.getV1());
            var4.addVertex(1.0F, 0.0F, 1.0F).setUv(var9.getU0(), var9.getV0());
            var4.addVertex(-1.0F, 0.0F, 1.0F).setUv(var9.getU1(), var9.getV0());
         }

         MeshData var14 = var4.buildOrThrow();

         try {
            var15 = RenderSystem.getDevice().createBuffer(() -> {
               return "Moon phases";
            }, 32, var14.vertexBuffer());
         } catch (Throwable var12) {
            if (var14 != null) {
               try {
                  var14.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (var14 != null) {
            var14.close();
         }
      } catch (Throwable var13) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var10) {
               var13.addSuppressed(var10);
            }
         }

         throw var13;
      }

      if (var3 != null) {
         var3.close();
      }

      return var15;
   }

   private GpuBuffer buildStars() {
      RandomSource var1 = RandomSource.create(10842L);
      float var2 = 100.0F;
      ByteBufferBuilder var3 = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * 1500 * 4);

      GpuBuffer var19;
      try {
         BufferBuilder var4 = new BufferBuilder(var3, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

         for(int var5 = 0; var5 < 1500; ++var5) {
            float var6 = var1.nextFloat() * 2.0F - 1.0F;
            float var7 = var1.nextFloat() * 2.0F - 1.0F;
            float var8 = var1.nextFloat() * 2.0F - 1.0F;
            float var9 = 0.15F + var1.nextFloat() * 0.1F;
            float var10 = Mth.lengthSquared(var6, var7, var8);
            if (!(var10 <= 0.010000001F) && !(var10 >= 1.0F)) {
               Vector3f var11 = (new Vector3f(var6, var7, var8)).normalize(100.0F);
               float var12 = (float)(var1.nextDouble() * 3.1415927410125732D * 2.0D);
               Matrix3f var13 = (new Matrix3f()).rotateTowards((new Vector3f(var11)).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-var12);
               var4.addVertex((new Vector3f(var9, -var9, 0.0F)).mul(var13).add(var11));
               var4.addVertex((new Vector3f(var9, var9, 0.0F)).mul(var13).add(var11));
               var4.addVertex((new Vector3f(-var9, var9, 0.0F)).mul(var13).add(var11));
               var4.addVertex((new Vector3f(-var9, -var9, 0.0F)).mul(var13).add(var11));
            }
         }

         MeshData var18 = var4.buildOrThrow();

         try {
            this.starIndexCount = var18.drawState().indexCount();
            var19 = RenderSystem.getDevice().createBuffer(() -> {
               return "Stars vertex buffer";
            }, 40, var18.vertexBuffer());
         } catch (Throwable var16) {
            if (var18 != null) {
               try {
                  var18.close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }
            }

            throw var16;
         }

         if (var18 != null) {
            var18.close();
         }
      } catch (Throwable var17) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var14) {
               var17.addSuppressed(var14);
            }
         }

         throw var17;
      }

      if (var3 != null) {
         var3.close();
      }

      return var19;
   }

   private void buildSkyDisc(VertexConsumer var1, float var2) {
      float var3 = Math.signum(var2) * 512.0F;
      var1.addVertex(0.0F, var2, 0.0F);

      for(int var4 = -180; var4 <= 180; var4 += 45) {
         var1.addVertex(var3 * Mth.cos((double)((float)var4 * 0.017453292F)), var2, 512.0F * Mth.sin((double)((float)var4 * 0.017453292F)));
      }

   }

   private static GpuBuffer buildEndSky() {
      ByteBufferBuilder var0 = ByteBufferBuilder.exactlySized(24 * DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize());

      GpuBuffer var10;
      try {
         BufferBuilder var1 = new BufferBuilder(var0, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

         for(int var2 = 0; var2 < 6; ++var2) {
            Matrix4f var3 = new Matrix4f();
            switch(var2) {
            case 1:
               var3.rotationX(1.5707964F);
               break;
            case 2:
               var3.rotationX(-1.5707964F);
               break;
            case 3:
               var3.rotationX(3.1415927F);
               break;
            case 4:
               var3.rotationZ(1.5707964F);
               break;
            case 5:
               var3.rotationZ(-1.5707964F);
            }

            var1.addVertex(var3, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(-14145496);
            var1.addVertex(var3, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(-14145496);
            var1.addVertex(var3, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(-14145496);
            var1.addVertex(var3, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(-14145496);
         }

         MeshData var9 = var1.buildOrThrow();

         try {
            var10 = RenderSystem.getDevice().createBuffer(() -> {
               return "End sky vertex buffer";
            }, 40, var9.vertexBuffer());
         } catch (Throwable var7) {
            if (var9 != null) {
               try {
                  var9.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var9 != null) {
            var9.close();
         }
      } catch (Throwable var8) {
         if (var0 != null) {
            try {
               var0.close();
            } catch (Throwable var5) {
               var8.addSuppressed(var5);
            }
         }

         throw var8;
      }

      if (var0 != null) {
         var0.close();
      }

      return var10;
   }

   public void renderSkyDisc(int var1) {
      GpuBufferSlice var2 = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), ARGB.vector4fFromARGB32(var1), new Vector3f(), new Matrix4f());
      GpuTextureView var3 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
      GpuTextureView var4 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
      RenderPass var5 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "Sky disc";
      }, var3, OptionalInt.empty(), var4, OptionalDouble.empty());

      try {
         var5.setPipeline(RenderPipelines.SKY);
         RenderSystem.bindDefaultUniforms(var5);
         var5.setUniform("DynamicTransforms", var2);
         var5.setVertexBuffer(0, this.topSkyBuffer);
         var5.draw(0, 10);
      } catch (Throwable var9) {
         if (var5 != null) {
            try {
               var5.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (var5 != null) {
         var5.close();
      }

   }

   public void extractRenderState(ClientLevel var1, float var2, Camera var3, SkyRenderState var4) {
      var4.skybox = var1.dimensionType().skybox();
      if (var4.skybox != DimensionType.Skybox.NONE) {
         if (var4.skybox == DimensionType.Skybox.END) {
            EndFlashState var6 = var1.endFlashState();
            if (var6 != null) {
               var4.endFlashIntensity = var6.getIntensity(var2);
               var4.endFlashXAngle = var6.getXAngle();
               var4.endFlashYAngle = var6.getYAngle();
            }
         } else {
            EnvironmentAttributeProbe var5 = var3.attributeProbe();
            var4.sunAngle = (Float)var5.getValue(EnvironmentAttributes.SUN_ANGLE, var2) * 0.017453292F;
            var4.moonAngle = (Float)var5.getValue(EnvironmentAttributes.MOON_ANGLE, var2) * 0.017453292F;
            var4.starAngle = (Float)var5.getValue(EnvironmentAttributes.STAR_ANGLE, var2) * 0.017453292F;
            var4.rainBrightness = 1.0F - var1.getRainLevel(var2);
            var4.starBrightness = (Float)var5.getValue(EnvironmentAttributes.STAR_BRIGHTNESS, var2);
            var4.sunriseAndSunsetColor = (Integer)var3.attributeProbe().getValue(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, var2);
            var4.moonPhase = (MoonPhase)var5.getValue(EnvironmentAttributes.MOON_PHASE, var2);
            var4.skyColor = (Integer)var5.getValue(EnvironmentAttributes.SKY_COLOR, var2);
            var4.shouldRenderDarkDisc = this.shouldRenderDarkDisc(var2, var1);
         }
      }
   }

   private boolean shouldRenderDarkDisc(float var1, ClientLevel var2) {
      return Minecraft.getInstance().player.getEyePosition(var1).y - var2.getLevelData().getHorizonHeight(var2) < 0.0D;
   }

   public void renderDarkDisc() {
      Matrix4fStack var1 = RenderSystem.getModelViewStack();
      var1.pushMatrix();
      var1.translate(0.0F, 12.0F, 0.0F);
      GpuBufferSlice var2 = RenderSystem.getDynamicUniforms().writeTransform(var1, new Vector4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(), new Matrix4f());
      GpuTextureView var3 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
      GpuTextureView var4 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
      RenderPass var5 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "Sky dark";
      }, var3, OptionalInt.empty(), var4, OptionalDouble.empty());

      try {
         var5.setPipeline(RenderPipelines.SKY);
         RenderSystem.bindDefaultUniforms(var5);
         var5.setUniform("DynamicTransforms", var2);
         var5.setVertexBuffer(0, this.bottomSkyBuffer);
         var5.draw(0, 10);
      } catch (Throwable var9) {
         if (var5 != null) {
            try {
               var5.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (var5 != null) {
         var5.close();
      }

      var1.popMatrix();
   }

   public void renderSunMoonAndStars(PoseStack var1, float var2, float var3, float var4, MoonPhase var5, float var6, float var7) {
      var1.pushPose();
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-90.0F));
      var1.pushPose();
      var1.mulPose((Quaternionfc)Axis.XP.rotation(var2));
      this.renderSun(var6, var1);
      var1.popPose();
      var1.pushPose();
      var1.mulPose((Quaternionfc)Axis.XP.rotation(var3));
      this.renderMoon(var5, var6, var1);
      var1.popPose();
      if (var7 > 0.0F) {
         var1.pushPose();
         var1.mulPose((Quaternionfc)Axis.XP.rotation(var4));
         this.renderStars(var7, var1);
         var1.popPose();
      }

      var1.popPose();
   }

   private void renderSun(float var1, PoseStack var2) {
      Matrix4fStack var3 = RenderSystem.getModelViewStack();
      var3.pushMatrix();
      var3.mul(var2.last().pose());
      var3.translate(0.0F, 100.0F, 0.0F);
      var3.scale(30.0F, 1.0F, 30.0F);
      GpuBufferSlice var4 = RenderSystem.getDynamicUniforms().writeTransform(var3, new Vector4f(1.0F, 1.0F, 1.0F, var1), new Vector3f(), new Matrix4f());
      GpuTextureView var5 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
      GpuTextureView var6 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
      GpuBuffer var7 = this.quadIndices.getBuffer(6);
      RenderPass var8 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "Sky sun";
      }, var5, OptionalInt.empty(), var6, OptionalDouble.empty());

      try {
         var8.setPipeline(RenderPipelines.CELESTIAL);
         RenderSystem.bindDefaultUniforms(var8);
         var8.setUniform("DynamicTransforms", var4);
         var8.bindTexture("Sampler0", this.celestialsAtlas.getTextureView(), this.celestialsAtlas.getSampler());
         var8.setVertexBuffer(0, this.sunBuffer);
         var8.setIndexBuffer(var7, this.quadIndices.type());
         var8.drawIndexed(0, 0, 6, 1);
      } catch (Throwable var12) {
         if (var8 != null) {
            try {
               var8.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (var8 != null) {
         var8.close();
      }

      var3.popMatrix();
   }

   private void renderMoon(MoonPhase var1, float var2, PoseStack var3) {
      int var4 = var1.index() * 4;
      Matrix4fStack var5 = RenderSystem.getModelViewStack();
      var5.pushMatrix();
      var5.mul(var3.last().pose());
      var5.translate(0.0F, 100.0F, 0.0F);
      var5.scale(20.0F, 1.0F, 20.0F);
      GpuBufferSlice var6 = RenderSystem.getDynamicUniforms().writeTransform(var5, new Vector4f(1.0F, 1.0F, 1.0F, var2), new Vector3f(), new Matrix4f());
      GpuTextureView var7 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
      GpuTextureView var8 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
      GpuBuffer var9 = this.quadIndices.getBuffer(6);
      RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "Sky moon";
      }, var7, OptionalInt.empty(), var8, OptionalDouble.empty());

      try {
         var10.setPipeline(RenderPipelines.CELESTIAL);
         RenderSystem.bindDefaultUniforms(var10);
         var10.setUniform("DynamicTransforms", var6);
         var10.bindTexture("Sampler0", this.celestialsAtlas.getTextureView(), this.celestialsAtlas.getSampler());
         var10.setVertexBuffer(0, this.moonBuffer);
         var10.setIndexBuffer(var9, this.quadIndices.type());
         var10.drawIndexed(var4, 0, 6, 1);
      } catch (Throwable var14) {
         if (var10 != null) {
            try {
               var10.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }
         }

         throw var14;
      }

      if (var10 != null) {
         var10.close();
      }

      var5.popMatrix();
   }

   private void renderStars(float var1, PoseStack var2) {
      Matrix4fStack var3 = RenderSystem.getModelViewStack();
      var3.pushMatrix();
      var3.mul(var2.last().pose());
      RenderPipeline var4 = RenderPipelines.STARS;
      GpuTextureView var5 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
      GpuTextureView var6 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
      GpuBuffer var7 = this.quadIndices.getBuffer(this.starIndexCount);
      GpuBufferSlice var8 = RenderSystem.getDynamicUniforms().writeTransform(var3, new Vector4f(var1, var1, var1, var1), new Vector3f(), new Matrix4f());
      RenderPass var9 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "Stars";
      }, var5, OptionalInt.empty(), var6, OptionalDouble.empty());

      try {
         var9.setPipeline(var4);
         RenderSystem.bindDefaultUniforms(var9);
         var9.setUniform("DynamicTransforms", var8);
         var9.setVertexBuffer(0, this.starBuffer);
         var9.setIndexBuffer(var7, this.quadIndices.type());
         var9.drawIndexed(0, 0, this.starIndexCount, 1);
      } catch (Throwable var13) {
         if (var9 != null) {
            try {
               var9.close();
            } catch (Throwable var12) {
               var13.addSuppressed(var12);
            }
         }

         throw var13;
      }

      if (var9 != null) {
         var9.close();
      }

      var3.popMatrix();
   }

   public void renderSunriseAndSunset(PoseStack var1, float var2, int var3) {
      float var4 = ARGB.alphaFloat(var3);
      if (!(var4 <= 0.001F)) {
         var1.pushPose();
         var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0F));
         float var5 = Mth.sin((double)var2) < 0.0F ? 180.0F : 0.0F;
         var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(var5 + 90.0F));
         Matrix4fStack var6 = RenderSystem.getModelViewStack();
         var6.pushMatrix();
         var6.mul(var1.last().pose());
         var6.scale(1.0F, 1.0F, var4);
         GpuBufferSlice var7 = RenderSystem.getDynamicUniforms().writeTransform(var6, ARGB.vector4fFromARGB32(var3), new Vector3f(), new Matrix4f());
         GpuTextureView var8 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
         GpuTextureView var9 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
         RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
            return "Sunrise sunset";
         }, var8, OptionalInt.empty(), var9, OptionalDouble.empty());

         try {
            var10.setPipeline(RenderPipelines.SUNRISE_SUNSET);
            RenderSystem.bindDefaultUniforms(var10);
            var10.setUniform("DynamicTransforms", var7);
            var10.setVertexBuffer(0, this.sunriseBuffer);
            var10.draw(0, 18);
         } catch (Throwable var14) {
            if (var10 != null) {
               try {
                  var10.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }
            }

            throw var14;
         }

         if (var10 != null) {
            var10.close();
         }

         var6.popMatrix();
         var1.popPose();
      }
   }

   public void renderEndSky() {
      RenderSystem.AutoStorageIndexBuffer var1 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var2 = var1.getBuffer(36);
      GpuTextureView var3 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
      GpuTextureView var4 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
      GpuBufferSlice var5 = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());
      RenderPass var6 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "End sky";
      }, var3, OptionalInt.empty(), var4, OptionalDouble.empty());

      try {
         var6.setPipeline(RenderPipelines.END_SKY);
         RenderSystem.bindDefaultUniforms(var6);
         var6.setUniform("DynamicTransforms", var5);
         var6.bindTexture("Sampler0", this.endSkyTexture.getTextureView(), this.endSkyTexture.getSampler());
         var6.setVertexBuffer(0, this.endSkyBuffer);
         var6.setIndexBuffer(var2, var1.type());
         var6.drawIndexed(0, 0, 36, 1);
      } catch (Throwable var10) {
         if (var6 != null) {
            try {
               var6.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (var6 != null) {
         var6.close();
      }

   }

   public void renderEndFlash(PoseStack var1, float var2, float var3, float var4) {
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - var4));
      var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-90.0F - var3));
      Matrix4fStack var5 = RenderSystem.getModelViewStack();
      var5.pushMatrix();
      var5.mul(var1.last().pose());
      var5.translate(0.0F, 100.0F, 0.0F);
      var5.scale(60.0F, 1.0F, 60.0F);
      GpuBufferSlice var6 = RenderSystem.getDynamicUniforms().writeTransform(var5, new Vector4f(var2, var2, var2, var2), new Vector3f(), new Matrix4f());
      GpuTextureView var7 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
      GpuTextureView var8 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
      GpuBuffer var9 = this.quadIndices.getBuffer(6);
      RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "End flash";
      }, var7, OptionalInt.empty(), var8, OptionalDouble.empty());

      try {
         var10.setPipeline(RenderPipelines.CELESTIAL);
         RenderSystem.bindDefaultUniforms(var10);
         var10.setUniform("DynamicTransforms", var6);
         var10.bindTexture("Sampler0", this.celestialsAtlas.getTextureView(), this.celestialsAtlas.getSampler());
         var10.setVertexBuffer(0, this.endFlashBuffer);
         var10.setIndexBuffer(var9, this.quadIndices.type());
         var10.drawIndexed(0, 0, 6, 1);
      } catch (Throwable var14) {
         if (var10 != null) {
            try {
               var10.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }
         }

         throw var14;
      }

      if (var10 != null) {
         var10.close();
      }

      var5.popMatrix();
   }

   public void close() {
      this.sunBuffer.close();
      this.moonBuffer.close();
      this.starBuffer.close();
      this.topSkyBuffer.close();
      this.bottomSkyBuffer.close();
      this.endSkyBuffer.close();
      this.sunriseBuffer.close();
      this.endFlashBuffer.close();
   }
}
