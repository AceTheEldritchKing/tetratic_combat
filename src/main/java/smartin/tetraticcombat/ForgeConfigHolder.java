package smartin.tetraticcombat;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ForgeConfigHolder {
    public static class Common
    {
        private static final boolean defaultBool1 = true;

        public final ForgeConfigSpec.ConfigValue<Boolean> EnableRescale;
        public final ForgeConfigSpec.ConfigValue<Boolean> EnableTetraRange;

        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("rendering");
            this.EnableRescale = builder.comment("Enable Tetratic Rescaling for Items in the players Hand.")
                    .define("enableRescale", defaultBool1);
            this.EnableTetraRange = builder.comment("Use Tetras AttackRange.")
                    .define("tetraRange", defaultBool1);
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
