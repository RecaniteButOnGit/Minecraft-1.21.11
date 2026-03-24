package net.minecraft.client.gui.screens.worldselection;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import org.jspecify.annotations.Nullable;

public record InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode selectedGameMode, GameRuleMap gameRuleOverwrites, @Nullable ResourceKey<FlatLevelGeneratorPreset> flatLevelPreset) {
   public InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode param1, GameRuleMap param2, @Nullable ResourceKey<FlatLevelGeneratorPreset> param3) {
      super();
      this.selectedGameMode = var1;
      this.gameRuleOverwrites = var2;
      this.flatLevelPreset = var3;
   }

   public WorldCreationUiState.SelectedGameMode selectedGameMode() {
      return this.selectedGameMode;
   }

   public GameRuleMap gameRuleOverwrites() {
      return this.gameRuleOverwrites;
   }

   @Nullable
   public ResourceKey<FlatLevelGeneratorPreset> flatLevelPreset() {
      return this.flatLevelPreset;
   }
}
