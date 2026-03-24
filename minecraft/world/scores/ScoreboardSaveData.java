package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class ScoreboardSaveData extends SavedData {
   public static final SavedDataType<ScoreboardSaveData> TYPE;
   private ScoreboardSaveData.Packed data;

   private ScoreboardSaveData() {
      this(ScoreboardSaveData.Packed.EMPTY);
   }

   public ScoreboardSaveData(ScoreboardSaveData.Packed var1) {
      super();
      this.data = var1;
   }

   public ScoreboardSaveData.Packed getData() {
      return this.data;
   }

   public void setData(ScoreboardSaveData.Packed var1) {
      if (!var1.equals(this.data)) {
         this.data = var1;
         this.setDirty();
      }

   }

   static {
      TYPE = new SavedDataType("scoreboard", ScoreboardSaveData::new, ScoreboardSaveData.Packed.CODEC.xmap(ScoreboardSaveData::new, ScoreboardSaveData::getData), DataFixTypes.SAVED_DATA_SCOREBOARD);
   }

   public static record Packed(List<Objective.Packed> objectives, List<Scoreboard.PackedScore> scores, Map<DisplaySlot, String> displaySlots, List<PlayerTeam.Packed> teams) {
      public static final ScoreboardSaveData.Packed EMPTY = new ScoreboardSaveData.Packed(List.of(), List.of(), Map.of(), List.of());
      public static final Codec<ScoreboardSaveData.Packed> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Objective.Packed.CODEC.listOf().optionalFieldOf("Objectives", List.of()).forGetter(ScoreboardSaveData.Packed::objectives), Scoreboard.PackedScore.CODEC.listOf().optionalFieldOf("PlayerScores", List.of()).forGetter(ScoreboardSaveData.Packed::scores), Codec.unboundedMap(DisplaySlot.CODEC, Codec.STRING).optionalFieldOf("DisplaySlots", Map.of()).forGetter(ScoreboardSaveData.Packed::displaySlots), PlayerTeam.Packed.CODEC.listOf().optionalFieldOf("Teams", List.of()).forGetter(ScoreboardSaveData.Packed::teams)).apply(var0, ScoreboardSaveData.Packed::new);
      });

      public Packed(List<Objective.Packed> param1, List<Scoreboard.PackedScore> param2, Map<DisplaySlot, String> param3, List<PlayerTeam.Packed> param4) {
         super();
         this.objectives = var1;
         this.scores = var2;
         this.displaySlots = var3;
         this.teams = var4;
      }

      public List<Objective.Packed> objectives() {
         return this.objectives;
      }

      public List<Scoreboard.PackedScore> scores() {
         return this.scores;
      }

      public Map<DisplaySlot, String> displaySlots() {
         return this.displaySlots;
      }

      public List<PlayerTeam.Packed> teams() {
         return this.teams;
      }
   }
}
