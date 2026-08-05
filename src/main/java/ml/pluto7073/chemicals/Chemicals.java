package ml.pluto7073.chemicals;

import ml.pluto7073.chemicals.commands.ChemicalCommands;
import ml.pluto7073.chemicals.component.ChemicalMap;
import ml.pluto7073.chemicals.handlers.ChemicalHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class Chemicals implements ModInitializer {

	public static final String MOD_ID = "chemicals";
	public static final ResourceKey<Registry<ChemicalHandler>> CHEMICAL_HANDLER_KEY =
			ResourceKey.createRegistryKey(id("chemical_handler"));
	public static final DefaultedRegistry<ChemicalHandler> CHEMICAL_HANDLER =
			BuiltInRegistries.registerDefaulted(CHEMICAL_HANDLER_KEY, "chemicals:air", registry -> ChemicalHandler.EMPTY);
	public static final Logger LOGGER = LogManager.getLogger("Chemicals");

	@Override
	public void onInitialize() {
		ChemicalMap.init();
		ChemicalHandler.init();
		ChemicalCommands.register();

		LOGGER.info("Ready to consume the chemicals");
	}

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}

	public static Collection<ChemicalHandler> getAllChemicals() {
		return CHEMICAL_HANDLER.stream().filter(handler -> handler != ChemicalHandler.EMPTY).toList();
	}

}
