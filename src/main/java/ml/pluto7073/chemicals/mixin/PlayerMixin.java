package ml.pluto7073.chemicals.mixin;

import ml.pluto7073.chemicals.Chemicals;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(at = @At("TAIL"), method = "defineSynchedData")
	private void chemicals$DefineChemicalTrackers(CallbackInfo ci) {
		Chemicals.REGISTRY.forEach(handler -> handler.defineDataForPlayer(getEntityData()));
	}

	@Inject(at = @At("HEAD"), method = "eat")
	private void chemicals$ConsumeFoodWithChemicals(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		FoodProperties food = stack.getItem().getFoodProperties();
		if (food == null) return;
		food.getChemicals().forEach((id, amount) -> {
			Objects.requireNonNull(Chemicals.REGISTRY.get(id)).add((Player) (Object) this, amount);
		});
	}

}
