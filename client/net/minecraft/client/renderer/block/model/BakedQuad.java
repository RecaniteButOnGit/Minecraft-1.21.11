package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;

public record BakedQuad(Vector3fc position0, Vector3fc position1, Vector3fc position2, Vector3fc position3, long packedUV0, long packedUV1, long packedUV2, long packedUV3, int tintIndex, Direction direction, TextureAtlasSprite sprite, boolean shade, int lightEmission) {
   public static final int VERTEX_COUNT = 4;

   public BakedQuad(Vector3fc param1, Vector3fc param2, Vector3fc param3, Vector3fc param4, long param5, long param7, long param9, long param11, int param13, Direction param14, TextureAtlasSprite param15, boolean param16, int param17) {
      super();
      this.position0 = var1;
      this.position1 = var2;
      this.position2 = var3;
      this.position3 = var4;
      this.packedUV0 = var5;
      this.packedUV1 = var7;
      this.packedUV2 = var9;
      this.packedUV3 = var11;
      this.tintIndex = var13;
      this.direction = var14;
      this.sprite = var15;
      this.shade = var16;
      this.lightEmission = var17;
   }

   public boolean isTinted() {
      return this.tintIndex != -1;
   }

   public Vector3fc position(int var1) {
      Vector3fc var10000;
      switch(var1) {
      case 0:
         var10000 = this.position0;
         break;
      case 1:
         var10000 = this.position1;
         break;
      case 2:
         var10000 = this.position2;
         break;
      case 3:
         var10000 = this.position3;
         break;
      default:
         throw new IndexOutOfBoundsException(var1);
      }

      return var10000;
   }

   public long packedUV(int var1) {
      long var10000;
      switch(var1) {
      case 0:
         var10000 = this.packedUV0;
         break;
      case 1:
         var10000 = this.packedUV1;
         break;
      case 2:
         var10000 = this.packedUV2;
         break;
      case 3:
         var10000 = this.packedUV3;
         break;
      default:
         throw new IndexOutOfBoundsException(var1);
      }

      return var10000;
   }

   public Vector3fc position0() {
      return this.position0;
   }

   public Vector3fc position1() {
      return this.position1;
   }

   public Vector3fc position2() {
      return this.position2;
   }

   public Vector3fc position3() {
      return this.position3;
   }

   public long packedUV0() {
      return this.packedUV0;
   }

   public long packedUV1() {
      return this.packedUV1;
   }

   public long packedUV2() {
      return this.packedUV2;
   }

   public long packedUV3() {
      return this.packedUV3;
   }

   public int tintIndex() {
      return this.tintIndex;
   }

   public Direction direction() {
      return this.direction;
   }

   public TextureAtlasSprite sprite() {
      return this.sprite;
   }

   public boolean shade() {
      return this.shade;
   }

   public int lightEmission() {
      return this.lightEmission;
   }
}
