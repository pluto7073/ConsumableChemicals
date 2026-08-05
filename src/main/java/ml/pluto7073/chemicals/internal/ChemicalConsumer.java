package ml.pluto7073.chemicals.internal;

import ml.pluto7073.chemicals.handlers.ChemicalHandler;
import ml.pluto7073.chemicals.handlers.ConsumedInstance;

public interface ChemicalConsumer {

	default void addChemical(ChemicalHandler handler, ConsumedInstance.AbsorptionType type, float amount) {
		addChemical(handler.createInstance(type, amount));
	}

	default void addChemical(ConsumedInstance instance) {}

	default void clearChemicalInstances() {}

}
