package ftblag.fluidcows.block.sorter;

import ftblag.fluidcows.base.BaseGui;
import ftblag.fluidcows.entity.EntityFluidCow;
import ftblag.fluidcows.item.ItemCowDisplayer;
import ftblag.fluidcows.item.ItemCowHalter;
import ftblag.fluidcows.network.NetworkHandler;
import ftblag.fluidcows.network.PacketAddInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.io.IOException;
import java.util.Arrays;

public class SorterGui extends BaseGui {

    private SorterTileEntity te;

    public SorterGui(EntityPlayer p, IInventory c) {
        super(new SorterContainer(p, c), "sorter", 176, 138);
        te = ((SorterTileEntity) c);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        mouseX -= guiLeft;
        mouseY -= guiTop;

        for (int i = 0; i < te.filter.size(); i++) {
            if (mouseX >= 45 && mouseY >= 6 + i * 10 && mouseX < 50 && mouseY < 11 + i * 10) {
                NetworkHandler.network.sendToServer(new PacketAddInfo(i + 1));
            }
        }

        if (mouseX >= 7 && mouseY >= 22 && mouseX < 20 && mouseY < 35) {
            ItemStack stack = mc.player.inventory.getItemStack();
            boolean cap = stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            boolean displ = stack.getItem() instanceof ItemCowDisplayer;
            if (!stack.isEmpty() && (cap || displ || stack.getItem() instanceof ItemCowHalter)) {
                if (cap) {
                    FluidStack fStack = FluidUtil.getFluidContained(stack);
                    if (fStack != null && fStack.getFluid() != null && fStack.getFluid().getName() != null) {
                        NetworkHandler.network.sendToServer(new PacketAddInfo(fStack.getFluid().getName()));
                    }
                    return;
                }
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null) {
                    String fName = tag.getString(displ ? "fluid" : EntityFluidCow.TYPE_FLUID);
                    if (!fName.isEmpty()) {
                        NetworkHandler.network.sendToServer(new PacketAddInfo(fName));
                    }
                }
            }
        } else if (mouseX >= 159 && mouseY >= 20 && mouseX < 169 && mouseY < 30) {
            NetworkHandler.network.sendToServer(new PacketAddInfo(true));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        int left = guiLeft + 44;
        int top = guiTop + 6;

        for (int i = 0; i < te.filter.size(); i++) {
            this.drawTexturedModalRect(left + 1, top, 176, 0, 5, 5);
            top += 10;
        }

        left = guiLeft + 44;
        top = guiTop + 6;
        for (String fluid : te.filter) {
            mc.fontRenderer.drawString(fluid, left + 8, top - 2, 0xF);
            top += 10;
        }

        drawRect(guiLeft + 160, guiTop + 21, guiLeft + 168, guiTop + 29, te.isBlackList ? 0xFF000000 : 0xFFFFFFFF);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);

        int left = mouseX - guiLeft;
        int top = mouseY - guiTop;
        int i = 0;

        for (String fluid : te.filter) {
            if (left >= 45 && top >= 6 + i * 10 && left < 50 && top < 11 + i * 10) {

                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(guiLeft + 45, guiTop + 6 + i * 10, guiLeft + 50, guiTop + 11 + i * 10, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();

                drawHoveringText(I18n.translateToLocalFormatted("gui.fluidcows.sorter.remove", fluid), mouseX, mouseY);
                break;
            }
            i++;
        }

        if (left >= 7 && top >= 22 && left < 20 && top < 35) {
            drawHoveringText(Arrays.asList(I18n.translateToLocal("gui.fluidcows.sorter.insert").split(" NEXT ")), mouseX, mouseY);
        } else if (left >= 159 && top >= 20 && left < 169 && top < 30) {
            drawHoveringText(I18n.translateToLocalFormatted("gui.fluidcows.sorter.list", te.isBlackList ? "white" : "black"), mouseX, mouseY);
        }
    }
}
