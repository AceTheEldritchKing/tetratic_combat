package smartin.tetraticcombat.mixin;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import smartin.tetraticcombat.ForgeConfigHolder;
import smartin.tetraticcombat.ItemResolver.Resolver;

import java.util.HashMap;
import java.util.Map;

@Mixin({PlayerEntity.class})
public class PlayerMixin {
    private static final Map<PlayerEntity, ItemStack> playerItemStackMap= new HashMap();
    @Inject(
            at = @At(value = "HEAD"),
            method = "Lnet/minecraft/entity/player/PlayerEntity;tick()V",
            remap = true,
            require = 1
    )
    private void tick(CallbackInfo ci){
        if(!ForgeConfigHolder.COMMON.playerMixin.get()) return;
        PlayerEntity p = (PlayerEntity)(Object) this;
        ItemStack handStack = playerItemStackMap.get(p);
        if(handStack==null || (p.getMainHandItem() != null && !p.getMainHandItem().equals(handStack,false))){
            Resolver.generateBetterCombatNBT(p.getMainHandItem(),false);
            playerItemStackMap.put(p,p.getMainHandItem());
        }
    }
}
