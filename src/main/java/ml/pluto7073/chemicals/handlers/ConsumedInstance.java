package ml.pluto7073.chemicals.handlers;

import ml.pluto7073.chemicals.Chemicals;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

@MethodsReturnNonnullByDefault
public class ConsumedInstance {

	private final ChemicalHandler handler;
	private final AbsorptionType type;
	private final float totalAmount;

	private float remaining;

	ConsumedInstance(ChemicalHandler handler, AbsorptionType type, float totalAmount) {
		this.handler = handler;
		this.type = type;
		this.totalAmount = totalAmount;
		remaining = totalAmount;
	}

	/**
	 * Tests if another Consumed Instance can successfully be merged with this one, meaning is it the same
	 * chemical and type of consumption
	 * @param other The other instance to compare
	 * @return whether they can be merged or not
	 */
	public boolean matches(ConsumedInstance other) {
		return other.type == type && other.handler == handler;
	}

	public float remaining() {
		return remaining;
	}

	public boolean update(Player player) {
		float absorbed = Math.min(type.getAbsorbedAmount(remaining), remaining);
		remaining -= absorbed;
		if (Math.abs(remaining) < 0.00001 * Math.abs(totalAmount)) {
			absorbed += remaining;
			remaining = 0;
		}
		handler.add(player, absorbed);
		return remaining == 0;
	}

	public ConsumedInstance copy() {
		return new ConsumedInstance(handler, type, totalAmount);
	}

	public ConsumedInstance copyWithAmount(float amount) {
		return new ConsumedInstance(handler, type, amount);
	}

	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Chemical", Chemicals.CHEMICAL_HANDLER.getKey(handler).toString());
		tag.putString("Type", type.toString());
		tag.putFloat("Total", totalAmount);
		tag.putFloat("Remaining", remaining);
		return tag;
	}

	public static ConsumedInstance load(CompoundTag tag) {
		ChemicalHandler handler = Chemicals.CHEMICAL_HANDLER.get(ResourceLocation.parse(tag.getString("Chemical")));
		AbsorptionType type = AbsorptionType.valueOf(tag.getString("Type"));
		float total = tag.getFloat("Total");
		float remaining = tag.getFloat("Remaining");
		ConsumedInstance instance = new ConsumedInstance(handler, type, total);
		instance.remaining = remaining;
		return instance;
	}

	/**
	 * Represents the type of chemical absorption that should be used when adding a chemical to a player.
	 * The <code>rate</code> value represents the percentage delay in absorbing all the chemical.
	 */
	public enum AbsorptionType implements StringRepresentable {

		INSTANT(0),
		DRINK(0.987794643221f),
		EAT(0.992658806871f),
		SOLID_INHALE(0.581709132937f),
		GAS_INHALE(0.895364369711f);

		private final float rate;

		AbsorptionType(float rate) {
			this.rate = rate;
		}

		float getAbsorbedAmount(float remaining) {
			return remaining * (1 - rate);
		}

		@Override
		public String getSerializedName() {
			return name().toLowerCase();
		}
	}

}
