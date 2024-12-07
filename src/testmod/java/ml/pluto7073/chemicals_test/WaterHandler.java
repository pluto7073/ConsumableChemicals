package ml.pluto7073.chemicals_test;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.handlers.HalfLifeChemicalHandler;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;

public class WaterHandler extends HalfLifeChemicalHandler {

	public static final WaterHandler INSTANCE = new WaterHandler(6000);

	private WaterHandler(int halfLifeTicks) {
		super(halfLifeTicks);
	}

	@Override
	public Collection<MobEffectInstance> getEffectsForAmount(float amount, Level level) {
		ArrayList<MobEffectInstance> effects = new ArrayList<>();
		if (amount <= 10) {
			effects.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 2));
		}
		if (amount >= 500) {
			effects.add(new MobEffectInstance(MobEffects.CONFUSION, 30 * 20));
		}
		return effects;
	}

	public static void init() {
		Registry.register(Chemicals.REGISTRY, ExampleModTest.id("water"), INSTANCE);
	}

}
