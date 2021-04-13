package net.ludocrypt.exdimapi.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.ludocrypt.exdimapi.mixin.ChunkGeneratorSettingsAccessor;
import net.ludocrypt.exdimapi.mixin.DimensionTypeAccessor;
import net.ludocrypt.exdimapi.mixin.MultiNoiseBiomeSourceAccessor;
import net.ludocrypt.exdimapi.util.DimensionUtil;
import net.ludocrypt.exdimapi.util.MusicController;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;

public class ExtraDimension {

	public final Identifier LOCAL_ID;
	public final RegistryKey<World> WORLD;
	public final RegistryKey<DimensionType> DIMENSION_KEY;
	public final RegistryKey<DimensionOptions> DIMENSION_OPTIONS_KEY;
	public final RegistryKey<ChunkGeneratorSettings> CHUNK_GENERATION_KEY;
	public final DimensionType DIMENSION_TYPE;
	public final MusicController MUSIC_SOUND_CONTROLLER;
	public final SkyProperties SKY_PROPERTIES;
	public final Map<RegistryKey<Biome>, Biome.MixedNoisePoint> NOISE_POINTS;
	public final MultiNoiseBiomeSource.Preset BIOME_SOURCE_PRESET;
	public final MultiNoiseBiomeSource.NoiseParameters TEMPERATURE_NOISE;
	public final MultiNoiseBiomeSource.NoiseParameters HUMIDITY_NOISE;
	public final MultiNoiseBiomeSource.NoiseParameters ALTITUDE_NOISE;
	public final MultiNoiseBiomeSource.NoiseParameters WEIRDNESS_NOISE;
	public final ChunkGeneratorSettings CHUNK_GENERATION_SETTINGS;

	public ExtraDimension(Identifier id, DimensionType dimensionType, SkyProperties skyProperties, MusicController musicController, MultiNoiseBiomeSource.NoiseParameters temperatureNoiseParameters, MultiNoiseBiomeSource.NoiseParameters humidityNoiseParameters, MultiNoiseBiomeSource.NoiseParameters altitudeNoiseParameters, MultiNoiseBiomeSource.NoiseParameters weirdnessNoiseParameters, ChunkGeneratorSettings chunkGenerationSettings, Map<RegistryKey<Biome>, Biome.MixedNoisePoint> noisePoints) {
		this.LOCAL_ID = id;
		this.WORLD = DimensionUtil.getWorld(LOCAL_ID);
		this.DIMENSION_KEY = DimensionUtil.getDimensionType(LOCAL_ID);
		this.DIMENSION_OPTIONS_KEY = DimensionUtil.getDimensionOptions(LOCAL_ID);
		this.CHUNK_GENERATION_KEY = DimensionUtil.getChunkGenerationSettings(LOCAL_ID);
		this.DIMENSION_TYPE = dimensionType;
		this.SKY_PROPERTIES = skyProperties;
		this.MUSIC_SOUND_CONTROLLER = musicController;
		this.TEMPERATURE_NOISE = temperatureNoiseParameters;
		this.HUMIDITY_NOISE = humidityNoiseParameters;
		this.ALTITUDE_NOISE = altitudeNoiseParameters;
		this.WEIRDNESS_NOISE = weirdnessNoiseParameters;
		this.NOISE_POINTS = noisePoints;
		this.BIOME_SOURCE_PRESET = new MultiNoiseBiomeSource.Preset(LOCAL_ID, (preset, registry, seed) -> {
			List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomes = new ArrayList<>();
			NOISE_POINTS.forEach((biomeKey, noisePoint) -> biomes.add(Pair.of(noisePoint, () -> registry.getOrThrow(biomeKey))));
			if (NOISE_POINTS.isEmpty()) {
				biomes.add(Pair.of(new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> registry.getOrThrow(BiomeKeys.PLAINS)));
			}
			return MultiNoiseBiomeSourceAccessor.createMultiNoiseBiomeSource(seed, biomes, TEMPERATURE_NOISE, HUMIDITY_NOISE, ALTITUDE_NOISE, WEIRDNESS_NOISE, Optional.of(Pair.of(registry, preset)));
		});
		this.CHUNK_GENERATION_SETTINGS = chunkGenerationSettings;
	}

