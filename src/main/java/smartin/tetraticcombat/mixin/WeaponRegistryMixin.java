package smartin.tetraticcombat.mixin;

import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import smartin.tetraticcombat.Config;

import java.util.Map;


@Mixin(value = WeaponRegistry.class, remap = false)
public abstract class WeaponRegistryMixin {

    @Inject(
            at = @At(value = "HEAD"),
            method = "loadAttributes",
            cancellable = true,
            remap = false
    )
    private static void loadAttributes(ResourceManager resourceManager, CallbackInfo ci){
        ci.cancel();
        WeaponRegistryInterface.loadContainers(resourceManager);
        Map<ResourceLocation, AttributesContainer> containers = WeaponRegistryInterface.containers();
        containers.forEach((itemId, container) -> {
            WeaponRegistry.resolveAndRegisterAttributes(itemId, container);
        });
    }

    @Inject(
            at = @At(value = "HEAD"),
            method = "Lnet/bettercombat/logic/WeaponRegistry;getAttributes(Lnet/minecraft/world/item/ItemStack;)Lnet/bettercombat/api/WeaponAttributes;",
            cancellable = true,
            remap = false
    )
    private static void getAttributes(ItemStack itemStack, CallbackInfoReturnable<WeaponAttributes> cir){
        if(itemStack!=null && itemStack.hasTag() && !itemStack.getTag().contains("weapon_attributes")){
            Config.generateBetterCombatNBT(itemStack);
        }
    }
}
