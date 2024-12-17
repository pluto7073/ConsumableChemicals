package ml.pluto7073.chemicals.component;

import com.mojang.serialization.Codec;

import com.mojang.serialization.Keyable;

import ml.pluto7073.chemicals.Chemicals;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record ChemicalMap(Map<ResourceLocation, Float> chemicals) {

	public static final Codec<ChemicalMap> CODEC = Codec.simpleMap(
			ResourceLocation.CODEC, Codec.FLOAT,
			Keyable.forStrings(() -> Chemicals.REGISTRY.keySet().stream().map(ResourceLocation::toString))
	).xmap(ChemicalMap::new, ChemicalMap::chemicals).codec();

	public static final StreamCodec<RegistryFriendlyByteBuf, ChemicalMap> STREAM_CODEC =
			ByteBufCodecs.fromCodecWithRegistries(CODEC);

	public static final DataComponentType<ChemicalMap> COMPONENT_TYPE =
			DataComponentType.<ChemicalMap>builder()
					.persistent(CODEC).networkSynchronized(STREAM_CODEC).build();

	public static class Builder {

		public HashMap<ResourceLocation, Float> chemicals = new HashMap<>();

		public Builder add(ResourceLocation id, float amount) {
			chemicals.put(id, amount);
			return this;
		}

		public ChemicalMap build() {
			return new ChemicalMap(chemicals);
		}

	}

	public static void init() {
		Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Chemicals.id("chemicals"), COMPONENT_TYPE
		);
	}

}
