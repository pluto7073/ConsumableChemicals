package ml.pluto7073.chemicals.mixin;

import ml.pluto7073.chemicals.Chemicals;
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
	private void chemicals$DefineChemicalTrackers(CallbackInfo ci) {
		Chemicals.CHEMICAL_HANDLER.forEach(handler -> handler.defineDataForPlayer(getEntityData()));
	}

	@Inject(at = @At("HEAD"), method = "eat")
	private void chemicals$ConsumeFoodWithChemicals(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		if (world.isClientSide) return;
		FoodProperties food = stack.getItem().getFoodProperties();
		if (food == null) return;
		if (stack.getUseAnimation() != UseAnim.EAT && stack.getUseAnimation() != UseAnim.DRINK) return;
		AbsorptionType type = stack.getUseAnimation() == UseAnim.EAT ? AbsorptionType.EAT : AbsorptionType.DRINK;
		food.getChemicals().forEach((id, amount) -> {
			addChemical(Chemicals.CHEMICAL_HANDLER.get(id), type, amount);
		});
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
