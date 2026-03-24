package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public record GlyphRenderState(Matrix3x2fc pose, TextRenderable renderable, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {
   public GlyphRenderState(Matrix3x2fc param1, TextRenderable param2, @Nullable ScreenRectangle param3) {
      super();
      this.pose = var1;
      this.renderable = var2;
      this.scissorArea = var3;
   }

   public void buildVertices(VertexConsumer var1) {
      this.renderable.render((new Matrix4f()).mul(this.pose), var1, 15728880, true);
   }

   public RenderPipeline pipeline() {
      return this.renderable.guiPipeline();
   }

   public TextureSetup textureSetup() {
      return TextureSetup.singleTextureWithLightmap(this.renderable.textureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
   }

   @Nullable
   public ScreenRectangle bounds() {
      return null;
   }

   public Matrix3x2fc pose() {
      return this.pose;
   }

   public TextRenderable renderable() {
      return this.renderable;
   }

   @Nullable
   public ScreenRectangle scissorArea() {
      return this.scissorArea;
   }
}
