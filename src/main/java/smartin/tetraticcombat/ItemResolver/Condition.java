package smartin.tetraticcombat.ItemResolver;

import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.ItemModuleMajor;
import se.mickelus.tetra.module.data.ImprovementData;

import javax.annotation.Nullable;

public class Condition {
    @Nullable
    public String type;
    public ExpandedContainer config;
    @Nullable
    public String key;
    @Nullable
    public String value;
    @Nullable
    public String improvement;
    @Nullable
    public Condition[] conditions;

    public ExpandedContainer resolve(ItemStack itemStack){
        if(type==null){
            return getChildifPossible(itemStack);
        }
        switch (type){
            case "module":
                assert itemStack.getTag() != null;
                assert key != null;
                if(itemStack.getTag().getString(key).equals(value)){
                    return getChildifPossible(itemStack);
                }
                break;
            case "improvement":
                if(itemStack.getItem() instanceof ModularItem item){
                    ItemModuleMajor[] modules = item.getMajorModules(itemStack);
                    if(modules==null) return null;
                    for (ItemModuleMajor module : modules) {
                        if (module!=null && (module.getSlot().equals(key) || key==null) && (module.getKey().equals(value) || value == null)) {
                            ImprovementData[] improvements = module.getImprovements(itemStack);
                            for (int h = 0; h < improvements.length; h++) {
                                if (improvements[h].key.equals(improvement)) {
                                    return getChildifPossible(itemStack);
                                }
                            }
                        }
                    }

                }
                break;
            default:
                return config;
        }
        return null;
    }

    private ExpandedContainer getChildifPossible(ItemStack itemStack){
        ExpandedContainer container;
        if(conditions!=null){
            for (Condition condition : conditions) {
                container = condition.resolve(itemStack);
                if (container != null)
                    return container;
            }
        }
        return config;
    }
}
