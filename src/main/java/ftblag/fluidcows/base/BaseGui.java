package ftblag.fluidcows.base;

import ftblag.fluidcows.util.FCUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class BaseGui extends GuiContainer {

    protected final ResourceLocation gui;

    public BaseGui(Container c, String n, int xSize, int ySize) {
        super(c);
        gui = FCUtils.gLoc("gui_" + n);
        this.xSize = xSize;
        this.ySize = ySize;
    }

    @Override
    public void drawScreen(int mX, int mY, float f) {
        drawDefaultBackground();
        super.drawScreen(mX, mY, f);
        renderHoveredToolTip(mX, mY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(gui);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
