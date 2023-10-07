package smartin.tetraticcombat.ItemResolver;

import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smartin.tetraticcombat.ForgeConfigHolder;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ReloadListener extends SimplePreparableReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        String dataFolder = "configs";
        Iterator<ResourceLocation> iterator = resourceManager.listResources(dataFolder, (fileName) -> fileName.toString().endsWith(".json")).keySet().iterator();
        JSONFormat mergedConfig = new JSONFormat();
        mergedConfig.Version = 1.0d;

        resourceManager.listPacks().filter(packResources -> packResources.getNamespaces(PackType.SERVER_DATA).contains("tetratic")).forEach(packResources -> {
            //packResources.getResources()

            Collection<ResourceLocation> packResourcesResources = packResources.getResources(PackType.SERVER_DATA, "tetratic", dataFolder, (fileName) -> fileName.toString().endsWith(".json"));
            if (packResourcesResources.size() > 0) {
                if(ForgeConfigHolder.COMMON.verboseLogs.get()){
                    LOGGER.info("Tetratic Loading Config from " + packResources.getName());
                }
                packResourcesResources.forEach(identifier -> {
                    if (identifier.getNamespace().equals("tetratic")) {
                        resourceManager.getResource(identifier).ifPresent(resource -> {
                            try {
                                BufferedReader reader = resource.openAsReader();
                                JSONFormat currentFile = new Gson().fromJson(reader, JSONFormat.class);
                                mergedConfig.merge(currentFile);
                            } catch (Exception JsonParsing) {
                                LOGGER.warn("Exception loading tetratic Configuration from Datapack");
                                LOGGER.warn(JsonParsing.getMessage());
                                LOGGER.warn(identifier.toString());
                            }
                        });
                    }
                });
            }
        });
        if(ForgeConfigHolder.COMMON.verboseLogs.get()){
            LOGGER.info(mergedConfig.toString());
        }
        return mergedConfig;
    }

    @Override
    protected void apply(Object mergedConfig, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Resolver.reload((JSONFormat) mergedConfig);
    }
}
