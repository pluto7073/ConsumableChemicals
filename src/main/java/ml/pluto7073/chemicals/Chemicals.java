package ml.pluto7073.chemicals;

import ml.pluto7073.chemicals.commands.ChemicalCommands;
import ml.pluto7073.chemicals.component.ChemicalMap;
import ml.pluto7073.chemicals.handlers.ConsumableChemicalHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.apache.logging.log4j.LogManager;

public class Chemicals implements ModInitializer {

	public static final String MOD_ID = "chemicals";
	public static final ResourceKey<Registry<ConsumableChemicalHandler>> REGISTRY_KEY =
			ResourceKey.createRegistryKey(id("chemical_handler"));
	public static final Registry<ConsumableChemicalHandler> REGISTRY =
			BuiltInRegistries.registerSimple(REGISTRY_KEY, registry -> ConsumableChemicalHandler.EMPTY);

	@Override
	public void onInitialize() {
		ChemicalMap.init();
		ChemicalCommands.register();

		LogManager.getLogger("Chemicals").info("Ready to consume the chemicals");
	}

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}

}
