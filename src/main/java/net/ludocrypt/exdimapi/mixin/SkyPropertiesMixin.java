package net.ludocrypt.exdimapi.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.exdimapi.ExtraDims;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(SkyProperties.class)
public class SkyPropertiesMixin {

	@Shadow
	@Final
	private static Object2ObjectMap<Identifier, SkyProperties> BY_IDENTIFIER;

	static {
		ExtraDims.EXTRA_DIMENSIONS.forEach((id, dim) -> {
			BY_IDENTIFIER.put(dim.LOCAL_ID, dim.SKY_PROPERTIES);
			ExtraDims.LOGGER.info("Added Sky Property " + id + " to registry");
		});
	}

}
