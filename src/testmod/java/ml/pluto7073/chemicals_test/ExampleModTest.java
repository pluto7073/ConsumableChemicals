package ml.pluto7073.chemicals_test;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.food.FoodProperties;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.ItemStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleModTest implements ModInitializer {
	public static final String ID = "chemicals_test";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final FoodProperties WATER_PILL = new FoodProperties.Builder()
			.alwaysEat().addChemical(id("water"), 100.0f)
			.fast().build();

	public static final Item WATER_PILL_ITEM = new Item(new Item.Properties().food(WATER_PILL));

	@Override
	public void onInitialize() {
		WaterHandler.init();
		new CompoundTag().getFl
		Registry.register(BuiltInRegistries.ITEM, id("water_pill"), WATER_PILL_ITEM);
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
