package net.minecraft.network.protocol.cookie;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public record ClientboundCookieRequestPacket(Identifier key) implements Packet<ClientCookiePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundCookieRequestPacket> STREAM_CODEC = Packet.codec(ClientboundCookieRequestPacket::write, ClientboundCookieRequestPacket::new);

   private ClientboundCookieRequestPacket(FriendlyByteBuf var1) {
      this(var1.readIdentifier());
   }

   public ClientboundCookieRequestPacket(Identifier param1) {
      super();
      this.key = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeIdentifier(this.key);
   }

   public PacketType<ClientboundCookieRequestPacket> type() {
      return CookiePacketTypes.CLIENTBOUND_COOKIE_REQUEST;
   }

   public void handle(ClientCookiePacketListener var1) {
      var1.handleRequestCookie(this);
   }

   public Identifier key() {
      return this.key;
   }
}
