package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.equine.DonkeyModel;
import net.minecraft.client.model.animal.equine.EquineSaddleModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.DonkeyRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public class DonkeyRenderer<T extends AbstractChestedHorse> extends AbstractHorseRenderer<T, DonkeyRenderState, DonkeyModel> {
   private final Identifier texture;

   public DonkeyRenderer(EntityRendererProvider.Context var1, DonkeyRenderer.Type var2) {
      super(var1, new DonkeyModel(var1.bakeLayer(var2.model)), new DonkeyModel(var1.bakeLayer(var2.babyModel)));
      this.texture = var2.texture;
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), var2.saddleLayer, (var0) -> {
         return var0.saddle;
      }, new EquineSaddleModel(var1.bakeLayer(var2.saddleModel)), new EquineSaddleModel(var1.bakeLayer(var2.babySaddleModel))));
   }

   public Identifier getTextureLocation(DonkeyRenderState var1) {
      return this.texture;
   }

   public DonkeyRenderState createRenderState() {
      return new DonkeyRenderState();
   }

   public void extractRenderState(T var1, DonkeyRenderState var2, float var3) {
      super.extractRenderState((AbstractHorse)var1, (EquineRenderState)var2, var3);
      var2.hasChest = var1.hasChest();
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState param1) {
      return this.getTextureLocation((DonkeyRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   public static enum Type {
      DONKEY(Identifier.withDefaultNamespace("textures/entity/horse/donkey.png"), ModelLayers.DONKEY, ModelLayers.DONKEY_BABY, EquipmentClientInfo.LayerType.DONKEY_SADDLE, ModelLayers.DONKEY_SADDLE, ModelLayers.DONKEY_BABY_SADDLE),
      MULE(Identifier.withDefaultNamespace("textures/entity/horse/mule.png"), ModelLayers.MULE, ModelLayers.MULE_BABY, EquipmentClientInfo.LayerType.MULE_SADDLE, ModelLayers.MULE_SADDLE, ModelLayers.MULE_BABY_SADDLE);

      final Identifier texture;
      final ModelLayerLocation model;
      final ModelLayerLocation babyModel;
      final EquipmentClientInfo.LayerType saddleLayer;
      final ModelLayerLocation saddleModel;
      final ModelLayerLocation babySaddleModel;

      private Type(final Identifier param3, final ModelLayerLocation param4, final ModelLayerLocation param5, final EquipmentClientInfo.LayerType param6, final ModelLayerLocation param7, final ModelLayerLocation param8) {
         this.texture = var3;
         this.model = var4;
         this.babyModel = var5;
         this.saddleLayer = var6;
         this.saddleModel = var7;
         this.babySaddleModel = var8;
      }

      // $FF: synthetic method
      private static DonkeyRenderer.Type[] $values() {
         return new DonkeyRenderer.Type[]{DONKEY, MULE};
      }
   }
}
