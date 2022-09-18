package smartin.tetraticcombat.ItemResolver;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class JSONFormat {
    public double Version = 0;
    public Map<String, Map<String, Condition>> attributemap = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();
    public void Merge(JSONFormat toMerge){
        if(this.attributemap==null){
            this.attributemap = toMerge.attributemap;
            return;
        }
        if(toMerge.attributemap==null){
            return;
        }
        toMerge.attributemap.forEach((key,map) -> {
            if(this.attributemap.containsKey(key)){
                map.forEach( (innerKey,condition) ->{
                    attributemap.get(key).put(innerKey,condition);
                });
            }
            else {
                this.attributemap.put(key,map);
            }
        });
    }

    public String toString(){
        Gson gson = new Gson();
        String configString = gson.toJson(this);
        return configString;
    }
}
