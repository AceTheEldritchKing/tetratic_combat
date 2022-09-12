package smartin.tetraticcombat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.effect.LungeEffect;
import se.mickelus.tetra.effect.SweepingEffect;
import se.mickelus.tetra.effect.howling.HowlingEffect;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

@Mod.EventBusSubscriber(
        modid = "tetratic",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class EventHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onClickInput(InputEvent.ClickInputEvent event) {
        System.out.println("CLICKINPUT");
        Minecraft mc = Minecraft.getInstance();
        ItemStack itemStack = mc.player.getMainHandItem();
        if (event.isAttack() && itemStack.getItem() instanceof ItemModularHandheld && mc.hitResult != null && HitResult.Type.MISS.equals(mc.hitResult.getType())) {
            if (getEffectLevel(itemStack, ItemEffect.truesweep) > 0) {
                SweepingEffect.triggerTruesweep();
            }

            if (getEffectLevel(itemStack, ItemEffect.howling) > 0) {
                HowlingEffect.sendPacket();
            }
        }

        if (event.isUseItem()) {
            LungeEffect.onRightClick(mc.player);
        }

    }
    private static int getEffectLevel(ItemStack itemStack, ItemEffect effect) {
        return EffectHelper.getEffectLevel(itemStack, effect);
    }
}