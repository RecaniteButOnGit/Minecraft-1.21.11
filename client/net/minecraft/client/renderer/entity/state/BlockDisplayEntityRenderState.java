package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.Display;
import org.jspecify.annotations.Nullable;

public class BlockDisplayEntityRenderState extends DisplayEntityRenderState {
   @Nullable
   public Display.BlockDisplay.BlockRenderState blockRenderState;

   public BlockDisplayEntityRenderState() {
      super();
   }

   public boolean hasSubState() {
      return this.blockRenderState != null;
   }
}
