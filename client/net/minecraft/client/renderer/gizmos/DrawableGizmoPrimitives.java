package net.minecraft.client.renderer.gizmos;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.gizmos.GizmoPrimitives;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector4f;

public class DrawableGizmoPrimitives implements GizmoPrimitives {
   private final DrawableGizmoPrimitives.Group opaque = new DrawableGizmoPrimitives.Group(true);
   private final DrawableGizmoPrimitives.Group translucent = new DrawableGizmoPrimitives.Group(false);
   private boolean isEmpty = true;

   public DrawableGizmoPrimitives() {
      super();
   }

   private DrawableGizmoPrimitives.Group getGroup(int var1) {
      return ARGB.alpha(var1) < 255 ? this.translucent : this.opaque;
   }

   public void addPoint(Vec3 var1, int var2, float var3) {
      this.getGroup(var2).points.add(new DrawableGizmoPrimitives.Point(var1, var2, var3));
      this.isEmpty = false;
   }

   public void addLine(Vec3 var1, Vec3 var2, int var3, float var4) {
      this.getGroup(var3).lines.add(new DrawableGizmoPrimitives.Line(var1, var2, var3, var4));
      this.isEmpty = false;
   }

   public void addTriangleFan(Vec3[] var1, int var2) {
      this.getGroup(var2).triangleFans.add(new DrawableGizmoPrimitives.TriangleFan(var1, var2));
      this.isEmpty = false;
   }

   public void addQuad(Vec3 var1, Vec3 var2, Vec3 var3, Vec3 var4, int var5) {
      this.getGroup(var5).quads.add(new DrawableGizmoPrimitives.Quad(var1, var2, var3, var4, var5));
      this.isEmpty = false;
   }

   public void addText(Vec3 var1, String var2, TextGizmo.Style var3) {
      this.getGroup(var3.color()).texts.add(new DrawableGizmoPrimitives.Text(var1, var2, var3));
      this.isEmpty = false;
   }

