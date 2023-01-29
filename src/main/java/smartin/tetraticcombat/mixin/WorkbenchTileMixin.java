package smartin.tetraticcombat.mixin;


import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import smartin.tetraticcombat.ItemResolver.Resolver;

@Mixin(WorkbenchTile.class)
public abstract class WorkbenchTileMixin {
    @Shadow public abstract ItemStack getTargetItemStack();

    @Inject(
            method = "Lse/mickelus/tetra/blocks/workbench/WorkbenchTile;craft(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("RETURN"),
            remap = false
    )
    private void modifyResult(Player severity, CallbackInfo ci){
        Resolver.resetBetterCombatNBT(this.getTargetItemStack());
        Resolver.generateBetterCombatNBT(this.getTargetItemStack(),true);
    }

}