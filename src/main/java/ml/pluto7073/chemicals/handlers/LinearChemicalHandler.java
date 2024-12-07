package ml.pluto7073.chemicals.handlers;

import net.minecraft.world.entity.player.Player;

/**
 * A Chemical Handler that removes a fixed amount from a player per tick
 */
public abstract class LinearChemicalHandler extends ConsumableChemicalHandler {

	private final float removePerTick;

	public LinearChemicalHandler(float removePerTick) {
		this.removePerTick = removePerTick;
	}

	@Override
	public void tickPlayer(Player player) {
		float amount = get(player) - removePerTick;
		if (amount < 0) amount = 0;
		set(player, amount);
	}

}
