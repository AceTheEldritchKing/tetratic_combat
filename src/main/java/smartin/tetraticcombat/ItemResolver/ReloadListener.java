package smartin.tetraticcombat.ItemResolver;

import com.google.gson.Gson;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ReloadListener extends net.minecraft.client.resources.ReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    protected Object prepare(IResourceManager resourceManager, IProfiler profilerFiller) {
        String dataFolder = "configs";
        Iterator<ResourceLocation> iterator = resourceManager.listResources(dataFolder, (fileName) -> fileName.endsWith(".json")).iterator();
        JSONFormat mergedConfig = new JSONFormat();
        mergedConfig.Version = 1.0d;

        while(iterator.hasNext()) {
            ResourceLocation identifier = iterator.next();
            if(identifier.getNamespace().equals("tetratic")){
                try {
                    IResource resource = resourceManager.getResource(identifier);
                    LOGGER.info("Loading "+resource.getLocation());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                    try{
                        JSONFormat currentFile = new Gson().fromJson(reader, JSONFormat.class);
                        mergedConfig.merge(currentFile);
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
    protected void apply(Object mergedConfig, IResourceManager resourceManager, IProfiler profilerFiller) {
        Resolver.reload((JSONFormat)mergedConfig);
    }
}
