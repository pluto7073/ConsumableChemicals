package ml.pluto7073.chemicals.mixin;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.component.ChemicalMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.SynchedEntityData;
import ml.pluto7073.chemicals.handlers.ConsumedInstance;
import ml.pluto7073.chemicals.handlers.ConsumedInstance.AbsorptionType;
import ml.pluto7073.chemicals.internal.ChemicalConsumer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements ChemicalConsumer {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(at = @At("TAIL"), method = "defineSynchedData")
	private void chemicals$DefineChemicalTrackers(SynchedEntityData.Builder builder, CallbackInfo ci) {
		Chemicals.CHEMICAL_HANDLER.forEach(handler -> handler.defineDataForPlayer(builder));
	}

	@Inject(at = @At("HEAD"), method = "eat")
	private void chemicals$ConsumeFoodWithChemicals(Level level, ItemStack food, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
		if (level.isClientSide) return;
		if (!food.has(ChemicalMap.COMPONENT_TYPE)) return;
		ChemicalMap chemicals = food.get(ChemicalMap.COMPONENT_TYPE);
		if (chemicals == null) return;
		if (food.getUseAnimation() != UseAnim.DRINK && food.getUseAnimation() != UseAnim.EAT) return;
		AbsorptionType type = food.getUseAnimation() == UseAnim.DRINK ? AbsorptionType.DRINK : AbsorptionType.EAT;
		chemicals.chemicals().forEach((id, amount) -> addChemical(Chemicals.CHEMICAL_HANDLER.get(id), type, amount));
	}

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public void addChemical(ConsumedInstance instance) {
		ChemicalConsumer.super.addChemical(instance);
	}

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public void clearChemicalInstances() {
		ChemicalConsumer.super.clearChemicalInstances();
	}
}
