package net.ludocrypt.exdimapi;

import net.fabricmc.api.ClientModInitializer;
import net.ludocrypt.exdimapi.mixin.SkyPropertiesAccessor;

public class ExtraDimsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ExtraDims.EXTRA_DIMENSIONS.forEach((id, dim) -> {
			SkyPropertiesAccessor.getBY_IDENTIFIER().put(dim.LOCAL_ID, dim.SKY_PROPERTIES);
			ExtraDims.LOGGER.info("Added Sky Property " + id + " to registry");
		});
	}

}
