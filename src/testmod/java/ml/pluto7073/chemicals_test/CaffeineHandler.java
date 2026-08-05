package ml.pluto7073.chemicals_test;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.handlers.HalfLifeChemicalHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;

public class CaffeineHandler extends HalfLifeChemicalHandler {

	public static final CaffeineHandler INSTANCE =
			new CaffeineHandler(3000, 2500/*, ChemicalModifiers.builder()
					.put(ChemicalModifier.SPEED, f -> 1 + f * 0.2f)
					.put(ChemicalModifier.DIG_SPEED, f -> 1 + f * 0.2f)
					.put(ChemicalModifier.SUPER_SATURATION, 0.3f)
					.put(ChemicalModifier.COLOR_HALLUCINATION, f -> ChemicalUtil.project(f * 1.3f, 0.7f, 1) * 0.03F)
					.put(ChemicalModifier.MOVEMENT_HALLUCINATION, f -> ChemicalUtil.project(f * 1.5f, 0.7f, 1) * 0.03f)
					.put(ChemicalModifier.CONTEXT_HALLUCINATION, f -> ChemicalUtil.project(f * 1.3f, 0.7f, 1) * 0.05f)
					.put(ChemicalModifier.HAND_TREMBLE, f -> ChemicalUtil.project(f, 0.6f, 1))
					.put(ChemicalModifier.VIEW_TREMBLE, f -> ChemicalUtil.project(f, 0.8f, 1))
					.put(ChemicalModifier.HAND_TWITCH, f -> ChemicalUtil.project(f,0.3f, 1) * 0.05f)
					.put(ChemicalModifier.JUMP_TWITCH, f -> ChemicalUtil.project(f, 0.6f, 1) * 0.07f)
					.build()*/);

	private CaffeineHandler(int halfLifeTicks, float maxRecommendedAmount) {
		super(halfLifeTicks, maxRecommendedAmount);
	}

	@Override
	public Collection<MobEffectInstance> getEffectsForAmount(float amount, Level level) {
		ArrayList<MobEffectInstance> list = new ArrayList<>();
		if (amount >= 100) {
			list.add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600));
		}
		if (amount >= 150) {
			list.add(new MobEffectInstance(MobEffects.DIG_SPEED, 600));
		}
		if (amount >= 300) {
			list.add(new MobEffectInstance(MobEffects.HUNGER, 600));
		}
		if (amount >= 400 && FabricLoader.getInstance().isModLoaded("dehydration")) {
			list.add(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getHolder(new ResourceLocation("dehydration:thirst_effect")).orElseThrow(),
					600, 0));
		}
		if (amount >= 450) {
			list.add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
		}
		if (amount >= 500) {
			list.add(new MobEffectInstance(MobEffects.JUMP, 600));
		}
		if (amount >= 600) {
			list.add(new MobEffectInstance(MobEffects.DIG_SPEED, 600, 1));
		}
		if (amount >= 700) {
			list.add(new MobEffectInstance(MobEffects.JUMP, 600, 1));
		}
		int lethalCaffeineDose = 3000;
		boolean overdose = true;
		if (overdose && amount >= lethalCaffeineDose) {
			list.add(new MobEffectInstance(MobEffects.POISON, 20 * 60));
		}
		return list;
	}

	public static void init() {
		Registry.register(Chemicals.CHEMICAL_HANDLER, ExampleModTest.id("caffeine"), INSTANCE);
	}

}
