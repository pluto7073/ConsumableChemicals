package ml.pluto7073.chemicals.internal;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public interface ChemicalHolder {

	default void setChemicals(Map<ResourceLocation, Float> chemicals) {}

	default Map<ResourceLocation, Float> getChemicals() {
		return Map.of();
	}

}
