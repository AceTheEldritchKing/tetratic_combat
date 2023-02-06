package smartin.tetraticcombat;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;


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
                    .define("rangeFallback", true);
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

    static //constructor
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
