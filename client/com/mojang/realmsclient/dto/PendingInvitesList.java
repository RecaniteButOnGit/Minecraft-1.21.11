package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public record PendingInvitesList(List<PendingInvite> pendingInvites) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public PendingInvitesList(List<PendingInvite> param1) {
      super();
      this.pendingInvites = var1;
   }

   public static PendingInvitesList parse(String var0) {
      ArrayList var1 = new ArrayList();

      try {
         JsonObject var2 = LenientJsonParser.parse(var0).getAsJsonObject();
         if (var2.get("invites").isJsonArray()) {
            Iterator var3 = var2.get("invites").getAsJsonArray().iterator();

            while(var3.hasNext()) {
               JsonElement var4 = (JsonElement)var3.next();
               PendingInvite var5 = PendingInvite.parse(var4.getAsJsonObject());
               if (var5 != null) {
                  var1.add(var5);
               }
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Could not parse PendingInvitesList", var6);
      }

      return new PendingInvitesList(var1);
   }

   public List<PendingInvite> pendingInvites() {
      return this.pendingInvites;
   }
}
