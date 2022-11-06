package smartin.tetraticcombat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ForgeConfigHolder {
    public static class Common
    {
        public final ForgeConfigSpec.ConfigValue<Boolean> EnableRescale;
        public final ForgeConfigSpec.ConfigValue<Boolean> EnableTetraRange;
        public final ForgeConfigSpec.ConfigValue<Boolean> ReachFallBack;
        public final ForgeConfigSpec.ConfigValue<Boolean> QuickReducesUpswing;
        public final ForgeConfigSpec.ConfigValue<Boolean> PlayerMixin;
        public final ForgeConfigSpec.ConfigValue<Boolean> EventOne;

        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("rendering");
            this.EnableRescale = builder.comment("Enable Tetratic Rescaling for Items in the players Hand.")
                    .define("enableRescale", true);
            builder.pop();
            builder.push("technical");
            this.EnableTetraRange = builder.comment("Use Tetras AttackRange.")
                    .define("tetraRange", true);
            this.ReachFallBack = builder.comment("Fall back to Tetras Reach Attribute instead of Range - used for brocken addons or old Tetra Versions.")
                    .define("rangeFallback", TetraSupportsRange());
            this.QuickReducesUpswing = builder.comment("Uses Tetras Quick Stat to Scale Upswing.")
                    .define("quickIsUpsing", true);
            this.PlayerMixin = builder.comment("Set the stacks on Itemswitching - This is for older Worlds or config Changes, if you do not have old Items in your world you can turn this off")
                    .define("fallBackApplyConfig", true);
            this.EventOne = builder.comment("Event Backup for Howling, Lunging and True sweep (might cause effects to be executed twice)")
                    .define("EmptyLeftClick", true);
            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static boolean TetraSupportsRange(){
        List<IModFileInfo> modFileInfos = ModList.get().getModFiles();
        for(int i = 0;i<modFileInfos.size();i++){
            List<IModInfo> modInfos = modFileInfos.get(i).getMods();
            for(int j = 0;j<modInfos.size();j++){
                return(versionAbove(modFileInfos.get(i).versionString(),"4.9.10"));
            }
        }
        return false;
    }

    public static boolean versionAbove(String versionString, String compareString){
        String[] version = versionString.split("\\.");
        String[] compare = compareString.split("\\.");
        int length = Math.min(version.length,compare.length);
        for(int i = 0;i<length;i++){
            if(Integer.parseInt(version[i])>Integer.parseInt(compare[i])) return false;
            if(Integer.parseInt(version[i])<Integer.parseInt(compare[i])) return true;
        }
        return false;
    }

    static //constructor
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
