package net.minecraft.client.renderer.block.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;

public record SimpleUnbakedGeometry(List<BlockElement> elements) implements UnbakedGeometry {
   public SimpleUnbakedGeometry(List<BlockElement> param1) {
      super();
      this.elements = var1;
   }

   public QuadCollection bake(TextureSlots var1, ModelBaker var2, ModelState var3, ModelDebugName var4) {
      return bake(this.elements, var1, var2, var3, var4);
   }

   public static QuadCollection bake(List<BlockElement> var0, TextureSlots var1, ModelBaker var2, ModelState var3, ModelDebugName var4) {
      QuadCollection.Builder var5 = new QuadCollection.Builder();
      Iterator var6 = var0.iterator();

      while(true) {
         BlockElement var7;
         boolean var8;
         boolean var9;
         boolean var10;
         Vector3fc var11;
         Vector3fc var12;
         do {
            if (!var6.hasNext()) {
               return var5.build();
            }

            var7 = (BlockElement)var6.next();
            var8 = true;
            var9 = true;
            var10 = true;
            var11 = var7.from();
            var12 = var7.to();
            if (var11.x() == var12.x()) {
               var9 = false;
               var10 = false;
            }

            if (var11.y() == var12.y()) {
               var8 = false;
               var10 = false;
            }

            if (var11.z() == var12.z()) {
               var8 = false;
               var9 = false;
            }
         } while(!var8 && !var9 && !var10);

         Iterator var13 = var7.faces().entrySet().iterator();

         while(var13.hasNext()) {
            Entry var14 = (Entry)var13.next();
            Direction var15 = (Direction)var14.getKey();
            BlockElementFace var16 = (BlockElementFace)var14.getValue();
            boolean var10000;
            switch(var15.getAxis()) {
            case X:
               var10000 = var8;
               break;
            case Y:
               var10000 = var9;
               break;
            case Z:
               var10000 = var10;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
            }

            boolean var17 = var10000;
            if (var17) {
               TextureAtlasSprite var18 = var2.sprites().resolveSlot(var1, var16.texture(), var4);
               BakedQuad var19 = FaceBakery.bakeQuad(var2.parts(), var11, var12, var16, var18, var15, var3, var7.rotation(), var7.shade(), var7.lightEmission());
               if (var16.cullForDirection() == null) {
                  var5.addUnculledFace(var19);
               } else {
                  var5.addCulledFace(Direction.rotate(var3.transformation().getMatrix(), var16.cullForDirection()), var19);
               }
            }
         }
      }
   }

   public List<BlockElement> elements() {
      return this.elements;
   }
}
