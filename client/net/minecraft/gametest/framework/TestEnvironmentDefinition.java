package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import org.slf4j.Logger;

public interface TestEnvironmentDefinition {
   Codec<TestEnvironmentDefinition> DIRECT_CODEC = BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE.byNameCodec().dispatch(TestEnvironmentDefinition::codec, (var0) -> {
      return var0;
   });
   Codec<Holder<TestEnvironmentDefinition>> CODEC = RegistryFileCodec.create(Registries.TEST_ENVIRONMENT, DIRECT_CODEC);

   static MapCodec<? extends TestEnvironmentDefinition> bootstrap(Registry<MapCodec<? extends TestEnvironmentDefinition>> var0) {
      Registry.register(var0, (String)"all_of", TestEnvironmentDefinition.AllOf.CODEC);
      Registry.register(var0, (String)"game_rules", TestEnvironmentDefinition.SetGameRules.CODEC);
      Registry.register(var0, (String)"time_of_day", TestEnvironmentDefinition.TimeOfDay.CODEC);
      Registry.register(var0, (String)"weather", TestEnvironmentDefinition.Weather.CODEC);
      return (MapCodec)Registry.register(var0, (String)"function", TestEnvironmentDefinition.Functions.CODEC);
   }

   void setup(ServerLevel var1);

   default void teardown(ServerLevel var1) {
   }

   MapCodec<? extends TestEnvironmentDefinition> codec();

