package smartin.tetraticcombat;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.effect.SweepingEffect;
import se.mickelus.tetra.effect.howling.HowlingEffect;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

@Mod.EventBusSubscriber(
        modid = "tetratic",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ClientEventHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST )
    public static void onEmptyLeftClick(PlayerInteractEvent.LeftClickEmpty event){
        if(!ForgeConfigHolder.COMMON.altEvent.get()) return;
        Minecraft mc = Minecraft.getInstance();
        assert mc.player != null;
        ItemStack itemStack = mc.player.getMainHandItem();
        if(event.isCanceled()) return;
        if (itemStack.getItem() instanceof ItemModularHandheld && mc.hitResult != null && RayTraceResult.Type.MISS.equals(mc.hitResult.getType())) {
            if (getEffectLevel(itemStack, ItemEffect.truesweep) > 0) {
                SweepingEffect.triggerTruesweep();
            }

            if (getEffectLevel(itemStack, ItemEffect.howling) > 0) {
                HowlingEffect.sendPacket();
            }
        }
    }

    private static int getEffectLevel(ItemStack itemStack, ItemEffect effect) {
        return EffectHelper.getEffectLevel(itemStack, effect);
    }
}