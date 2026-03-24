package net.minecraft.server.jsonrpc;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;
import net.minecraft.server.jsonrpc.api.Schema;
import org.jspecify.annotations.Nullable;

public interface OutgoingRpcMethod<Params, Result> {
   String NOTIFICATION_PREFIX = "notification/";

   MethodInfo<Params, Result> info();

   OutgoingRpcMethod.Attributes attributes();

   @Nullable
   default JsonElement encodeParams(Params var1) {
      return null;
   }

   @Nullable
   default Result decodeResult(JsonElement var1) {
      return null;
   }

   static OutgoingRpcMethod.OutgoingRpcMethodBuilder<Void, Void> notification() {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder(OutgoingRpcMethod.ParmeterlessNotification::new);
   }

   static <Params> OutgoingRpcMethod.OutgoingRpcMethodBuilder<Params, Void> notificationWithParams() {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder(OutgoingRpcMethod.Notification::new);
   }

   static <Result> OutgoingRpcMethod.OutgoingRpcMethodBuilder<Void, Result> request() {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder(OutgoingRpcMethod.ParameterlessMethod::new);
   }

   static <Params, Result> OutgoingRpcMethod.OutgoingRpcMethodBuilder<Params, Result> requestWithParams() {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder(OutgoingRpcMethod.Method::new);
   }

   public static class OutgoingRpcMethodBuilder<Params, Result> {
      public static final OutgoingRpcMethod.Attributes DEFAULT_ATTRIBUTES = new OutgoingRpcMethod.Attributes(true);
      private final OutgoingRpcMethod.Factory<Params, Result> method;
      private String description = "";
      @Nullable
      private ParamInfo<Params> paramInfo;
      @Nullable
      private ResultInfo<Result> resultInfo;

      public OutgoingRpcMethodBuilder(OutgoingRpcMethod.Factory<Params, Result> var1) {
         super();
         this.method = var1;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder<Params, Result> description(String var1) {
         this.description = var1;
         return this;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder<Params, Result> response(String var1, Schema<Result> var2) {
         this.resultInfo = new ResultInfo(var1, var2);
         return this;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder<Params, Result> param(String var1, Schema<Params> var2) {
         this.paramInfo = new ParamInfo(var1, var2);
         return this;
      }

      private OutgoingRpcMethod<Params, Result> build() {
         MethodInfo var1 = new MethodInfo(this.description, this.paramInfo, this.resultInfo);
         return this.method.create(var1, DEFAULT_ATTRIBUTES);
      }

      public Holder.Reference<OutgoingRpcMethod<Params, Result>> register(String var1) {
         return this.register(Identifier.withDefaultNamespace("notification/" + var1));
      }

      private Holder.Reference<OutgoingRpcMethod<Params, Result>> register(Identifier var1) {
         return Registry.registerForHolder(BuiltInRegistries.OUTGOING_RPC_METHOD, (Identifier)var1, this.build());
      }
   }

   @FunctionalInterface
   public interface Factory<Params, Result> {
      OutgoingRpcMethod<Params, Result> create(MethodInfo<Params, Result> var1, OutgoingRpcMethod.Attributes var2);
   }

   public static record Method<Params, Result>(MethodInfo<Params, Result> info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod<Params, Result> {
      public Method(MethodInfo<Params, Result> param1, OutgoingRpcMethod.Attributes param2) {
         super();
         this.info = var1;
         this.attributes = var2;
      }

      @Nullable
      public JsonElement encodeParams(Params var1) {
         if (this.info.params().isEmpty()) {
            throw new IllegalStateException("Method defined as having no parameters");
         } else {
            return (JsonElement)((ParamInfo)this.info.params().get()).schema().codec().encodeStart(JsonOps.INSTANCE, var1).getOrThrow();
         }
      }

      public Result decodeResult(JsonElement var1) {
         if (this.info.result().isEmpty()) {
            throw new IllegalStateException("Method defined as having no result");
         } else {
            return ((ResultInfo)this.info.result().get()).schema().codec().parse(JsonOps.INSTANCE, var1).getOrThrow();
         }
      }

      public MethodInfo<Params, Result> info() {
         return this.info;
      }

      public OutgoingRpcMethod.Attributes attributes() {
         return this.attributes;
      }
   }

   public static record ParameterlessMethod<Result>(MethodInfo<Void, Result> info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod<Void, Result> {
      public ParameterlessMethod(MethodInfo<Void, Result> param1, OutgoingRpcMethod.Attributes param2) {
         super();
         this.info = var1;
         this.attributes = var2;
      }

      public Result decodeResult(JsonElement var1) {
         if (this.info.result().isEmpty()) {
            throw new IllegalStateException("Method defined as having no result");
         } else {
            return ((ResultInfo)this.info.result().get()).schema().codec().parse(JsonOps.INSTANCE, var1).getOrThrow();
         }
      }

      public MethodInfo<Void, Result> info() {
         return this.info;
      }

      public OutgoingRpcMethod.Attributes attributes() {
         return this.attributes;
      }
   }

   public static record Notification<Params>(MethodInfo<Params, Void> info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod<Params, Void> {
      public Notification(MethodInfo<Params, Void> param1, OutgoingRpcMethod.Attributes param2) {
         super();
         this.info = var1;
         this.attributes = var2;
      }

      @Nullable
      public JsonElement encodeParams(Params var1) {
         if (this.info.params().isEmpty()) {
            throw new IllegalStateException("Method defined as having no parameters");
         } else {
            return (JsonElement)((ParamInfo)this.info.params().get()).schema().codec().encodeStart(JsonOps.INSTANCE, var1).getOrThrow();
         }
      }

      public MethodInfo<Params, Void> info() {
         return this.info;
      }

      public OutgoingRpcMethod.Attributes attributes() {
         return this.attributes;
      }
   }

   public static record ParmeterlessNotification(MethodInfo<Void, Void> info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod<Void, Void> {
      public ParmeterlessNotification(MethodInfo<Void, Void> param1, OutgoingRpcMethod.Attributes param2) {
         super();
         this.info = var1;
         this.attributes = var2;
      }

      public MethodInfo<Void, Void> info() {
         return this.info;
      }

      public OutgoingRpcMethod.Attributes attributes() {
         return this.attributes;
      }
   }

   public static record Attributes(boolean discoverable) {
      public Attributes(boolean param1) {
         super();
         this.discoverable = var1;
      }

      public boolean discoverable() {
         return this.discoverable;
      }
   }
}