   public static record AllOf(List<Holder<TestEnvironmentDefinition>> definitions) implements TestEnvironmentDefinition {
      public static final MapCodec<TestEnvironmentDefinition.AllOf> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(TestEnvironmentDefinition.CODEC.listOf().fieldOf("definitions").forGetter(TestEnvironmentDefinition.AllOf::definitions)).apply(var0, TestEnvironmentDefinition.AllOf::new);
      });

      public AllOf(TestEnvironmentDefinition... var1) {
         this(Arrays.stream(var1).map(Holder::direct).toList());
      }

      public AllOf(List<Holder<TestEnvironmentDefinition>> param1) {
         super();
         this.definitions = var1;
      }

      public void setup(ServerLevel var1) {
         this.definitions.forEach((var1x) -> {
            ((TestEnvironmentDefinition)var1x.value()).setup(var1);
         });
      }

      public void teardown(ServerLevel var1) {
         this.definitions.forEach((var1x) -> {
            ((TestEnvironmentDefinition)var1x.value()).teardown(var1);
         });
      }

      public MapCodec<TestEnvironmentDefinition.AllOf> codec() {
         return CODEC;
      }

      public List<Holder<TestEnvironmentDefinition>> definitions() {
         return this.definitions;
      }
   }

   public static record SetGameRules(GameRuleMap gameRulesMap) implements TestEnvironmentDefinition {
      public static final MapCodec<TestEnvironmentDefinition.SetGameRules> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(GameRuleMap.CODEC.fieldOf("rules").forGetter(TestEnvironmentDefinition.SetGameRules::gameRulesMap)).apply(var0, TestEnvironmentDefinition.SetGameRules::new);
      });

      public SetGameRules(GameRuleMap param1) {
         super();
         this.gameRulesMap = var1;
      }

      public void setup(ServerLevel var1) {
         GameRules var2 = var1.getGameRules();
         MinecraftServer var3 = var1.getServer();
         var2.setAll(this.gameRulesMap, var3);
      }

      public void teardown(ServerLevel var1) {
         this.gameRulesMap.keySet().forEach((var2) -> {
            this.resetRule(var1, var2);
         });
      }

      private <T> void resetRule(ServerLevel var1, GameRule<T> var2) {
         var1.getGameRules().set(var2, var2.defaultValue(), var1.getServer());
      }

      public MapCodec<TestEnvironmentDefinition.SetGameRules> codec() {
         return CODEC;
      }

      public GameRuleMap gameRulesMap() {
         return this.gameRulesMap;
      }
   }

   public static record TimeOfDay(int time) implements TestEnvironmentDefinition {
      public static final MapCodec<TestEnvironmentDefinition.TimeOfDay> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("time").forGetter(TestEnvironmentDefinition.TimeOfDay::time)).apply(var0, TestEnvironmentDefinition.TimeOfDay::new);
      });

      public TimeOfDay(int param1) {
         super();
         this.time = var1;
      }

      public void setup(ServerLevel var1) {
         var1.setDayTime((long)this.time);
      }

      public MapCodec<TestEnvironmentDefinition.TimeOfDay> codec() {
         return CODEC;
      }

      public int time() {
         return this.time;
      }
   }

   public static record Weather(TestEnvironmentDefinition.Weather.Type weather) implements TestEnvironmentDefinition {
      public static final MapCodec<TestEnvironmentDefinition.Weather> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(TestEnvironmentDefinition.Weather.Type.CODEC.fieldOf("weather").forGetter(TestEnvironmentDefinition.Weather::weather)).apply(var0, TestEnvironmentDefinition.Weather::new);
      });

      public Weather(TestEnvironmentDefinition.Weather.Type param1) {
         super();
         this.weather = var1;
      }

      public void setup(ServerLevel var1) {
         this.weather.apply(var1);
      }

      public void teardown(ServerLevel var1) {
         var1.resetWeatherCycle();
      }

      public MapCodec<TestEnvironmentDefinition.Weather> codec() {
         return CODEC;
      }

      public TestEnvironmentDefinition.Weather.Type weather() {
         return this.weather;
      }

      public static enum Type implements StringRepresentable {
         CLEAR("clear", 100000, 0, false, false),
         RAIN("rain", 0, 100000, true, false),
         THUNDER("thunder", 0, 100000, true, true);

         public static final Codec<TestEnvironmentDefinition.Weather.Type> CODEC = StringRepresentable.fromEnum(TestEnvironmentDefinition.Weather.Type::values);
         private final String id;
         private final int clearTime;
         private final int rainTime;
         private final boolean raining;
         private final boolean thundering;

         private Type(final String param3, final int param4, final int param5, final boolean param6, final boolean param7) {
            this.id = var3;
            this.clearTime = var4;
            this.rainTime = var5;
            this.raining = var6;
            this.thundering = var7;
         }

         void apply(ServerLevel var1) {
            var1.setWeatherParameters(this.clearTime, this.rainTime, this.raining, this.thundering);
         }

         public String getSerializedName() {
            return this.id;
         }

         // $FF: synthetic method
         private static TestEnvironmentDefinition.Weather.Type[] $values() {
            return new TestEnvironmentDefinition.Weather.Type[]{CLEAR, RAIN, THUNDER};
         }
      }
   }

   public static record Functions(Optional<Identifier> setupFunction, Optional<Identifier> teardownFunction) implements TestEnvironmentDefinition {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec<TestEnvironmentDefinition.Functions> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Identifier.CODEC.optionalFieldOf("setup").forGetter(TestEnvironmentDefinition.Functions::setupFunction), Identifier.CODEC.optionalFieldOf("teardown").forGetter(TestEnvironmentDefinition.Functions::teardownFunction)).apply(var0, TestEnvironmentDefinition.Functions::new);
      });

      public Functions(Optional<Identifier> param1, Optional<Identifier> param2) {
         super();
         this.setupFunction = var1;
         this.teardownFunction = var2;
      }

      public void setup(ServerLevel var1) {
         this.setupFunction.ifPresent((var1x) -> {
            run(var1, var1x);
         });
      }

      public void teardown(ServerLevel var1) {
         this.teardownFunction.ifPresent((var1x) -> {
            run(var1, var1x);
         });
      }

      private static void run(ServerLevel var0, Identifier var1) {
         MinecraftServer var2 = var0.getServer();
         ServerFunctionManager var3 = var2.getFunctions();
         Optional var4 = var3.get(var1);
         if (var4.isPresent()) {
            CommandSourceStack var5 = var2.createCommandSourceStack().withPermission(LevelBasedPermissionSet.GAMEMASTER).withSuppressedOutput().withLevel(var0);
            var3.execute((CommandFunction)var4.get(), var5);
         } else {
            LOGGER.error("Test Batch failed for non-existent function {}", var1);
         }

      }

      public MapCodec<TestEnvironmentDefinition.Functions> codec() {
         return CODEC;
      }

      public Optional<Identifier> setupFunction() {
         return this.setupFunction;
      }

      public Optional<Identifier> teardownFunction() {
         return this.teardownFunction;
      }
   }
}
