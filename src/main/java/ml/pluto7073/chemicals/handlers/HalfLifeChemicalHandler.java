package ml.pluto7073.chemicals.handlers;

import net.minecraft.world.entity.player.Player;

/**
 * A Chemical handler that exponentially ticks down the amount in a player, using the specified half life.
 */
public abstract class HalfLifeChemicalHandler extends ConsumableChemicalHandler {

	protected final float perTickMultiplier;

	public HalfLifeChemicalHandler(int halfLifeTicks) {
		perTickMultiplier = (float) Math.pow(0.5, 1.0 / halfLifeTicks);
	}

	@Override
	public void tickPlayer(Player player) {
		float amount = get(player) * perTickMultiplier;
		if (amount <= 0.0001f) amount = 0f;
		set(player, amount);
	}

}
