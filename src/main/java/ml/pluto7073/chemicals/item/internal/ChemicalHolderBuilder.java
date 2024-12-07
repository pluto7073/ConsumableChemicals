package ml.pluto7073.chemicals.item.internal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

public interface ChemicalHolderBuilder {

	default FoodProperties.Builder addChemical(ResourceLocation id, float amount) {
		return null;
	}

}
