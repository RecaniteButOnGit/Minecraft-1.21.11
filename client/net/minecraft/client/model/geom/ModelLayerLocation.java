package net.minecraft.client.model.geom;

import net.minecraft.resources.Identifier;

public record ModelLayerLocation(Identifier model, String layer) {
   public ModelLayerLocation(Identifier param1, String param2) {
      super();
      this.model = var1;
      this.layer = var2;
   }

   public String toString() {
      String var10000 = String.valueOf(this.model);
      return var10000 + "#" + this.layer;
   }

   public Identifier model() {
      return this.model;
   }

   public String layer() {
      return this.layer;
   }
}
