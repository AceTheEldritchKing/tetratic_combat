package smartin.tetraticcombat.ItemResolver;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.properties.AttributeHelper;
import smartin.tetraticcombat.ForgeConfigHolder;
import smartin.tetraticcombat.network.SSyncConfig;

import java.util.Map;


public class Resolver {
    private static final Logger LOGGER = LogManager.getLogger();
    public static JSONFormat weaponConfig;

    public static void reload(JSONFormat config) {
        weaponConfig = config;
        configFileToSSyncConfig();
    }


    public static SSyncConfig configFileToSSyncConfig() {
        Gson gson = new Gson();
        String configString = gson.toJson(weaponConfig);
        return new SSyncConfig(configString);
    }

    public static void readConfig(String config) {
        weaponConfig = new Gson().fromJson(config, JSONFormat.class);
    }

    public static ExpandedContainer findWeaponByNBT(ItemStack stack) {
        if(weaponConfig==null) return null;
        if(stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            for (String key : tag.getAllKeys()) {
                if(weaponConfig.attributemap.containsKey(key)){
                    Map<String, Condition> map1 =  weaponConfig.attributemap.get(key);
                    if(map1.containsKey(tag.getString(key))){
                        return map1.get(tag.getString(key)).resolve(stack);
                    }
                }
            }
        }
        return null;
    }
    public static ItemStack generateBetterCombatNBT(ItemStack itemStack){
        return generateBetterCombatNBT(itemStack,false);
    }

    public static ItemStack generateBetterCombatNBT(ItemStack itemStack,boolean force){
        if(force && itemStack.hasTag()&&itemStack.getTag().contains("weapon_attributes")){
            itemStack.removeTagKey("weapon_attributes");
        }
        ExpandedContainer container = Resolver.findWeaponByNBT(itemStack);
        if(container!=null){
            try{
                double range = getAttackRange(itemStack);
                WeaponAttributes attributes = WeaponRegistry.resolveAttributes(new ResourceLocation("tetratic:generated"),container.attributes);
                if(ForgeConfigHolder.COMMON.EnableTetraRange.get()){
                    attributes =  new WeaponAttributes(range,attributes.pose(),attributes.offHandPose(),attributes.isTwoHanded(),attributes.category(),attributes.attacks());
                }
                RescaleUpswing(attributes,getQuickStat(itemStack));
                WeaponAttributesHelper.validate(attributes);
                AttributesContainer attributesContainer =  new AttributesContainer(container.attributes.parent(),attributes);
                WeaponAttributesHelper.writeToNBT(itemStack,attributesContainer);
                applyScale(itemStack,container.scaleX,container.scaleY,container.scaleZ);
                applyTranslation(itemStack,container.translationX,container.translationY,container.translationZ);
                return itemStack;
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return itemStack;
    }

    private static void applyScale(ItemStack stack,float x,float y,float z){
        CompoundTag nbt = stack.getTag();
        if(x!=1.0f)
            nbt.putFloat("tetraticScaleX",x);
        else if(nbt.contains("tetraticScaleX"))
            nbt.remove("tetraticScaleX");
        if(y!=1.0f)
            nbt.putFloat("tetraticScaleY",y);
        else if(nbt.contains("tetraticScaleY"))
            nbt.remove("tetraticScaleY");
        if(z!=1.0f)
            nbt.putFloat("tetraticScaleZ",z);
        else if(nbt.contains("tetraticScaleZ"))
            nbt.remove("tetraticScaleZ");
        stack.setTag(nbt);
    }
    private static void applyTranslation(ItemStack stack,double x,double y,double z){
        CompoundTag nbt = stack.getTag();
        if(x!=0.0f)
            nbt.putDouble("tetraticTranslateX",x);
        else if(nbt.contains("tetraticTranslateX"))
            nbt.remove("tetraticTranslateX");
        if(y!=0.0f)
            nbt.putDouble("tetraticTranslateY",y);
        else if(nbt.contains("tetraticTranslateY"))
            nbt.remove("tetraticTranslateY");
        if(z!=0.0f)
            nbt.putDouble("tetraticTranslateZ",z);
        else if(nbt.contains("tetraticTranslateZ"))
            nbt.remove("tetraticTranslateZ");
        stack.setTag(nbt);
    }

    private static double getQuickStat(ItemStack stack){
        if(stack.getItem() instanceof ModularItem item){
            var map = item.getEffectData(stack).levelMap;
            if(map.containsKey(ItemEffect.quickStrike)){
                float level = map.get(ItemEffect.quickStrike);
                return level*0.05d+0.2d;
            }
        }
        return 0.0d;
    }

    private static void RescaleUpswing(WeaponAttributes weaponAttributes, double scale){
        if(!ForgeConfigHolder.COMMON.QuickReducesUpswing.get()) return;
        WeaponAttributes.Attack[] attacks = weaponAttributes.attacks();
        for(int i = 0;i<attacks.length;i++) {
            double newUpswing = Math.max(0,attacks[i].upswing()-attacks[i].upswing()*scale);
            attacks[i] = new WeaponAttributes.Attack(
                    attacks[i].conditions(),
                    attacks[i].hitbox(),
                    attacks[i].damageMultiplier(),
                    attacks[i].angle(),
                    newUpswing,
                    attacks[i].animation(),
                    attacks[i].swingSound(),
                    attacks[i].impactSound()
            );
        }
    }

    private static double getAttackRange(ItemStack itemStack){
        if(itemStack.getItem() instanceof ModularItem item){
            //TetraItem, use fallback to Reach
            System.out.println(item.getAttributeValue(itemStack, ForgeMod.ATTACK_RANGE.get()));
            if(item.getAttributeValue(itemStack, ForgeMod.ATTACK_RANGE.get())!=0){
                return 3.0d + item.getAttributeValue(itemStack, ForgeMod.ATTACK_RANGE.get());
            }
            else{
                if(ForgeConfigHolder.COMMON.ReachFallBack.get()){
                    return 3.0d + item.getAttributeValue(itemStack, ForgeMod.REACH_DISTANCE.get());
                }
                else{
                    return 3.0d;
                }
            }
        }
        else{
            //not a tetra Item, technically this code is not needed
            try{
                Multimap<Attribute, AttributeModifier> attributeMap = itemStack.getAttributeModifiers(itemStack.getEquipmentSlot());
                return AttributeHelper.getMergedAmount(attributeMap.get(ForgeMod.ATTACK_RANGE.get()),3.0d);
            }catch (Exception e){
                return 3.0d;
            }
        }
    }
}
