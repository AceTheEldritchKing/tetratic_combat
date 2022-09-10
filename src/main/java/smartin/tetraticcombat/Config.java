package smartin.tetraticcombat;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.properties.AttributeHelper;
import smartin.tetraticcombat.network.SSyncConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Config {
    private static final Logger LOGGER = LogManager.getLogger();
    public static File jsonFile;
    public static String fileName = "bettercombatnbt.json";
    public static Map<String, Map<String, Condition>> JSON_MAP = new HashMap<>();


    public static void init(Path folder) {
        jsonFile = new File(FileUtils.getOrCreateDirectory(folder, "serverconfig").toFile(), fileName);
        try {
            if (jsonFile.createNewFile()) {
                System.out.println("No Config found");
                Path defaultConfigPath = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve(fileName);
                InputStreamReader defaults = new InputStreamReader(Files.exists(defaultConfigPath)? Files.newInputStream(defaultConfigPath) :
                        Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/fightnbtintegration/"+fileName)));
                FileOutputStream writer = new FileOutputStream(jsonFile, false);
                int read;
                while ((read = defaults.read()) != -1) {
                    writer.write(read);
                }
                writer.close();
                defaults.close();
            }
        } catch (IOException error) {
            LOGGER.warn(error.getMessage());
        }

        readConfig(jsonFile);
    }

    public static SSyncConfig configFileToSSyncConfig() {
        try {
            return new SSyncConfig(new String(Files.readAllBytes(jsonFile.toPath())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void readConfig(String config) {
        JSON_MAP = new Gson().fromJson(config, new TypeToken<Map<String, Map<String, Condition>>>(){}.getType());
    }

    public static void readConfig(File path) {
        try (Reader reader = new FileReader(path)) {
            JSON_MAP = new Gson().fromJson(reader, new TypeToken<Map<String, Map<String, Condition>>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            JSON_MAP = new HashMap<>();
        }
    }

    public static ExpandedContainer findWeaponByNBT(ItemStack stack) {
        if(stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            for (String key : tag.getAllKeys()) {
                if(JSON_MAP.containsKey(key)){
                    Map<String, Condition> map1 =  JSON_MAP.get(key);
                    if(map1.containsKey(tag.getString(key))){
                        System.out.println(map1.get(tag.getString(key)));
                        return map1.get(tag.getString(key)).resolve(stack);
                    }
                }
            }
        }
        return null;
    }

    public static ItemStack generateBetterCombatNBT(ItemStack itemStack){
        ExpandedContainer container = Config.findWeaponByNBT(itemStack);
        if(container!=null){
            try{
                double range = getAttackRange(itemStack);
                WeaponAttributes attributes = WeaponRegistry.resolveAttributes(new ResourceLocation("tetratic:generated"),container.attributes);
                if(ForgeConfigHolder.COMMON.EnableTetraRange.get()){
                    attributes =  new WeaponAttributes(range,attributes.pose(),attributes.offHandPose(),attributes.isTwoHanded(),attributes.category(),attributes.attacks());
                }
                WeaponAttributesHelper.validate(attributes);
                AttributesContainer attributesContainer =  new AttributesContainer(container.attributes.parent(),attributes);
                WeaponAttributesHelper.writeToNBT(itemStack,attributesContainer);
                CompoundTag nbt = itemStack.getTag();
                if(container.scaleX!=1.0f)
                    nbt.putFloat("tetraticScaleX",container.scaleX);
                else if(nbt.contains("tetraticScaleX"))
                    nbt.remove("tetraticScaleX");
                if(container.scaleY!=1.0f)
                    nbt.putFloat("tetraticScaleY",container.scaleY);
                else if(nbt.contains("tetraticScaleY"))
                    nbt.remove("tetraticScaleY");
                if(container.scaleZ!=1.0f)
                    nbt.putFloat("tetraticScaleZ",container.scaleZ);
                else if(nbt.contains("tetraticScaleZ"))
                    nbt.remove("tetraticScaleZ");
                itemStack.setTag(nbt);
                return itemStack;
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return itemStack;
    }

    private static double getAttackRange(ItemStack itemStack){
        double range = 3.0d;
        if(itemStack.getItem() instanceof ModularItem item){
            //TetraItem, use fallback to Reach
            if(item.getAttributeModifiers(itemStack.getEquipmentSlot(),itemStack).containsKey(ForgeMod.ATTACK_RANGE)){
                System.out.println("RANGE");
                range = 3.0d + item.getAttributeValue(itemStack, ForgeMod.ATTACK_RANGE.get());
            }
            else{
                System.out.println("REACH");
                range = 3.0d + item.getAttributeValue(itemStack, ForgeMod.REACH_DISTANCE.get());
            }
        }
        else{
            //not a tetra Item, technically this code is not needed
            Multimap<Attribute, AttributeModifier> attributeMap = itemStack.getAttributeModifiers(itemStack.getEquipmentSlot());
            range = AttributeHelper.getMergedAmount(attributeMap.get(ForgeMod.ATTACK_RANGE.get()),3.0d);
        }
        return range;
    }
}
