package smartin.tetraticcombat.mixin;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import smartin.tetraticcombat.ForgeConfigHolder;

@Mixin({HeldItemLayer.class})
public class ItemInHandRendererMixin {

    @Inject(
            at = @At(value = "HEAD"),
            method = "Lnet/minecraft/client/renderer/entity/layers/HeldItemLayer;renderArmWithItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;Lnet/minecraft/util/HandSide;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            remap = true,
            require = 1
    )
    private void renderArmWithItem(LivingEntity p_229135_1_, ItemStack itemstack, ItemCameraTransforms.TransformType p_229135_3_, HandSide p_229135_4_, MatrixStack poseStack, IRenderTypeBuffer p_229135_6_, int p_229135_7_, CallbackInfo ci){
        if(!ForgeConfigHolder.COMMON.enableRescale.get()) return;
        CompoundNBT tag = itemstack.getTag();


        if(tag==null) return;
        float xScale = tag.contains("tetraticScaleX") ? tag.getFloat("tetraticScaleX"):1.0f;
        float yScale = tag.contains("tetraticScaleY") ? tag.getFloat("tetraticScaleY"):1.0f;
        float zScale = tag.contains("tetraticScaleZ") ? tag.getFloat("tetraticScaleZ"):1.0f;
        double xTranslate = tag.contains("tetraticTranslateX") ? tag.getFloat("tetraticTranslateX"):0.0d;
        double yTranslate = tag.contains("tetraticTranslateY") ? tag.getFloat("tetraticTranslateY"):0.0d;
        double zTranslate = tag.contains("tetraticTranslateZ") ? tag.getFloat("tetraticTranslateZ"):0.0d;
        poseStack.scale(xScale,yScale,zScale);
        poseStack.translate(xTranslate,yTranslate,zTranslate);
    }
}
