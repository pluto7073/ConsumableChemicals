package ml.pluto7073.chemicals.handlers;

import net.minecraft.world.entity.player.Player;

/**
 * An implementation of <code>ConsumableChemicalHandler</code> for a chemical that stays in the player
 * and resets once a maximum amount is reached, performing an action on the player
 */
public abstract class StaticChemicalHandler extends ChemicalHandler {

	/**
	 * Creates a new StaticChemicalHandler instance
	 * @param maxAmount The maximum amount of this chemical, or <code>0</code> if no limit is desired
	 */
	public StaticChemicalHandler(float maxAmount) {
		super(maxAmount);
	}

	public abstract void onMaxAmountReached(Player player);

	@Override
	public void doTick(Player player) {
		if (get(player) > maxRecommendedAmount && maxRecommendedAmount > 0) {
			set(player, 0);
			onMaxAmountReached(player);
		}
	}
}
