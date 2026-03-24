package net.minecraft.client.renderer;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public record PostChainConfig(Map<Identifier, PostChainConfig.InternalTarget> internalTargets, List<PostChainConfig.Pass> passes) {
   public static final Codec<PostChainConfig> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.unboundedMap(Identifier.CODEC, PostChainConfig.InternalTarget.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostChainConfig::internalTargets), PostChainConfig.Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostChainConfig::passes)).apply(var0, PostChainConfig::new);
   });

   public PostChainConfig(Map<Identifier, PostChainConfig.InternalTarget> param1, List<PostChainConfig.Pass> param2) {
      super();
      this.internalTargets = var1;
      this.passes = var2;
   }

   public Map<Identifier, PostChainConfig.InternalTarget> internalTargets() {
      return this.internalTargets;
   }

   public List<PostChainConfig.Pass> passes() {
      return this.passes;
   }

   public static record InternalTarget(Optional<Integer> width, Optional<Integer> height, boolean persistent, int clearColor) {
      public static final Codec<PostChainConfig.InternalTarget> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("width").forGetter(PostChainConfig.InternalTarget::width), ExtraCodecs.POSITIVE_INT.optionalFieldOf("height").forGetter(PostChainConfig.InternalTarget::height), Codec.BOOL.optionalFieldOf("persistent", false).forGetter(PostChainConfig.InternalTarget::persistent), ExtraCodecs.ARGB_COLOR_CODEC.optionalFieldOf("clear_color", 0).forGetter(PostChainConfig.InternalTarget::clearColor)).apply(var0, PostChainConfig.InternalTarget::new);
      });

      public InternalTarget(Optional<Integer> param1, Optional<Integer> param2, boolean param3, int param4) {
         super();
         this.width = var1;
         this.height = var2;
         this.persistent = var3;
         this.clearColor = var4;
      }

      public Optional<Integer> width() {
         return this.width;
      }

      public Optional<Integer> height() {
         return this.height;
      }

      public boolean persistent() {
         return this.persistent;
      }

      public int clearColor() {
         return this.clearColor;
      }
   }

   public static record Pass(Identifier vertexShaderId, Identifier fragmentShaderId, List<PostChainConfig.Input> inputs, Identifier outputTarget, Map<String, List<UniformValue>> uniforms) {
      private static final Codec<List<PostChainConfig.Input>> INPUTS_CODEC;
      private static final Codec<Map<String, List<UniformValue>>> UNIFORM_BLOCKS_CODEC;
      public static final Codec<PostChainConfig.Pass> CODEC;

      public Pass(Identifier param1, Identifier param2, List<PostChainConfig.Input> param3, Identifier param4, Map<String, List<UniformValue>> param5) {
         super();
         this.vertexShaderId = var1;
         this.fragmentShaderId = var2;
         this.inputs = var3;
         this.outputTarget = var4;
         this.uniforms = var5;
      }

      public Stream<Identifier> referencedTargets() {
         Stream var1 = this.inputs.stream().flatMap((var0) -> {
            return var0.referencedTargets().stream();
         });
         return Stream.concat(var1, Stream.of(this.outputTarget));
      }

      public Identifier vertexShaderId() {
         return this.vertexShaderId;
      }

      public Identifier fragmentShaderId() {
         return this.fragmentShaderId;
      }

      public List<PostChainConfig.Input> inputs() {
         return this.inputs;
      }

      public Identifier outputTarget() {
         return this.outputTarget;
      }

      public Map<String, List<UniformValue>> uniforms() {
         return this.uniforms;
      }

      static {
         INPUTS_CODEC = PostChainConfig.Input.CODEC.listOf().validate((var0) -> {
            ObjectArraySet var1 = new ObjectArraySet(var0.size());
            Iterator var2 = var0.iterator();

            PostChainConfig.Input var3;
            do {
               if (!var2.hasNext()) {
                  return DataResult.success(var0);
               }

               var3 = (PostChainConfig.Input)var2.next();
            } while(var1.add(var3.samplerName()));

            return DataResult.error(() -> {
               return "Encountered repeated sampler name: " + var3.samplerName();
            });
         });
         UNIFORM_BLOCKS_CODEC = Codec.unboundedMap(Codec.STRING, UniformValue.CODEC.listOf());
         CODEC = RecordCodecBuilder.create((var0) -> {
            return var0.group(Identifier.CODEC.fieldOf("vertex_shader").forGetter(PostChainConfig.Pass::vertexShaderId), Identifier.CODEC.fieldOf("fragment_shader").forGetter(PostChainConfig.Pass::fragmentShaderId), INPUTS_CODEC.optionalFieldOf("inputs", List.of()).forGetter(PostChainConfig.Pass::inputs), Identifier.CODEC.fieldOf("output").forGetter(PostChainConfig.Pass::outputTarget), UNIFORM_BLOCKS_CODEC.optionalFieldOf("uniforms", Map.of()).forGetter(PostChainConfig.Pass::uniforms)).apply(var0, PostChainConfig.Pass::new);
         });
      }
   }

   public static record TargetInput(String samplerName, Identifier targetId, boolean useDepthBuffer, boolean bilinear) implements PostChainConfig.Input {
      public static final Codec<PostChainConfig.TargetInput> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.STRING.fieldOf("sampler_name").forGetter(PostChainConfig.TargetInput::samplerName), Identifier.CODEC.fieldOf("target").forGetter(PostChainConfig.TargetInput::targetId), Codec.BOOL.optionalFieldOf("use_depth_buffer", false).forGetter(PostChainConfig.TargetInput::useDepthBuffer), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(PostChainConfig.TargetInput::bilinear)).apply(var0, PostChainConfig.TargetInput::new);
      });

      public TargetInput(String param1, Identifier param2, boolean param3, boolean param4) {
         super();
         this.samplerName = var1;
         this.targetId = var2;
         this.useDepthBuffer = var3;
         this.bilinear = var4;
      }

      public Set<Identifier> referencedTargets() {
         return Set.of(this.targetId);
      }

      public String samplerName() {
         return this.samplerName;
      }

      public Identifier targetId() {
         return this.targetId;
      }

      public boolean useDepthBuffer() {
         return this.useDepthBuffer;
      }

      public boolean bilinear() {
         return this.bilinear;
      }
   }

   public static record TextureInput(String samplerName, Identifier location, int width, int height, boolean bilinear) implements PostChainConfig.Input {
      public static final Codec<PostChainConfig.TextureInput> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.STRING.fieldOf("sampler_name").forGetter(PostChainConfig.TextureInput::samplerName), Identifier.CODEC.fieldOf("location").forGetter(PostChainConfig.TextureInput::location), ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(PostChainConfig.TextureInput::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(PostChainConfig.TextureInput::height), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(PostChainConfig.TextureInput::bilinear)).apply(var0, PostChainConfig.TextureInput::new);
      });

      public TextureInput(String param1, Identifier param2, int param3, int param4, boolean param5) {
         super();
         this.samplerName = var1;
         this.location = var2;
         this.width = var3;
         this.height = var4;
         this.bilinear = var5;
      }

      public Set<Identifier> referencedTargets() {
         return Set.of();
      }

      public String samplerName() {
         return this.samplerName;
      }

      public Identifier location() {
         return this.location;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public boolean bilinear() {
         return this.bilinear;
      }
   }

   public interface Input {
      Codec<PostChainConfig.Input> CODEC = Codec.xor(PostChainConfig.TextureInput.CODEC, PostChainConfig.TargetInput.CODEC).xmap((var0) -> {
         return (PostChainConfig.Input)var0.map(Function.identity(), Function.identity());
      }, (var0) -> {
         Objects.requireNonNull(var0);
         byte var2 = 0;
         Either var10000;
         switch(var0.typeSwitch<invokedynamic>(var0, var2)) {
         case 0:
            PostChainConfig.TextureInput var3 = (PostChainConfig.TextureInput)var0;
            var10000 = Either.left(var3);
            break;
         case 1:
            PostChainConfig.TargetInput var4 = (PostChainConfig.TargetInput)var0;
            var10000 = Either.right(var4);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      });

      String samplerName();

      Set<Identifier> referencedTargets();
   }
}
