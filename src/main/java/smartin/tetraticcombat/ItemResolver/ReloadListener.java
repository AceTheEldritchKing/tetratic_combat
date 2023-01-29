package smartin.tetraticcombat.ItemResolver;

import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class ReloadListener extends SimplePreparableReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        String dataFolder = "configs";
        Map<ResourceLocation,Resource> files = resourceManager.listResources(dataFolder, (fileName) -> fileName.toString().endsWith(".json"));
        JSONFormat mergedConfig = new JSONFormat();
        mergedConfig.Version = 1.0d;

        files.forEach((resourceLocation, resource) -> {
            if(resourceLocation.getNamespace().equals("tetratic")){
                try {
                    LOGGER.info("Loading "+resource.toString());
                    BufferedReader reader = resource.openAsReader();
                    try{
                        JSONFormat currentFile = new Gson().fromJson(reader, JSONFormat.class);
                        mergedConfig.merge(currentFile);
                    } catch (Exception jsonParsing){
                        LOGGER.warn("Exception loading tetratic Configuration from Datapack");
                        LOGGER.warn(jsonParsing.getMessage());
                        LOGGER.warn(resourceLocation);
                    }
                } catch (IOException ignored) {
                    LOGGER.info("Tetratic failure loading file "+resourceLocation.toString());
                }
            }
        });
        LOGGER.info(mergedConfig.toString());
        return mergedConfig;
    }

    @Override
    protected void apply(Object mergedConfig, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Resolver.reload((JSONFormat)mergedConfig);
    }
}
