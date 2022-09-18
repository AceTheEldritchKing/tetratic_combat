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
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ReloadListener extends SimplePreparableReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    protected Object prepare(ResourceManager resourceManager, ProfilerFiller p_10797_) {
        String dataFolder = "configs";
        LOGGER.info("LOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \nLOADING CONFIGS \n");
        Iterator<ResourceLocation> iterator = resourceManager.listResources(dataFolder, (fileName) -> fileName.endsWith(".json")).iterator();
        LOGGER.info(iterator.hasNext());
        JSONFormat mergedConfig = new JSONFormat();
        mergedConfig.Version = 1.0d;

        while(iterator.hasNext()) {
            ResourceLocation identifier = iterator.next();
            if(identifier.getNamespace().equals("tetratic")){
                try {
                    Resource resource = resourceManager.getResource(identifier);
                    LOGGER.info("Loading "+resource.getLocation());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                    try{
                        JSONFormat currentFile = new Gson().fromJson(reader, JSONFormat.class);
                        mergedConfig.Merge(currentFile);
                    } catch (Exception JsonParsing){
                        LOGGER.warn("Exception loading tetratic Configuration from Datapack");
                        LOGGER.warn(JsonParsing.getMessage());
                        LOGGER.warn(resource.getLocation());
                    }
                } catch (IOException ignored) {}
            }
        }
        LOGGER.info(mergedConfig.toString());
        return mergedConfig;
    }

    @Override
    protected void apply(Object mergedConfig, ResourceManager resourceManager, ProfilerFiller p_10795_) {
        Resolver.reload((JSONFormat)mergedConfig);
    }
}
