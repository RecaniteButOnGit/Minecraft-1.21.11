package net.minecraft.util.profiling.jfr.parse;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.util.profiling.jfr.serialize.JfrResultJsonSerializer;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.ChunkIdentification;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.FpsStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.IoSummary;
import net.minecraft.util.profiling.jfr.stats.PacketIdentification;
import net.minecraft.util.profiling.jfr.stats.StructureGenStat;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import net.minecraft.util.profiling.jfr.stats.TimedStatSummary;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

public record JfrStatsResult(Instant recordingStarted, Instant recordingEnded, Duration recordingDuration, @Nullable Duration worldCreationDuration, List<FpsStat> fps, List<TickTimeStat> serverTickTimes, List<CpuLoadStat> cpuLoadStats, GcHeapStat.Summary heapSummary, ThreadAllocationStat.Summary threadAllocationSummary, IoSummary<PacketIdentification> receivedPacketsSummary, IoSummary<PacketIdentification> sentPacketsSummary, IoSummary<ChunkIdentification> writtenChunks, IoSummary<ChunkIdentification> readChunks, FileIOStat.Summary fileWrites, FileIOStat.Summary fileReads, List<ChunkGenStat> chunkGenStats, List<StructureGenStat> structureGenStats) {
   public JfrStatsResult(Instant param1, Instant param2, Duration param3, @Nullable Duration param4, List<FpsStat> param5, List<TickTimeStat> param6, List<CpuLoadStat> param7, GcHeapStat.Summary param8, ThreadAllocationStat.Summary param9, IoSummary<PacketIdentification> param10, IoSummary<PacketIdentification> param11, IoSummary<ChunkIdentification> param12, IoSummary<ChunkIdentification> param13, FileIOStat.Summary param14, FileIOStat.Summary param15, List<ChunkGenStat> param16, List<StructureGenStat> param17) {
      super();
      this.recordingStarted = var1;
      this.recordingEnded = var2;
      this.recordingDuration = var3;
      this.worldCreationDuration = var4;
      this.fps = var5;
      this.serverTickTimes = var6;
      this.cpuLoadStats = var7;
      this.heapSummary = var8;
      this.threadAllocationSummary = var9;
      this.receivedPacketsSummary = var10;
      this.sentPacketsSummary = var11;
      this.writtenChunks = var12;
      this.readChunks = var13;
      this.fileWrites = var14;
      this.fileReads = var15;
      this.chunkGenStats = var16;
      this.structureGenStats = var17;
   }

   public List<Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>>> chunkGenSummary() {
      Map var1 = (Map)this.chunkGenStats.stream().collect(Collectors.groupingBy(ChunkGenStat::status));
      return var1.entrySet().stream().map((var0) -> {
         return Pair.of((ChunkStatus)var0.getKey(), TimedStatSummary.summary((List)var0.getValue()));
      }).filter((var0) -> {
         return ((Optional)var0.getSecond()).isPresent();
      }).map((var0) -> {
         return Pair.of((ChunkStatus)var0.getFirst(), (TimedStatSummary)((Optional)var0.getSecond()).get());
      }).sorted(Comparator.comparing((var0) -> {
         return ((TimedStatSummary)var0.getSecond()).totalDuration();
      }).reversed()).toList();
   }

   public String asJson() {
      return (new JfrResultJsonSerializer()).format(this);
   }

   public Instant recordingStarted() {
      return this.recordingStarted;
   }

   public Instant recordingEnded() {
      return this.recordingEnded;
   }

   public Duration recordingDuration() {
      return this.recordingDuration;
   }

   @Nullable
   public Duration worldCreationDuration() {
      return this.worldCreationDuration;
   }

   public List<FpsStat> fps() {
      return this.fps;
   }

   public List<TickTimeStat> serverTickTimes() {
      return this.serverTickTimes;
   }

   public List<CpuLoadStat> cpuLoadStats() {
      return this.cpuLoadStats;
   }

   public GcHeapStat.Summary heapSummary() {
      return this.heapSummary;
   }

   public ThreadAllocationStat.Summary threadAllocationSummary() {
      return this.threadAllocationSummary;
   }

   public IoSummary<PacketIdentification> receivedPacketsSummary() {
      return this.receivedPacketsSummary;
   }

   public IoSummary<PacketIdentification> sentPacketsSummary() {
      return this.sentPacketsSummary;
   }

   public IoSummary<ChunkIdentification> writtenChunks() {
      return this.writtenChunks;
   }

   public IoSummary<ChunkIdentification> readChunks() {
      return this.readChunks;
   }

   public FileIOStat.Summary fileWrites() {
      return this.fileWrites;
   }

   public FileIOStat.Summary fileReads() {
      return this.fileReads;
   }

   public List<ChunkGenStat> chunkGenStats() {
      return this.chunkGenStats;
   }

   public List<StructureGenStat> structureGenStats() {
      return this.structureGenStats;
   }
}
