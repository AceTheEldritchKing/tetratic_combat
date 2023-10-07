package smartin.tetraticcombat;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;


public class ForgeConfigHolder {
    private ForgeConfigHolder(){}
    public static class Common
    {
        public final ForgeConfigSpec.ConfigValue<Boolean> enableRescale;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableTetraRange;
        public final ForgeConfigSpec.ConfigValue<Boolean> reachFallBack;
        public final ForgeConfigSpec.ConfigValue<Boolean> quickReducesUpswing;
        public final ForgeConfigSpec.ConfigValue<Boolean> playerMixin;
        public final ForgeConfigSpec.ConfigValue<Boolean> altEvent;
        public final ForgeConfigSpec.ConfigValue<Boolean> verboseLogs;

        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("rendering");
            this.enableRescale = builder.comment("Enable Tetratic Rescaling for Items in the players Hand.")
                    .define("enableRescale", true);
            builder.pop();
            builder.push("technical");
            this.enableTetraRange = builder.comment("Use Tetras AttackRange.")
                    .define("tetraRange", true);
            this.reachFallBack = builder.comment("Fall back to Tetras Reach Attribute instead of Range - used for broken addons or old Tetra Versions.")
                    .define("rangeFallback", true);
            this.quickReducesUpswing = builder.comment("Uses Tetras Quick Stat to Scale Upswing.")
                    .define("quickIsUpswing", true);
            this.playerMixin = builder.comment("Set the stacks on item switching - This is for older Worlds or config Changes, if you do not have old Items in your world you can turn this off")
                    .define("fallBackApplyConfig", true);
            this.altEvent = builder.comment("Event Backup for Howling, Lunging and True sweep (might cause effects to be executed twice)")
                    .define("EmptyLeftClick", true);
            this.verboseLogs = builder.comment("Verbose logs, enable this if you are having trouble and want to see more details in the logs")
                    .define("verboseLogs", false);
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
