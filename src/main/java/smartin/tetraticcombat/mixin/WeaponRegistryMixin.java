package smartin.tetraticcombat.mixin;

import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import smartin.tetraticcombat.Config;


@Mixin(value = WeaponRegistry.class, remap = false)
public abstract class WeaponRegistryMixin {

    @Inject(
            at = @At(value = "HEAD"),
            method = "Lnet/bettercombat/logic/WeaponRegistry;getAttributes(Lnet/minecraft/world/item/ItemStack;)Lnet/bettercombat/api/WeaponAttributes;",
            cancellable = true,
            remap = false,
            require = -1
    )
    private static void getAttributes(ItemStack itemStack, CallbackInfoReturnable<WeaponAttributes> cir){
        /**
         * This Mixin is by no means required.
         * Its only here to allow old items without the Tag to be loaded Correctly
         * It checks if an Item does not have the Tag set and if its supposed to have a Tag
         * If both are true it generates a new Tag
         * There is no harm if this mixins fails, nor will the game crash
         * This should be moved to a Forge event in the future and only react on a change of Item in Hand
         */
        if(itemStack!=null && itemStack.hasTag() && !itemStack.getTag().contains("weapon_attributes")){
            Config.generateBetterCombatNBT(itemStack);
        }
    }
}
