package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleType;

public class GameRulesService {
   public GameRulesService() {
      super();
   }

   public static List<GameRulesService.GameRuleUpdate<?>> get(MinecraftApi var0) {
      ArrayList var1 = new ArrayList();
      var0.gameRuleService().getAvailableGameRules().forEach((var2) -> {
         addGameRule(var0, var2, var1);
      });
      return var1;
   }

   private static <T> void addGameRule(MinecraftApi var0, GameRule<T> var1, List<GameRulesService.GameRuleUpdate<?>> var2) {
      Object var3 = var0.gameRuleService().getRuleValue(var1);
      var2.add(getTypedRule(var0, var1, Objects.requireNonNull(var3)));
   }

   public static <T> GameRulesService.GameRuleUpdate<T> getTypedRule(MinecraftApi var0, GameRule<T> var1, T var2) {
      return var0.gameRuleService().getTypedRule(var1, var2);
   }

   public static <T> GameRulesService.GameRuleUpdate<T> update(MinecraftApi var0, GameRulesService.GameRuleUpdate<T> var1, ClientInfo var2) {
      return var0.gameRuleService().updateGameRule(var1, var2);
   }

   public static record GameRuleUpdate<T>(GameRule<T> gameRule, T value) {
      public static final Codec<GameRulesService.GameRuleUpdate<?>> TYPED_CODEC;
      public static final Codec<GameRulesService.GameRuleUpdate<?>> CODEC;

      public GameRuleUpdate(GameRule<T> param1, T param2) {
         super();
         this.gameRule = var1;
         this.value = var2;
      }

      private static <T> MapCodec<? extends GameRulesService.GameRuleUpdate<T>> getValueCodec(GameRule<T> var0) {
         return var0.valueCodec().fieldOf("value").xmap((var1) -> {
            return new GameRulesService.GameRuleUpdate(var0, var1);
         }, GameRulesService.GameRuleUpdate::value);
      }

      private static <T> MapCodec<? extends GameRulesService.GameRuleUpdate<T>> getValueAndTypeCodec(GameRule<T> var0) {
         return RecordCodecBuilder.mapCodec((var1) -> {
            return var1.group(StringRepresentable.fromEnum(GameRuleType::values).fieldOf("type").forGetter((var0x) -> {
               return var0x.gameRule.gameRuleType();
            }), var0.valueCodec().fieldOf("value").forGetter(GameRulesService.GameRuleUpdate::value)).apply(var1, (var1x, var2) -> {
               return getUntypedRule(var0, var1x, var2);
            });
         });
      }

      private static <T> GameRulesService.GameRuleUpdate<T> getUntypedRule(GameRule<T> var0, GameRuleType var1, T var2) {
         if (var0.gameRuleType() != var1) {
            String var10002 = String.valueOf(var1);
            throw new InvalidParameterJsonRpcException("Stated type \"" + var10002 + "\" mismatches with actual type \"" + String.valueOf(var0.gameRuleType()) + "\" of gamerule \"" + var0.id() + "\"");
         } else {
            return new GameRulesService.GameRuleUpdate(var0, var2);
         }
      }

      public GameRule<T> gameRule() {
         return this.gameRule;
      }

      public T value() {
         return this.value;
      }

      static {
         TYPED_CODEC = BuiltInRegistries.GAME_RULE.byNameCodec().dispatch("key", GameRulesService.GameRuleUpdate::gameRule, GameRulesService.GameRuleUpdate::getValueAndTypeCodec);
         CODEC = BuiltInRegistries.GAME_RULE.byNameCodec().dispatch("key", GameRulesService.GameRuleUpdate::gameRule, GameRulesService.GameRuleUpdate::getValueCodec);
      }
   }
}
