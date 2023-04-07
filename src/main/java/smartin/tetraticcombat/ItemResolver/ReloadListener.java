package smartin.tetraticcombat.ItemResolver;

import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.util.Iterator;

public class ReloadListener extends SimplePreparableReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        String dataFolder = "configs";
        Iterator<ResourceLocation> iterator = resourceManager.listResources(dataFolder, (fileName) -> fileName.toString().endsWith(".json")).keySet().iterator();
        JSONFormat mergedConfig = new JSONFormat();
        mergedConfig.Version = 1.0d;

        resourceManager.listPacks().forEach(packResources -> {
            Iterator<ResourceLocation> iterator2 = resourceManager.listResources(dataFolder, (fileName) -> fileName.toString().endsWith(".json")).keySet().iterator();
            while(iterator2.hasNext()) {
                ResourceLocation identifier = iterator2.next();
                if(identifier.getNamespace().equals("tetratic")){
                    LOGGER.warn("Tetratic "+identifier.toString());
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
            }
        });

        /*
        while(iterator.hasNext()) {
            ResourceLocation identifier = iterator.next();
            if(identifier.getNamespace().equals("tetratic")){
                LOGGER.warn("Tetratic "+identifier.toString());
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
        }

         */
        LOGGER.info(mergedConfig.toString());
        return mergedConfig;
    }

    @Override
    protected void apply(Object mergedConfig, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Resolver.reload((JSONFormat)mergedConfig);
    }
}
