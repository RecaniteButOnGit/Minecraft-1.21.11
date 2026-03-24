package net.minecraft.client.multiplayer;

import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.Identifier;

public record TransferState(Map<Identifier, byte[]> cookies, Map<UUID, PlayerInfo> seenPlayers, boolean seenInsecureChatWarning) {
   public TransferState(Map<Identifier, byte[]> param1, Map<UUID, PlayerInfo> param2, boolean param3) {
      super();
      this.cookies = var1;
      this.seenPlayers = var2;
      this.seenInsecureChatWarning = var3;
   }

   public Map<Identifier, byte[]> cookies() {
      return this.cookies;
   }

   public Map<UUID, PlayerInfo> seenPlayers() {
      return this.seenPlayers;
   }

   public boolean seenInsecureChatWarning() {
      return this.seenInsecureChatWarning;
   }
}
