package ml.pluto7073.chemicals.handlers;

import net.minecraft.world.entity.player.Player;

/**
 * An implementation of <code>ConsumableChemicalHandler</code> for a chemical that stays in the player
 * and resets once a maximum amount is reached, performing an action on the player
 */
public abstract class StaticChemicalHandler extends ConsumableChemicalHandler {

	protected final float maxAmount;

	public StaticChemicalHandler(float maxAmount) {
		this.maxAmount = maxAmount;
	}

	public abstract void onMaxAmountReached(Player player);

	@Override
	public void tickPlayer(Player player) {
		if (get(player) > maxAmount) {
			set(player, 0);
			onMaxAmountReached(player);
		}
	}
}