	public ChunkGenerator createGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
		return new NoiseChunkGenerator(BIOME_SOURCE_PRESET.getBiomeSource(biomeRegistry, seed), seed, () -> {
			return chunkGeneratorSettingsRegistry.getOrThrow(CHUNK_GENERATION_KEY);
		});
	}

	public void init() {
		BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, LOCAL_ID, CHUNK_GENERATION_SETTINGS);
	}

	public static class Builder {
		private Identifier LOCAL_ID;
		private DimensionType DIMENSION_TYPE = DimensionTypeAccessor.createDimensionType(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, 256, HorizontalVoronoiBiomeAccessType.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getId(), new Identifier("overworld"), 0.0F);
		private MusicController MUSIC_SOUND_CONTROLLER = (dim, client, ci) -> {

		};
		private SkyProperties SKY_PROPERTIES = SkyProperties.byDimensionType(DIMENSION_TYPE);
		private MultiNoiseBiomeSource.NoiseParameters TEMPERATURE_NOISE = new MultiNoiseBiomeSource.NoiseParameters(-7, ImmutableList.of(1.0D, 1.0D));
		private MultiNoiseBiomeSource.NoiseParameters HUMIDITY_NOISE = new MultiNoiseBiomeSource.NoiseParameters(-7, ImmutableList.of(1.0D, 1.0D));
		private MultiNoiseBiomeSource.NoiseParameters ALTITUDE_NOISE = new MultiNoiseBiomeSource.NoiseParameters(-7, ImmutableList.of(1.0D, 1.0D));
		private MultiNoiseBiomeSource.NoiseParameters WEIRDNESS_NOISE = new MultiNoiseBiomeSource.NoiseParameters(-7, ImmutableList.of(1.0D, 1.0D));
		private ChunkGeneratorSettings CHUNK_GENERATION_SETTINGS = ChunkGeneratorSettingsAccessor.invokeCreateSurfaceSettings(new StructuresConfig(true), false, ChunkGeneratorSettings.OVERWORLD.getValue());
		private Map<RegistryKey<Biome>, Biome.MixedNoisePoint> NOISE_POINTS = new HashMap<>();

		public Builder(Identifier id) {
			this.LOCAL_ID = id;
		}

		public Builder addBiome(RegistryKey<Biome> biome, Biome.MixedNoisePoint noise) {
			NOISE_POINTS.put(biome, noise);
			return this;
		}

		public Builder setNoisePoints(Map<RegistryKey<Biome>, Biome.MixedNoisePoint> noisePoints) {
			NOISE_POINTS = noisePoints;
			return this;
		}

		public Builder setDimensionType(DimensionType dimensionType) {
			this.DIMENSION_TYPE = dimensionType;
			return this;
		}

		public Builder setDimensionType(OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean hasEnderDragonFight, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight, BiomeAccessType biomeAccessType, Identifier infiniburn, Identifier skyProperties, float ambientLight) {
			this.DIMENSION_TYPE = DimensionTypeAccessor.createDimensionType(fixedTime, hasSkylight, hasCeiling, ultrawarm, natural, coordinateScale, hasEnderDragonFight, piglinSafe, bedWorks, respawnAnchorWorks, hasRaids, logicalHeight, biomeAccessType, infiniburn, skyProperties, ambientLight);
			return this;
		}

		public Builder setMusicController(MusicController musicController) {
			this.MUSIC_SOUND_CONTROLLER = musicController;
			return this;
		}

		public Builder setSkyProperties(SkyProperties skyProperties) {
			this.SKY_PROPERTIES = skyProperties;
			return this;
		}

		public Builder setTemperatureNoise(MultiNoiseBiomeSource.NoiseParameters noise) {
			this.TEMPERATURE_NOISE = noise;
			return this;
		}

		public Builder setHumidityNoise(MultiNoiseBiomeSource.NoiseParameters noise) {
			this.HUMIDITY_NOISE = noise;
			return this;
		}

		public Builder setAltitudeNoise(MultiNoiseBiomeSource.NoiseParameters noise) {
			this.ALTITUDE_NOISE = noise;
			return this;
		}

		public Builder setWeirdnessNoise(MultiNoiseBiomeSource.NoiseParameters noise) {
			this.WEIRDNESS_NOISE = noise;
			return this;
		}

		public Builder setChunkGeneratorSettings(ChunkGeneratorSettings chunkGeneratorSettings) {
			this.CHUNK_GENERATION_SETTINGS = chunkGeneratorSettings;
			return this;
		}

		public Builder setChunkGeneratorSettings(StructuresConfig structuresConfig, GenerationShapeConfig generationShapeConfig, BlockState defaultBlock, BlockState defaultFluid, int bedrockCeilingY, int bedrockFloorY, int seaLevel, boolean mobGenerationDisabled) {
			this.CHUNK_GENERATION_SETTINGS = ChunkGeneratorSettingsAccessor.createChunkGeneratorSettings(structuresConfig, generationShapeConfig, defaultBlock, defaultFluid, bedrockCeilingY, bedrockFloorY, seaLevel, mobGenerationDisabled);
			return this;
		}

		public Identifier getId() {
			return LOCAL_ID;
		}

		public DimensionType getDimensionType() {
			return DIMENSION_TYPE;
		}

		public MusicController getMusicController() {
			return MUSIC_SOUND_CONTROLLER;
		}

		public SkyProperties getSkyProperties() {
			return SKY_PROPERTIES;
		}

		public MultiNoiseBiomeSource.NoiseParameters getTemperatureNoise() {
			return TEMPERATURE_NOISE;
		}

		public MultiNoiseBiomeSource.NoiseParameters getHumidityNoise() {
			return HUMIDITY_NOISE;
		}

		public MultiNoiseBiomeSource.NoiseParameters getAltitudeNoise() {
			return ALTITUDE_NOISE;
		}

		public MultiNoiseBiomeSource.NoiseParameters getWeirdnessNoise() {
			return WEIRDNESS_NOISE;
		}

		public ChunkGeneratorSettings getChunkGeneratorSettings() {
			return CHUNK_GENERATION_SETTINGS;
		}

		public ExtraDimension build() {
			if (LOCAL_ID == null) {
				throw new UnsupportedOperationException("Local Id null");
			}
			if (DIMENSION_TYPE == null) {
				throw new UnsupportedOperationException("Dimension Type Null");
			}
			if (MUSIC_SOUND_CONTROLLER == null) {
				throw new UnsupportedOperationException("Music Sound Controller null null");
			}
			if (SKY_PROPERTIES == null) {
				throw new UnsupportedOperationException("Sky Properties null");
			}
			if (TEMPERATURE_NOISE == null) {
				throw new UnsupportedOperationException("Temperature Noise null");
			}
			if (HUMIDITY_NOISE == null) {
				throw new UnsupportedOperationException("Humidity Noise null");
			}
			if (ALTITUDE_NOISE == null) {
				throw new UnsupportedOperationException("Altitude Noise null");
			}
			if (WEIRDNESS_NOISE == null) {
				throw new UnsupportedOperationException("Weirdness Noise null");
			}
			if (CHUNK_GENERATION_SETTINGS == null) {
				throw new UnsupportedOperationException("Chunk Generation Settings null");
			}
			if (NOISE_POINTS == null) {
				throw new UnsupportedOperationException("Noise Points null");
			}

			return new ExtraDimension(LOCAL_ID, DIMENSION_TYPE, SKY_PROPERTIES, MUSIC_SOUND_CONTROLLER, TEMPERATURE_NOISE, HUMIDITY_NOISE, ALTITUDE_NOISE, WEIRDNESS_NOISE, CHUNK_GENERATION_SETTINGS, NOISE_POINTS);
		}

	}

}
