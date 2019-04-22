package ftblag.fluidcows.network;

import ftblag.fluidcows.block.sorter.SorterContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAddInfo implements IMessage {

    private boolean swapBlackWhiteList = false;
    private String addNewFluid;
    private int removeFluidByIndex = -1;

    public PacketAddInfo() {
    }

    public PacketAddInfo(boolean swapBlackWhiteList) {
        this.swapBlackWhiteList = swapBlackWhiteList;
    }

    public PacketAddInfo(String addNewFluid) {
        this.addNewFluid = addNewFluid;
    }

    public PacketAddInfo(int removeFluidByIndex) {
        this.removeFluidByIndex = removeFluidByIndex;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(removeFluidByIndex);
        if (removeFluidByIndex == -1) {
            buf.writeBoolean(swapBlackWhiteList);
            if (!swapBlackWhiteList) {
                ByteBufUtils.writeUTF8String(buf, addNewFluid);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        removeFluidByIndex = buf.readInt();
        if (removeFluidByIndex == -1) {
            swapBlackWhiteList = buf.readBoolean();
            if (!swapBlackWhiteList) {
                addNewFluid = ByteBufUtils.readUTF8String(buf);
            }
        }
    }

    public static class Handler implements IMessageHandler<PacketAddInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketAddInfo p, MessageContext msg) {
            EntityPlayerMP player = msg.getServerHandler().player;

            if (player.openContainer instanceof SorterContainer) {
                ((SorterContainer) player.openContainer).go(p.addNewFluid, p.swapBlackWhiteList, p.removeFluidByIndex);
            }

            return null;
        }
    }
}
