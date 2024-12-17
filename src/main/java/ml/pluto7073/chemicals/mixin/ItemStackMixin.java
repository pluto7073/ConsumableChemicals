package ml.pluto7073.chemicals.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.component.ChemicalMap;
import ml.pluto7073.chemicals.handlers.ConsumableChemicalHandler;
import ml.pluto7073.chemicals.item.ChemicalContaining;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
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
public abstract class ItemStackMixin implements DataComponentHolder {

	@Unique
	private ItemStack chem$This() {
		return (ItemStack) (Object) this;
	}

	@Shadow
	public abstract Item getItem();

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/Item;" +
							"appendHoverText(Lnet/minecraft/world/item/ItemStack;" +
							"Lnet/minecraft/world/item/Item$TooltipContext;" +
							"Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"),
			method = "getTooltipLines"
	)
	private void chemicals$AddChemicalsFromComponent(Item.TooltipContext context, Player player, TooltipFlag config, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
		if (!has(ChemicalMap.COMPONENT_TYPE)) return;
		ChemicalMap chemicals = get(ChemicalMap.COMPONENT_TYPE);
		if (chemicals == null) return;
		chemicals.chemicals().forEach((id, amount) -> Chemicals.REGISTRY.getOptional(id)
				.ifPresent(handler -> handler.appendTooltip(list, amount, chem$This())));
	}

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/Item;" +
							"appendHoverText(Lnet/minecraft/world/item/ItemStack;" +
							"Lnet/minecraft/world/item/Item$TooltipContext;" +
							"Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"),
			method = "getTooltipLines"
	)
	private void chemicals$AddChemicalTooltipForChemicalContaining(Item.TooltipContext context, Player player, TooltipFlag config, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
		if (!(getItem() instanceof ChemicalContaining item)) return;
		if (!(config.isCreative() || config.isAdvanced())) return;
		Chemicals.REGISTRY.forEach(handler -> {
			float amount = item.getChemicalContent(handler.getId(), chem$This());
			if (amount <= 0) return;
			handler.appendTooltip(list, amount, chem$This());
		});
	}

	@Inject(at = @At("HEAD"), method = "finishUsingItem")
	private void chemicals$AddChemicalsToPlayer(Level level, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
		if (!(user instanceof Player player) || level.isClientSide || !(getItem() instanceof ChemicalContaining item)) return;
		UseAnim anim = getItem().getUseAnimation(chem$This());
		if (!anim.equals(UseAnim.DRINK) && !anim.equals(UseAnim.EAT)) return;
		for (ConsumableChemicalHandler handler : Chemicals.REGISTRY) {
			handler.add(player, item.getChemicalContent(handler.getId(), chem$This()));
		}
	}

}
