package smartin.tetraticcombat.mixin;


import net.bettercombat.api.AttributesContainer;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(value = WeaponRegistry.class, remap = false)
public interface WeaponRegistryInterface {

    @Accessor("containers")
    static Map<ResourceLocation, AttributesContainer> containers() {
        throw new AssertionError();
    }

    @Invoker("loadContainers")
    static void loadContainers(ResourceManager resourceManager) {
        throw new AssertionError();
    }

}