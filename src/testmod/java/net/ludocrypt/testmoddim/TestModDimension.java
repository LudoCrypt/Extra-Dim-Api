package net.ludocrypt.testmoddim;

import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.fabricmc.api.ModInitializer;
import net.ludocrypt.exdimapi.api.ExtraDimApi;
import net.ludocrypt.exdimapi.api.ExtraDimension;
import net.ludocrypt.exdimapi.util.NoiseSettings;
import net.minecraft.block.Blocks;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;

public class TestModDimension implements ModInitializer, ExtraDimApi {

	@Override
	public void onInitialize() {

	}

	@Override
	public void registerModDimensions(Map<Identifier, ExtraDimension> registry) {
		ExtraDimension.Builder plainsExtra = new ExtraDimension.Builder(new Identifier("testmoddim", "plains_extra"));
		plainsExtra.addBiome(BiomeKeys.BADLANDS, new Biome.MixedNoisePoint(0, 0, 0, 0, 0));
		plainsExtra.addBiome(BiomeKeys.BEACH, new Biome.MixedNoisePoint(0.2F, 0, 0.5F, 0, 0));
		plainsExtra.setChunkGeneratorSettings(new StructuresConfig(false), new GenerationShapeConfig(256, new NoiseSamplingConfig(0.514507745D, 0.693358845D, 50.928453884D, 128.954865687D), new SlideConfig(0, 0, 0), new SlideConfig(0, 0, 0), 1, 2, 2.1399846D, 1.8662893D, true, true, false, false), Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), -10, 0, -1, false);
		plainsExtra.setAltitudeNoise(new NoiseSettings(1, ImmutableList.of(0.15D, 0.958D, 0.325D, -0.88D, 12.05D)));
		plainsExtra.setMusicController((dim, client, ci) -> {
			if (client.world.getRegistryKey().equals(dim.WORLD) && dim.LOCAL_ID.equals(plainsExtra.getId())) {
				ci.setReturnValue(new MusicSound(SoundEvents.MUSIC_DISC_BLOCKS, 0, 0, false));
			}
		});
		registry.put(new Identifier("testmoddim", "plains_extra"), plainsExtra.build());
	}

}
