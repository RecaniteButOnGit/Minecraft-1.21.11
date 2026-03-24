package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.Display;
import org.jspecify.annotations.Nullable;

public class TextDisplayEntityRenderState extends DisplayEntityRenderState {
   @Nullable
   public Display.TextDisplay.TextRenderState textRenderState;
   @Nullable
   public Display.TextDisplay.CachedInfo cachedInfo;

   public TextDisplayEntityRenderState() {
      super();
   }

   public boolean hasSubState() {
      return this.textRenderState != null;
   }
}
