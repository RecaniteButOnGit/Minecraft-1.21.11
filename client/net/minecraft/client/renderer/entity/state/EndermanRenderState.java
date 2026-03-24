package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class EndermanRenderState extends HumanoidRenderState {
   public boolean isCreepy;
   @Nullable
   public BlockState carriedBlock;

   public EndermanRenderState() {
      super();
   }
}
