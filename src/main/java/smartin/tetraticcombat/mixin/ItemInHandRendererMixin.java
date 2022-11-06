package smartin.tetraticcombat.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import smartin.tetraticcombat.ForgeConfigHolder;

@Mixin({ItemInHandRenderer.class})
public class ItemInHandRendererMixin {

    @Inject(
            at = @At(value = "HEAD"),
            method = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            remap = true,
            require = 1
    )
    private void renderArmWithItem(LivingEntity p_109323_, ItemStack itemstack, ItemTransforms.TransformType p_109325_, boolean p_109326_, PoseStack poseStack, MultiBufferSource p_109328_, int p_109329_, CallbackInfo ci){
        if(!ForgeConfigHolder.COMMON.EnableRescale.get()) return;
        CompoundTag tag = itemstack.getTag();
        byte[] bytes = new byte[]{};

        if(tag==null) return;
        float xscale = tag.contains("tetraticScaleX") ? tag.getFloat("tetraticScaleX"):1.0f;
        float yscale = tag.contains("tetraticScaleY") ? tag.getFloat("tetraticScaleY"):1.0f;
        float zscale = tag.contains("tetraticScaleZ") ? tag.getFloat("tetraticScaleZ"):1.0f;
        double xTranslate = tag.contains("tetraticTranslateX") ? tag.getFloat("tetraticTranslateX"):0.0d;
        double yTranslate = tag.contains("tetraticTranslateY") ? tag.getFloat("tetraticTranslateY"):0.0d;
        double zTranslate = tag.contains("tetraticTranslateZ") ? tag.getFloat("tetraticTranslateZ"):0.0d;
        poseStack.scale(xscale,yscale,zscale);
        poseStack.translate(xTranslate,yTranslate,zTranslate);
    }
}
