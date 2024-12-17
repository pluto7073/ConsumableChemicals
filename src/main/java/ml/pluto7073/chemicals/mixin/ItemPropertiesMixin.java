package ml.pluto7073.chemicals.mixin;

import ml.pluto7073.chemicals.component.ChemicalMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Properties.class)
public class ItemPropertiesMixin {

	@Shadow
	@Nullable
	private DataComponentMap.Builder components;

	@SuppressWarnings("DiscouragedShift")
	@Inject(
			at = @At(value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/core/component/DataComponentMap$Builder;" +
						"addAll(Lnet/minecraft/core/component/DataComponentMap;)" +
						"Lnet/minecraft/core/component/DataComponentMap$Builder;",
					shift = At.Shift.AFTER),
			method = "component"
	)
	private <T> void chemicals$AddDefaultChemicalMap(DataComponentType<T> type, T value, CallbackInfoReturnable<Item.Properties> cir) {
		this.components.set(ChemicalMap.COMPONENT_TYPE, new ChemicalMap.Builder().build());
	}

}
