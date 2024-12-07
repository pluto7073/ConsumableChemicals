package ml.pluto7073.chemicals.item;

import ml.pluto7073.chemicals.Chemicals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.food.FoodProperties;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An abstract class for items that can contain a variable amount of any chemical
 * in a stack.  When an item that inherits <code>ChemicalContainingItem</code> is
 * finished using by a player and the <code>UseAnim</code> of the item is {@link UseAnim#DRINK}
 * or {@link UseAnim#EAT}, any chemicals in the item will be added to the player
 * <p>
 * Note: This class is intended for items that have a variable amount of chemicals.
 * For Items that will always have the same amount of chemicals, use {@link FoodProperties.Builder#addChemical(ResourceLocation, float)}
 */
public abstract class ChemicalContainingItem extends Item {

	protected ChemicalContainingItem(Properties settings) {
		super(settings);
	}

	/**
	 * Obtains the amount of a specific chemical stored in a stack
	 * @param id The ID representing the chemical
	 * @param stack The itemStack
	 * @return Amount of the desired chemical stored in the stack
	 */
	public abstract float getChemicalContent(ResourceLocation id, ItemStack stack);

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {

		Chemicals.REGISTRY.forEach(handler -> {
			float amount = getChemicalContent(handler.getId(), stack);
			handler.appendTooltip(tooltip, amount, stack);
		});

	}
}
