package ml.pluto7073.chemicals.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.handlers.ConsumableChemicalHandler;
import ml.pluto7073.chemicals.item.ChemicalContainingItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@Unique
	private ItemStack chem$This() {
		return (ItemStack) (Object) this;
	}

	@Shadow
	public abstract Item getItem();

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;" +
							"Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"),
			method = "getTooltipLines"
	)
	private void chemicals$AddChemicalTooltipForFood(Player player, TooltipFlag context, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
		if (!getItem().isEdible() || getItem().getFoodProperties() == null) return;
		if (!(context.isCreative() || context.isAdvanced())) return;
		FoodProperties food = getItem().getFoodProperties();
		for (ConsumableChemicalHandler handler : Chemicals.REGISTRY) {
			if (!food.getChemicals().containsKey(handler.getId())) continue;
			handler.appendTooltip(list, food.getChemicals().get(handler.getId()), chem$This());
		}
	}

	@Inject(at = @At("HEAD"), method = "finishUsingItem")
	private void chemicals$AddChemicalsToPlayer(Level level, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
		if (!(user instanceof Player player) || level.isClientSide || !(getItem() instanceof ChemicalContainingItem item)) return;
		UseAnim anim = getItem().getUseAnimation(chem$This());
		if (!anim.equals(UseAnim.DRINK) && !anim.equals(UseAnim.EAT)) return;
		for (ConsumableChemicalHandler handler : Chemicals.REGISTRY) {
			handler.add(player, item.getChemicalContent(handler.getId(), chem$This()));
		}
	}

}
