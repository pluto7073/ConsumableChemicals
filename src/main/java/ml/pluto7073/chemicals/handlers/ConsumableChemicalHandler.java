package ml.pluto7073.chemicals.handlers;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.mojang.serialization.Codec;

import ml.pluto7073.chemicals.Chemicals;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * The base class representing a Chemical
 * <p>
 * Three pre-made handlers already exist:
 * <p>
 * {@link HalfLifeChemicalHandler}
 * <p>
 * The most realistic of the two.  Exponentially ticks down the amount of the chemical in the player's body
 * based on the half life in ticks specified
 * <p>
 * {@link LinearChemicalHandler}
 * <p>
 * The simplest handler.  Removed a set amount of chemical from the player each tick, until the amount reaches zero
 * <p>
 * {@link StaticChemicalHandler}
 * <p>
 * For chemicals that stay persistent in the player until death or a maximum amount is reached, where the latter
 * will perform an action on the player defined in {@link StaticChemicalHandler#onMaxAmountReached(Player)}
 */
public abstract class ConsumableChemicalHandler {

	public static final ConsumableChemicalHandler EMPTY =
			Registry.register(Chemicals.REGISTRY, Chemicals.id("empty"), new ConsumableChemicalHandler() {
				@Override
				public void tickPlayer(Player player) {}

				@Override
				public Collection<MobEffectInstance> getEffectsForAmount(float amount, Level level) {
					return List.of();
				}

				@Override
				public float get(Player player) {
					return 0;
				}

				@Override
				public float add(Player player, float amount) { return 0; }

				@Override
				public void set(Player player, float amount) {}
			});

	protected final EntityDataAccessor<Float> accessor;

	public ConsumableChemicalHandler() {
		accessor = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
	}

	/**
	 * Updates the current amount of the chemical in the specified player
	 * @param player The player to update
	 */
	public abstract void tickPlayer(Player player);

	/**
	 * Gets the current amount of the chemical in the player
	 * @param player The player to retrieve
	 * @return Amount of the chemical
	 */
	public float get(Player player) {
		return player.getEntityData().get(accessor);
	}

	/**
	 * Adds the chemical to a player
	 * @param player The player to add to
	 * @param amount The amount of the chemical to add
	 * @return The new amount of the chemical in the player
	 */
	public float add(Player player, float amount) {
		float current = player.getEntityData().get(accessor);
		current += amount;
		if (current < 0) current = 0;
		player.getEntityData().set(accessor, current);
		return current;
	}

	/**
	 * Sets amount of chemical in a player
	 * @param player The player to set
	 * @param amount Amount of chemical
	 */
	public void set(Player player, float amount) {
		player.getEntityData().set(accessor, Math.max(0f, amount));
	}

	/**
	 * Get a list of effects associated with an amount of this chemical
	 * @param amount Amount of the chemical
	 * @param level The current Level, for utility purposes
	 * @return A list of <code>MobEffectInstance</code>s
	 */
	public abstract Collection<MobEffectInstance> getEffectsForAmount(float amount, Level level);

	public void defineDataForPlayer(SynchedEntityData.Builder data) {
		data.define(accessor, 0f);
	}

	/**
	 * Appends a tooltip to an item containing this chemical
	 * @param tooltip The current list of tooltips
	 * @param amount The amount in the stack
	 * @param stack The current stack, for utility purposes
	 */
	public void appendTooltip(List<Component> tooltip, float amount, ItemStack stack) {
		tooltip.add(Component.translatable("tooltip.chemicals.amount", formatAmount(amount), Component.translatable(getLanguageKey())));
	}

	public ResourceLocation getId() {
		return Chemicals.REGISTRY.getResourceKey(this).orElseThrow().location();
	}

	public String getLanguageKey() {
		return getId().toLanguageKey("chemical_handler");
	}

	/**
	 * Appends the units that this amount is tracked in to the amount, e.g. <code>amount + "mg"</code> or
	 * <code>amount + "L"</code>
	 */
	public String formatAmount(float amount) {
		return amount + "u";
	}

	/**
	 * Implement this method if you want custom syntax for this chemical's command using <code>/chemicals</code>
	 * @return A <code>LiteralArgumentBuilder</code> representing the entire subcommand for this chemical
	 * <p>
	 * <strong>Note: </strong> It is recommended that this begin with <code>literal("YOUR_CHEMICAL_ID")</code>
	 */
	public @Nullable LiteralArgumentBuilder<CommandSourceStack> createCustomChemicalCommandExtension() {
		return null;
	}

	/**
	 * Store any extra data required for the Chemical Handler
	 * @param player The player to store data from
	 * @param tag The CompoundTag to store the data in
	 */
	public void saveExtraPlayerData(Player player, CompoundTag tag) {}

	/**
	 * Load any extra data required for the Chemical Handler
	 * @param player The player to load to
	 * @param tag The tag to load from
	 */
	public void loadExtraPlayerData(Player player, CompoundTag tag) {}

}
