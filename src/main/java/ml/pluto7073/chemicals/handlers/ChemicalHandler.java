package ml.pluto7073.chemicals.handlers;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.handlers.ConsumedInstance.AbsorptionType;
import ml.pluto7073.chemicals.item.ChemicalContaining;
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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

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
public abstract class ChemicalHandler {

	public static final ChemicalHandler EMPTY =
			Registry.register(Chemicals.CHEMICAL_HANDLER, Chemicals.id("air"), new ChemicalHandler(0) {
				@Override
				public void doTick(Player player) {}

				@Override
				public Collection<MobEffectInstance> getEffectsForAmount(float amount, Level level) {
					return List.of();
				}

				@Override
				public float add(Player player, float amount) {
					return 0;
				}
			});

	protected final EntityDataAccessor<Float> accessor;
	protected final EntityDataAccessor<Integer> ticksAccessor;
	protected final float maxRecommendedAmount;

	public ChemicalHandler(float maxRecommendedAmount) {
		accessor = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
		ticksAccessor = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
		this.maxRecommendedAmount = maxRecommendedAmount;
	}

	public final void tickPlayer(Player player) {
		int ticks = player.getEntityData().get(ticksAccessor) + 1;
		doTick(player);
		if (get(player) == 0) {
			player.getEntityData().set(ticksAccessor, 0);
			return;
		}
		player.getEntityData().set(ticksAccessor, ticks);
	}

	/**
	 * @return the length in ticks that <code>player</code> has had this chemical in their system.  Resets to 0
	 * if amount equals 0.
	 */
	public int getTicksInPlayer(Player player) {
		return player.getEntityData().get(ticksAccessor);
	}

	/**
	 * Updates the current amount of the chemical in the specified player
	 * @param player The player to update
	 */
	protected abstract void doTick(Player player);

	/**
	 * Gets the current amount of the chemical in the player
	 * @param player The player to retrieve
	 * @return Amount of the chemical
	 */
	public float get(Player player) {
		return player.getEntityData().get(accessor);
	}

	/**
	 * @return The relative strength of the chemical in the specified <code>player</code> between 0 and 1, using the <code>maxRecommendedAmount</code>
	 */
	public float getStrength(Player player) {
		return get(player) / maxRecommendedAmount;
	}

	/**
	 * Adds the chemical to a player
	 * @return The new amount of the chemical in the player
	 * @deprecated Use {@link Player#addChemical(ChemicalHandler, AbsorptionType, float)}
	 * or {@link Player#addChemical(ConsumedInstance)} to properly add chemicals to a player
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

	public void defineDataForPlayer(SynchedEntityData data) {
		data.define(accessor, 0f);
		data.define(ticksAccessor, 0);
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
		return Chemicals.CHEMICAL_HANDLER.getResourceKey(this).orElseThrow().location();
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
	 * @return the maximum amount of this drug in units that the player should logically have in their system.
	 * This will not scale with any settings/game rules and doesn't serve as a hard limit, just a way to assign
	 * a percentage to the amount in a player's system.
	 */
	public float getMaxRecommendedAmount() {
		return maxRecommendedAmount;
	}

	/**
	 * Implement this method if you want custom syntax for this chemical's command using <code>/chemicals</code>
	 * @return A <code>LiteralArgumentBuilder</code> representing the entire subcommand for this chemical
	 * <p>
	 * <strong>Note: </strong> It is recommended that this begin with <code>literal("example:your_chemical")</code>
	 */
	public @Nullable LiteralArgumentBuilder<CommandSourceStack> createCustomChemicalCommandExtension() {
		return null;
	}

	/**
	 * Store any extra data required for the Chemical Handler
	 * @param player The player to store data from
	 * @param tag The CompoundTag to store the data in
	 */
	public void saveExtraPlayerData(Player player, CompoundTag tag) {
		int ticks = player.getEntityData().get(ticksAccessor);
		if (ticks > 0) {
			tag.putInt(getId().toString() + "/ticks", ticks);
		}
	}

	/**
	 * Load any extra data required for the Chemical Handler
	 * @param player The player to load to
	 * @param tag The tag to load from
	 */
	public void loadExtraPlayerData(Player player, CompoundTag tag) {
		if (tag.contains(getId().toString() + "/ticks")) {
			int ticks = tag.getInt(getId().toString() + "/ticks");
			player.getEntityData().set(ticksAccessor, ticks);
		}
	}

	/**
	 * Creates a new <code>ConsumedInstance</code> using this chemical handler.
	 * @param type The desired speed of absorption
	 * @param amount The total amount of the chemical to add
	 * @return The new consumed instance
	 */
	public ConsumedInstance createInstance(AbsorptionType type, float amount) {
		return new ConsumedInstance(this, type, amount);
	}

	/**
	 * Creates a new <code>ConsumedInstance</code> using this chemical handler and
	 * the specified item as an instance of {@link ChemicalContaining}.
	 * @param type The desired speed of absorption
	 * @param stack The item stack to get information from
	 * @return The new consumed instance
	 */
	public ConsumedInstance createInstance(AbsorptionType type, ItemStack stack, Level level) {
		if (!(stack.getItem() instanceof ChemicalContaining item)) return new ConsumedInstance(this, type, 0);
		return new ConsumedInstance(this, type, item.getConsumedChemicalContent(getId(), stack, level));
	}

	public void contrast(Vector4f rgba, Player player) {}

	public void bloom(Vector4f rgba, Player player) {}

	public static void init() {}

}
