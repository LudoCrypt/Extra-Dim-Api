package net.ludocrypt.exdimapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

@Mixin(DimensionType.class)
public interface SkyPropertiesAccessor {

	@Accessor("BY_IDENTIFIER")
	public static Object2ObjectMap<Identifier, SkyProperties> getBY_IDENTIFIER() {
		throw new UnsupportedOperationException();
	}

}
