package smartin.tetraticcombat.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface IPacket {
    IPacket readPacketData(FriendlyByteBuf buf);

    void writePacketData(FriendlyByteBuf buf);

    void processPacket(NetworkEvent.Context ctx);
}
