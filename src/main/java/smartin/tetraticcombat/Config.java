package smartin.tetraticcombat;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import smartin.tetraticcombat.network.SSyncConfig;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
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
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Config {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Type attributesContainerFileFormat = (new TypeToken<AttributesContainer>() {
    }).getType();


    public static File jsonFile;
    public static Map<String, Map<String, AttributesContainer>> JSON_MAP = new HashMap<>();


    public static void init(Path folder) {
        jsonFile = new File(FileUtils.getOrCreateDirectory(folder, "serverconfig").toFile(), "bettercombatnbt.json");
        try {
            if (jsonFile.createNewFile()) {
                Path defaultConfigPath = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve("bettercombatnbt.json");
                InputStreamReader defaults = new InputStreamReader(Files.exists(defaultConfigPath)? Files.newInputStream(defaultConfigPath) :
                        Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/fightnbtintegration/bettercombatnbt.json")));
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
        JSON_MAP = new Gson().fromJson(config, new TypeToken<Map<String, Map<String, AttributesContainer>>>(){}.getType());
    }

    public static void readConfig(File path) {
        try (Reader reader = new FileReader(path)) {
            JSON_MAP = new Gson().fromJson(reader, new TypeToken<Map<String, Map<String, AttributesContainer>>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            JSON_MAP = new HashMap<>();
        }
    }

    public static AttributesContainer findWeaponByNBT(ItemStack stack) {
        if(JSON_MAP==null){
            System.out.println("NULL");
            return null;
        }
        if(stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            for (String key : tag.getAllKeys()) {
                if(JSON_MAP.containsKey(key)){
                    Map<String, AttributesContainer> map1 =  JSON_MAP.get(key);
                    if(map1.containsKey(tag.getString(key))){
                        return map1.get(tag.getString(key));
                    }
                }
            }
        }
        return null;
    }

    public static ItemStack generateBetterCombatNBT(ItemStack itemStack){
        AttributesContainer container = Config.findWeaponByNBT(itemStack);
        if(container!=null){
            try{
                double range;
                WeaponAttributes attributes = WeaponRegistry.resolveAttributes(new ResourceLocation("tetratic:generated"),container);
                if(itemStack.getItem() instanceof ModularItem){
                    ModularItem item = (ModularItem) itemStack.getItem();
                    if(item.getAttributeValue(itemStack, ForgeMod.ATTACK_RANGE.get())==0){
                        range = 3.0d + item.getAttributeValue(itemStack, ForgeMod.REACH_DISTANCE.get());
                    }
                    else{
                        range = 3.0d + item.getAttributeValue(itemStack, ForgeMod.ATTACK_RANGE.get());
                    }
                }
                else{
                    Multimap<Attribute, AttributeModifier> attributeMap = itemStack.getAttributeModifiers(itemStack.getEquipmentSlot());
                    range = AttributeHelper.getMergedAmount(attributeMap.get(ForgeMod.ATTACK_RANGE.get()),3.0d);
                }
                if(attributes!=null && range!=attributes.attackRange()){
                    System.out.println("new WeaponAttributes"+range);
                    attributes =  new WeaponAttributes(range,attributes.pose(),attributes.offHandPose(),false,attributes.category(),attributes.attacks());
                }

                container =  new AttributesContainer(container.parent(),attributes);
                WeaponAttributesHelper.validate(attributes);
                WeaponAttributesHelper.writeToNBT(itemStack,container);
                return itemStack;
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return itemStack;
    }
}
