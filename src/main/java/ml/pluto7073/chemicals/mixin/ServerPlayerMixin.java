package ml.pluto7073.chemicals.mixin;

import com.mojang.authlib.GameProfile;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.handlers.ConsumableChemicalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

	public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(at = @At("TAIL"), method = "tick")
	private void chemicals$TickChemicalData(CallbackInfo ci) {
		Chemicals.REGISTRY.forEach(handler -> handler.tickPlayer(this));
	}

	@Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
	private void chemicals$ReadChemicalData(CompoundTag nbt, CallbackInfo ci) {
		if (!nbt.contains("Chemicals")) return;
		CompoundTag data = nbt.getCompound("Chemicals");
		CompoundTag extra = data.contains("ExtraData") ? data.getCompound("ExtraData") : new CompoundTag();
		for (ConsumableChemicalHandler handler : Chemicals.REGISTRY) {
			handler.loadExtraPlayerData(this, extra);
			if (!data.contains(handler.getId().toString())) continue;
			float amount = data.getFloat(handler.getId().toString());
			handler.set(this, amount);
		}
	}

	@Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
	private void chemicals$SaveChemicalData(CompoundTag nbt, CallbackInfo ci) {
		CompoundTag tag = new CompoundTag();
		CompoundTag extra = new CompoundTag();
		for (ConsumableChemicalHandler handler : Chemicals.REGISTRY) {
			handler.saveExtraPlayerData(this, extra);
			float amount = handler.get(this);
			if (amount == 0) continue;
			tag.putFloat(handler.getId().toString(), amount);
		}
		tag.put("ExtraData", extra);
		nbt.put("Chemicals", tag);
	}

	@Inject(at = @At("TAIL"), method = "tick")
	private void chemicals$ApplyChemicalEffects(CallbackInfo ci) {
		Chemicals.REGISTRY.forEach(handler -> {
			float amount = handler.get(this);
			handler.getEffectsForAmount(amount, level()).forEach(this::addEffect);
		});
	}

}
