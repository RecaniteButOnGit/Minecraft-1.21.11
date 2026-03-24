package net.minecraft.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RegistryContextSwapper;
import org.jspecify.annotations.Nullable;

public record ClientItem(ItemModel.Unbaked model, ClientItem.Properties properties, @Nullable RegistryContextSwapper registrySwapper) {
   public static final Codec<ClientItem> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ItemModels.CODEC.fieldOf("model").forGetter(ClientItem::model), ClientItem.Properties.MAP_CODEC.forGetter(ClientItem::properties)).apply(var0, ClientItem::new);
   });

   public ClientItem(ItemModel.Unbaked var1, ClientItem.Properties var2) {
      this(var1, var2, (RegistryContextSwapper)null);
   }

   public ClientItem(ItemModel.Unbaked param1, ClientItem.Properties param2, @Nullable RegistryContextSwapper param3) {
      super();
      this.model = var1;
      this.properties = var2;
      this.registrySwapper = var3;
   }

   public ClientItem withRegistrySwapper(RegistryContextSwapper var1) {
      return new ClientItem(this.model, this.properties, var1);
   }

   public ItemModel.Unbaked model() {
      return this.model;
   }

   public ClientItem.Properties properties() {
      return this.properties;
   }

   @Nullable
   public RegistryContextSwapper registrySwapper() {
      return this.registrySwapper;
   }

   public static record Properties(boolean handAnimationOnSwap, boolean oversizedInGui, float swapAnimationScale) {
      public static final ClientItem.Properties DEFAULT = new ClientItem.Properties(true, false, 1.0F);
      public static final MapCodec<ClientItem.Properties> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.BOOL.optionalFieldOf("hand_animation_on_swap", true).forGetter(ClientItem.Properties::handAnimationOnSwap), Codec.BOOL.optionalFieldOf("oversized_in_gui", false).forGetter(ClientItem.Properties::oversizedInGui), Codec.FLOAT.optionalFieldOf("swap_animation_scale", 1.0F).forGetter(ClientItem.Properties::swapAnimationScale)).apply(var0, ClientItem.Properties::new);
      });

      public Properties(boolean param1, boolean param2, float param3) {
         super();
         this.handAnimationOnSwap = var1;
         this.oversizedInGui = var2;
         this.swapAnimationScale = var3;
      }

      public boolean handAnimationOnSwap() {
         return this.handAnimationOnSwap;
      }

      public boolean oversizedInGui() {
         return this.oversizedInGui;
      }

      public float swapAnimationScale() {
         return this.swapAnimationScale;
      }
   }
}