   public void render(PoseStack var1, MultiBufferSource var2, CameraRenderState var3, Matrix4f var4) {
      this.opaque.render(var1, var2, var3, var4);
      this.translucent.render(var1, var2, var3, var4);
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   static record Group(boolean opaque, List<DrawableGizmoPrimitives.Line> lines, List<DrawableGizmoPrimitives.Quad> quads, List<DrawableGizmoPrimitives.TriangleFan> triangleFans, List<DrawableGizmoPrimitives.Text> texts, List<DrawableGizmoPrimitives.Point> points) {
      final List<DrawableGizmoPrimitives.Line> lines;
      final List<DrawableGizmoPrimitives.Quad> quads;
      final List<DrawableGizmoPrimitives.TriangleFan> triangleFans;
      final List<DrawableGizmoPrimitives.Text> texts;
      final List<DrawableGizmoPrimitives.Point> points;

      Group(boolean var1) {
         this(var1, new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList());
      }

      private Group(boolean param1, List<DrawableGizmoPrimitives.Line> param2, List<DrawableGizmoPrimitives.Quad> param3, List<DrawableGizmoPrimitives.TriangleFan> param4, List<DrawableGizmoPrimitives.Text> param5, List<DrawableGizmoPrimitives.Point> param6) {
         super();
         this.opaque = var1;
         this.lines = var2;
         this.quads = var3;
         this.triangleFans = var4;
         this.texts = var5;
         this.points = var6;
      }

      public void render(PoseStack var1, MultiBufferSource var2, CameraRenderState var3, Matrix4f var4) {
         this.renderQuads(var1, var2, var3);
         this.renderTriangleFans(var1, var2, var3);
         this.renderLines(var1, var2, var3, var4);
         this.renderTexts(var1, var2, var3);
         this.renderPoints(var1, var2, var3);
      }

      private void renderTexts(PoseStack var1, MultiBufferSource var2, CameraRenderState var3) {
         Minecraft var4 = Minecraft.getInstance();
         Font var5 = var4.font;
         if (var3.initialized) {
            double var6 = var3.pos.x();
            double var8 = var3.pos.y();
            double var10 = var3.pos.z();
            Iterator var12 = this.texts.iterator();

            while(var12.hasNext()) {
               DrawableGizmoPrimitives.Text var13 = (DrawableGizmoPrimitives.Text)var12.next();
               var1.pushPose();
               var1.translate((float)(var13.pos().x() - var6), (float)(var13.pos().y() - var8), (float)(var13.pos().z() - var10));
               var1.mulPose((Quaternionfc)var3.orientation);
               var1.scale(var13.style.scale() / 16.0F, -var13.style.scale() / 16.0F, var13.style.scale() / 16.0F);
               float var14;
               if (var13.style.adjustLeft().isEmpty()) {
                  var14 = (float)(-var5.width(var13.text)) / 2.0F;
               } else {
                  var14 = (float)(-var13.style.adjustLeft().getAsDouble()) / var13.style.scale();
               }

               var5.drawInBatch((String)var13.text, var14, 0.0F, var13.style.color(), false, var1.last().pose(), var2, Font.DisplayMode.NORMAL, 0, 15728880);
               var1.popPose();
            }

         }
      }

      private void renderLines(PoseStack var1, MultiBufferSource var2, CameraRenderState var3, Matrix4f var4) {
         VertexConsumer var5 = var2.getBuffer(this.opaque ? RenderTypes.lines() : RenderTypes.linesTranslucent());
         PoseStack.Pose var6 = var1.last();
         Vector4f var7 = new Vector4f();
         Vector4f var8 = new Vector4f();
         Vector4f var9 = new Vector4f();
         Vector4f var10 = new Vector4f();
         Vector4f var11 = new Vector4f();
         double var12 = var3.pos.x();
         double var14 = var3.pos.y();
         double var16 = var3.pos.z();
         Iterator var18 = this.lines.iterator();

         while(true) {
            DrawableGizmoPrimitives.Line var19;
            while(true) {
               boolean var20;
               boolean var21;
               do {
                  if (!var18.hasNext()) {
                     return;
                  }

                  var19 = (DrawableGizmoPrimitives.Line)var18.next();
                  var7.set(var19.start().x() - var12, var19.start().y() - var14, var19.start().z() - var16, 1.0D);
                  var8.set(var19.end().x() - var12, var19.end().y() - var14, var19.end().z() - var16, 1.0D);
                  var7.mul(var4, var9);
                  var8.mul(var4, var10);
                  var20 = var9.z > -0.05F;
                  var21 = var10.z > -0.05F;
               } while(var20 && var21);

               if (!var20 && !var21) {
                  break;
               }

               float var22 = var10.z - var9.z;
               if (!(Math.abs(var22) < 1.0E-9F)) {
                  float var23 = Mth.clamp((-0.05F - var9.z) / var22, 0.0F, 1.0F);
                  var7.lerp(var8, var23, var11);
                  if (var20) {
                     var7.set(var11);
                  } else {
                     var8.set(var11);
                  }
                  break;
               }
            }

            var5.addVertex(var6, var7.x, var7.y, var7.z).setNormal(var6, var8.x - var7.x, var8.y - var7.y, var8.z - var7.z).setColor(var19.color()).setLineWidth(var19.width());
            var5.addVertex(var6, var8.x, var8.y, var8.z).setNormal(var6, var8.x - var7.x, var8.y - var7.y, var8.z - var7.z).setColor(var19.color()).setLineWidth(var19.width());
         }
      }

      private void renderTriangleFans(PoseStack var1, MultiBufferSource var2, CameraRenderState var3) {
         PoseStack.Pose var4 = var1.last();
         double var5 = var3.pos.x();
         double var7 = var3.pos.y();
         double var9 = var3.pos.z();
         Iterator var11 = this.triangleFans.iterator();

         while(var11.hasNext()) {
            DrawableGizmoPrimitives.TriangleFan var12 = (DrawableGizmoPrimitives.TriangleFan)var11.next();
            VertexConsumer var13 = var2.getBuffer(RenderTypes.debugTriangleFan());
            Vec3[] var14 = var12.points();
            int var15 = var14.length;

            for(int var16 = 0; var16 < var15; ++var16) {
               Vec3 var17 = var14[var16];
               var13.addVertex(var4, (float)(var17.x() - var5), (float)(var17.y() - var7), (float)(var17.z() - var9)).setColor(var12.color());
            }
         }

      }

      private void renderQuads(PoseStack var1, MultiBufferSource var2, CameraRenderState var3) {
         VertexConsumer var4 = var2.getBuffer(RenderTypes.debugFilledBox());
         PoseStack.Pose var5 = var1.last();
         double var6 = var3.pos.x();
         double var8 = var3.pos.y();
         double var10 = var3.pos.z();
         Iterator var12 = this.quads.iterator();

         while(var12.hasNext()) {
            DrawableGizmoPrimitives.Quad var13 = (DrawableGizmoPrimitives.Quad)var12.next();
            var4.addVertex(var5, (float)(var13.a().x() - var6), (float)(var13.a().y() - var8), (float)(var13.a().z() - var10)).setColor(var13.color());
            var4.addVertex(var5, (float)(var13.b().x() - var6), (float)(var13.b().y() - var8), (float)(var13.b().z() - var10)).setColor(var13.color());
            var4.addVertex(var5, (float)(var13.c().x() - var6), (float)(var13.c().y() - var8), (float)(var13.c().z() - var10)).setColor(var13.color());
            var4.addVertex(var5, (float)(var13.d().x() - var6), (float)(var13.d().y() - var8), (float)(var13.d().z() - var10)).setColor(var13.color());
         }

      }

      private void renderPoints(PoseStack var1, MultiBufferSource var2, CameraRenderState var3) {
         VertexConsumer var4 = var2.getBuffer(RenderTypes.debugPoint());
         PoseStack.Pose var5 = var1.last();
         double var6 = var3.pos.x();
         double var8 = var3.pos.y();
         double var10 = var3.pos.z();
         Iterator var12 = this.points.iterator();

         while(var12.hasNext()) {
            DrawableGizmoPrimitives.Point var13 = (DrawableGizmoPrimitives.Point)var12.next();
            var4.addVertex(var5, (float)(var13.pos.x() - var6), (float)(var13.pos.y() - var8), (float)(var13.pos.z() - var10)).setColor(var13.color()).setLineWidth(var13.size());
         }

      }

      public boolean opaque() {
         return this.opaque;
      }

      public List<DrawableGizmoPrimitives.Line> lines() {
         return this.lines;
      }

      public List<DrawableGizmoPrimitives.Quad> quads() {
         return this.quads;
      }

      public List<DrawableGizmoPrimitives.TriangleFan> triangleFans() {
         return this.triangleFans;
      }

      public List<DrawableGizmoPrimitives.Text> texts() {
         return this.texts;
      }

      public List<DrawableGizmoPrimitives.Point> points() {
         return this.points;
      }
   }

   private static record Point(Vec3 pos, int color, float size) {
      final Vec3 pos;

      Point(Vec3 param1, int param2, float param3) {
         super();
         this.pos = var1;
         this.color = var2;
         this.size = var3;
      }

      public Vec3 pos() {
         return this.pos;
      }

      public int color() {
         return this.color;
      }

      public float size() {
         return this.size;
      }
   }

   private static record Line(Vec3 start, Vec3 end, int color, float width) {
      Line(Vec3 param1, Vec3 param2, int param3, float param4) {
         super();
         this.start = var1;
         this.end = var2;
         this.color = var3;
         this.width = var4;
      }

      public Vec3 start() {
         return this.start;
      }

      public Vec3 end() {
         return this.end;
      }

      public int color() {
         return this.color;
      }

      public float width() {
         return this.width;
      }
   }

   private static record TriangleFan(Vec3[] points, int color) {
      TriangleFan(Vec3[] param1, int param2) {
         super();
         this.points = var1;
         this.color = var2;
      }

      public Vec3[] points() {
         return this.points;
      }

      public int color() {
         return this.color;
      }
   }

   private static record Quad(Vec3 a, Vec3 b, Vec3 c, Vec3 d, int color) {
      Quad(Vec3 param1, Vec3 param2, Vec3 param3, Vec3 param4, int param5) {
         super();
         this.a = var1;
         this.b = var2;
         this.c = var3;
         this.d = var4;
         this.color = var5;
      }

      public Vec3 a() {
         return this.a;
      }

      public Vec3 b() {
         return this.b;
      }

      public Vec3 c() {
         return this.c;
      }

      public Vec3 d() {
         return this.d;
      }

      public int color() {
         return this.color;
      }
   }

   private static record Text(Vec3 pos, String text, TextGizmo.Style style) {
      final String text;
      final TextGizmo.Style style;

      Text(Vec3 param1, String param2, TextGizmo.Style param3) {
         super();
         this.pos = var1;
         this.text = var2;
         this.style = var3;
      }

      public Vec3 pos() {
         return this.pos;
      }

      public String text() {
         return this.text;
      }

      public TextGizmo.Style style() {
         return this.style;
      }
   }
}
