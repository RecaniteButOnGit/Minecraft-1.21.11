package com.mojang.realmsclient.client.worldupload;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public abstract class RealmsUploadException extends RuntimeException {
   public RealmsUploadException() {
      super();
   }

   @Nullable
   public Component getStatusMessage() {
      return null;
   }

   @Nullable
   public Component[] getErrorMessages() {
      return null;
   }
}
