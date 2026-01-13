package ml.pluto7073.chemicals.internal;

import ml.pluto7073.chemicals.handlers.ChemicalHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

public interface ChemicalHolderBuilder {

	default FoodProperties.Builder addChemical(ResourceLocation id, float amount) {
		return null;
	}

	default FoodProperties.Builder addChemical(ChemicalHandler handler, float amount) {
		return addChemical(handler.getId(), amount);
	}

}
