package ml.pluto7073.chemicals_test;

import ml.pluto7073.chemicals.component.ChemicalMap;
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

	public static final Item WATER_PILL_ITEM = new Item(new Item.Properties()
			.component(ChemicalMap.COMPONENT_TYPE,
					new ChemicalMap.Builder().add(id("water"), 100f).build())
			.food(new FoodProperties.Builder().fast().alwaysEdible().build()));

	@Override
	public void onInitialize() {
		WaterHandler.init();
		Registry.register(BuiltInRegistries.ITEM, id("water_pill"), WATER_PILL_ITEM);
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
