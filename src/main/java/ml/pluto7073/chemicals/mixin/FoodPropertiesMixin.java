package ml.pluto7073.chemicals.mixin;

import com.google.common.collect.ImmutableMap;

import ml.pluto7073.chemicals.item.internal.ChemicalHolder;
import ml.pluto7073.chemicals.item.internal.ChemicalHolderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin implements ChemicalHolder {

	@Unique private final Map<ResourceLocation, Float> chemicals$Chemicals = new HashMap<>();

	@Override
	public void setChemicals(Map<ResourceLocation, Float> chemicals) {
		chemicals$Chemicals.clear();
		chemicals$Chemicals.putAll(chemicals);
	}

	@Override
	public Map<ResourceLocation, Float> getChemicals() {
		return ImmutableMap.copyOf(chemicals$Chemicals);
	}

	@Mixin(FoodProperties.Builder.class)
	public static class BuilderMixin implements ChemicalHolderBuilder {

		@Unique private final Map<ResourceLocation, Float> chemicals$Chemicals = new HashMap<>();

		@Override
		public FoodProperties.Builder addChemical(ResourceLocation id, float amount) {
			chemicals$Chemicals.put(id, amount);
			return (FoodProperties.Builder) (Object) this;
		}

		@Inject(at = @At("RETURN"), method = "build")
		private void chemicals$DispatchToProperties(CallbackInfoReturnable<FoodProperties> cir) {
			FoodProperties food = cir.getReturnValue();
			food.setChemicals(chemicals$Chemicals);
		}

	}

}
