package net.minecraft.network.protocol;

import net.minecraft.resources.Identifier;

public record PacketType<T extends Packet<?>>(PacketFlow flow, Identifier id) {
   public PacketType(PacketFlow param1, Identifier param2) {
      super();
      this.flow = var1;
      this.id = var2;
   }

   public String toString() {
      String var10000 = this.flow.id();
      return var10000 + "/" + String.valueOf(this.id);
   }

   public PacketFlow flow() {
      return this.flow;
   }

   public Identifier id() {
      return this.id;
   }
}
