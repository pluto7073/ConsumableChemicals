package ml.pluto7073.chemicals.mixin;

import com.mojang.authlib.GameProfile;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.handlers.ChemicalHandler;
import ml.pluto7073.chemicals.handlers.ConsumedInstance;
import ml.pluto7073.chemicals.internal.ChemicalConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements ChemicalConsumer {

	@Unique private final List<ConsumedInstance> chemicals$consumedInstances = new ArrayList<>();

	public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(at = @At("TAIL"), method = "tick")
	private void chemicals$TickChemicalData(CallbackInfo ci) {
		Chemicals.CHEMICAL_HANDLER.forEach(handler -> handler.tickPlayer(this));
		List<ConsumedInstance> added = chemicals$consumedInstances.stream().filter(instance -> instance.update(this)).toList();
		chemicals$consumedInstances.removeAll(added);
	}

	@Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
	private void chemicals$ReadChemicalData(CompoundTag nbt, CallbackInfo ci) {
		clearChemicalInstances();
		if (!nbt.contains("Chemicals")) return;
		CompoundTag data = nbt.getCompound("Chemicals");
		CompoundTag extra = data.contains("ExtraData") ? data.getCompound("ExtraData") : new CompoundTag();
		for (ChemicalHandler handler : Chemicals.CHEMICAL_HANDLER) {
			handler.loadExtraPlayerData(this, extra);
			if (!data.contains(handler.getId().toString())) continue;
			float amount = data.getFloat(handler.getId().toString());
			handler.set(this, amount);
		}
		if (!data.contains("Consumed")) return;
		ListTag consumed = data.getList("Consumed", Tag.TAG_COMPOUND);
		for (Tag tag : consumed) {
			if (!(tag instanceof CompoundTag inst)) continue;
			chemicals$consumedInstances.add(ConsumedInstance.load(inst));
		}
	}

	@Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
	private void chemicals$SaveChemicalData(CompoundTag nbt, CallbackInfo ci) {
		CompoundTag tag = new CompoundTag();
		CompoundTag extra = new CompoundTag();
		for (ChemicalHandler handler : Chemicals.CHEMICAL_HANDLER) {
			handler.saveExtraPlayerData(this, extra);
			float amount = handler.get(this);
			if (amount == 0) continue;
			tag.putFloat(handler.getId().toString(), amount);
		}
		tag.put("ExtraData", extra);
		nbt.put("Chemicals", tag);
		ListTag consumed = new ListTag();
		for (ConsumedInstance instance : chemicals$consumedInstances) {
			consumed.add(instance.save());
		}
		nbt.put("Consumed", consumed);
	}

	@Inject(at = @At("TAIL"), method = "tick")
	private void chemicals$ApplyChemicalEffects(CallbackInfo ci) {
		Chemicals.CHEMICAL_HANDLER.forEach(handler -> {
			float amount = handler.get(this);
			handler.getEffectsForAmount(amount, level()).forEach(this::addEffect);
		});
	}

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public void addChemical(ConsumedInstance instance) {
		for (int i = 0; i < chemicals$consumedInstances.size(); i++) {
			ConsumedInstance existing = chemicals$consumedInstances.get(i);
			if (existing.matches(instance)) {
				chemicals$consumedInstances.set(i, existing.copyWithAmount(existing.remaining() + instance.remaining()));
				return;
			}
		}
		chemicals$consumedInstances.add(instance);
	}

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public void clearChemicalInstances() {
		chemicals$consumedInstances.clear();
		Chemicals.CHEMICAL_HANDLER.forEach(handler -> handler.set(this, 0));
	}
}
