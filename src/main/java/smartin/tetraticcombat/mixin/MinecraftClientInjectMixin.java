package smartin.tetraticcombat.mixin;


import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, priority = 800)
public class MinecraftClientInjectMixin {

    @Inject(
            method = {"startAttack"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void pre_pre_doAttack(CallbackInfoReturnable<Boolean> info) {

    }
}
