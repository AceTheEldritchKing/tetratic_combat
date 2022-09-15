package smartin.tetraticcombat.network;


import smartin.tetraticcombat.ItemResolver.Resolver;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SSyncConfig implements IPacket {
    public String json;

    public SSyncConfig(String json) {
        this.json = json;
    }
    public SSyncConfig() {}

    @Override
    public SSyncConfig readPacketData(FriendlyByteBuf buf) {
        json = buf.readUtf();
        return this;
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) {
        buf.writeUtf(json, json.length());
    }

    @Override
    public void processPacket(NetworkEvent.Context ctx) {
        Resolver.readConfig(this.json);
    }
}
