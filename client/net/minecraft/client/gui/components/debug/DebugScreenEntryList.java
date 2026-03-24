package net.minecraft.client.gui.components.debug;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.datafix.DataFixTypes;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class DebugScreenEntryList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_DEBUG_PROFILE_VERSION = 4649;
   private Map<Identifier, DebugScreenEntryStatus> allStatuses;
   private final List<Identifier> currentlyEnabled = new ArrayList();
   private boolean isOverlayVisible = false;
   @Nullable
   private DebugScreenProfile profile;
   private final File debugProfileFile;
   private long currentlyEnabledVersion;
   private final Codec<DebugScreenEntryList.SerializedOptions> codec;

   public DebugScreenEntryList(File var1) {
      super();
      this.debugProfileFile = new File(var1, "debug-profile.json");
      this.codec = DataFixTypes.DEBUG_PROFILE.wrapCodec(DebugScreenEntryList.SerializedOptions.CODEC, Minecraft.getInstance().getFixerUpper(), 4649);
      this.load();
   }

   public void load() {
      try {
         if (!this.debugProfileFile.isFile()) {
            this.loadDefaultProfile();
            this.rebuildCurrentList();
            return;
         }

         Dynamic var1 = new Dynamic(JsonOps.INSTANCE, StrictJsonParser.parse(FileUtils.readFileToString(this.debugProfileFile, StandardCharsets.UTF_8)));
         DebugScreenEntryList.SerializedOptions var2 = (DebugScreenEntryList.SerializedOptions)this.codec.parse(var1).getOrThrow((var0) -> {
            return new IOException("Could not parse debug profile JSON: " + var0);
         });
         if (var2.profile().isPresent()) {
            this.loadProfile((DebugScreenProfile)var2.profile().get());
         } else {
            this.allStatuses = new HashMap();
            if (var2.custom().isPresent()) {
               this.allStatuses.putAll((Map)var2.custom().get());
            }

            this.profile = null;
         }
      } catch (JsonSyntaxException | IOException var3) {
         LOGGER.error("Couldn't read debug profile file {}, resetting to default", this.debugProfileFile, var3);
         this.loadDefaultProfile();
         this.save();
      }

      this.rebuildCurrentList();
   }

   public void loadProfile(DebugScreenProfile var1) {
      this.profile = var1;
      Map var2 = (Map)DebugScreenEntries.PROFILES.get(var1);
      this.allStatuses = new HashMap(var2);
      this.rebuildCurrentList();
   }

   private void loadDefaultProfile() {
      this.profile = DebugScreenProfile.DEFAULT;
      this.allStatuses = new HashMap((Map)DebugScreenEntries.PROFILES.get(DebugScreenProfile.DEFAULT));
   }

   public DebugScreenEntryStatus getStatus(Identifier var1) {
      DebugScreenEntryStatus var2 = (DebugScreenEntryStatus)this.allStatuses.get(var1);
      return var2 == null ? DebugScreenEntryStatus.NEVER : var2;
   }

   public boolean isCurrentlyEnabled(Identifier var1) {
      return this.currentlyEnabled.contains(var1);
   }

   public void setStatus(Identifier var1, DebugScreenEntryStatus var2) {
      this.profile = null;
      this.allStatuses.put(var1, var2);
      this.rebuildCurrentList();
      this.save();
   }

   public boolean toggleStatus(Identifier var1) {
      DebugScreenEntryStatus var2 = (DebugScreenEntryStatus)this.allStatuses.get(var1);
      byte var4 = 0;
      switch(var2.enumSwitch<invokedynamic>(var2, var4)) {
      case -1:
      default:
         this.setStatus(var1, DebugScreenEntryStatus.ALWAYS_ON);
         return true;
      case 0:
         this.setStatus(var1, DebugScreenEntryStatus.NEVER);
         return false;
      case 1:
         if (this.isOverlayVisible) {
            this.setStatus(var1, DebugScreenEntryStatus.NEVER);
            return false;
         }

         this.setStatus(var1, DebugScreenEntryStatus.ALWAYS_ON);
         return true;
      case 2:
         if (this.isOverlayVisible) {
            this.setStatus(var1, DebugScreenEntryStatus.IN_OVERLAY);
         } else {
            this.setStatus(var1, DebugScreenEntryStatus.ALWAYS_ON);
         }

         return true;
      }
   }

   public Collection<Identifier> getCurrentlyEnabled() {
      return this.currentlyEnabled;
   }

   public void toggleDebugOverlay() {
      this.setOverlayVisible(!this.isOverlayVisible);
   }

   public void setOverlayVisible(boolean var1) {
      if (this.isOverlayVisible != var1) {
         this.isOverlayVisible = var1;
         this.rebuildCurrentList();
      }

   }

   public boolean isOverlayVisible() {
      return this.isOverlayVisible;
   }

   public void rebuildCurrentList() {
      this.currentlyEnabled.clear();
      boolean var1 = Minecraft.getInstance().showOnlyReducedInfo();
      Iterator var2 = this.allStatuses.entrySet().iterator();

      while(true) {
         Entry var3;
         do {
            if (!var2.hasNext()) {
               this.currentlyEnabled.sort(Identifier::compareTo);
               ++this.currentlyEnabledVersion;
               return;
            }

            var3 = (Entry)var2.next();
         } while(var3.getValue() != DebugScreenEntryStatus.ALWAYS_ON && (!this.isOverlayVisible || var3.getValue() != DebugScreenEntryStatus.IN_OVERLAY));

         DebugScreenEntry var4 = DebugScreenEntries.getEntry((Identifier)var3.getKey());
         if (var4 != null && var4.isAllowed(var1)) {
            this.currentlyEnabled.add((Identifier)var3.getKey());
         }
      }
   }

   public long getCurrentlyEnabledVersion() {
      return this.currentlyEnabledVersion;
   }

   public boolean isUsingProfile(DebugScreenProfile var1) {
      return this.profile == var1;
   }

   public void save() {
      DebugScreenEntryList.SerializedOptions var1 = new DebugScreenEntryList.SerializedOptions(Optional.ofNullable(this.profile), this.profile == null ? Optional.of(this.allStatuses) : Optional.empty());

      try {
         FileUtils.writeStringToFile(this.debugProfileFile, ((JsonElement)this.codec.encodeStart(JsonOps.INSTANCE, var1).getOrThrow()).toString(), StandardCharsets.UTF_8);
      } catch (IOException var3) {
         LOGGER.error("Failed to save debug profile file {}", this.debugProfileFile, var3);
      }

   }

   static record SerializedOptions(Optional<DebugScreenProfile> profile, Optional<Map<Identifier, DebugScreenEntryStatus>> custom) {
      private static final Codec<Map<Identifier, DebugScreenEntryStatus>> CUSTOM_ENTRIES_CODEC;
      public static final Codec<DebugScreenEntryList.SerializedOptions> CODEC;

      SerializedOptions(Optional<DebugScreenProfile> param1, Optional<Map<Identifier, DebugScreenEntryStatus>> param2) {
         super();
         this.profile = var1;
         this.custom = var2;
      }

      public Optional<DebugScreenProfile> profile() {
         return this.profile;
      }

      public Optional<Map<Identifier, DebugScreenEntryStatus>> custom() {
         return this.custom;
      }

      static {
         CUSTOM_ENTRIES_CODEC = Codec.unboundedMap(Identifier.CODEC, DebugScreenEntryStatus.CODEC);
         CODEC = RecordCodecBuilder.create((var0) -> {
            return var0.group(DebugScreenProfile.CODEC.optionalFieldOf("profile").forGetter(DebugScreenEntryList.SerializedOptions::profile), CUSTOM_ENTRIES_CODEC.optionalFieldOf("custom").forGetter(DebugScreenEntryList.SerializedOptions::custom)).apply(var0, DebugScreenEntryList.SerializedOptions::new);
         });
      }
   }
}
