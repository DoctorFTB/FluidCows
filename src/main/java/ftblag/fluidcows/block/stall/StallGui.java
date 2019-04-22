package ftblag.fluidcows.block.stall;

import ftblag.fluidcows.base.BaseGui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class StallGui extends BaseGui {

    private StallTileEntity te;
    public FluidTank fluidTank;
    private int tankX, tankY;

    public StallGui(EntityPlayer p, IInventory c) {
        super(new StallContainer(p, c), "stall", 176, 166);
        te = ((StallTileEntity) c);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.fluidTank = te.getTank();
        this.tankX = guiLeft + 7;
        this.tankY = guiTop + 5;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mX, int mY) {
        super.drawGuiContainerBackgroundLayer(f, mX, mY);
        if (fluidTank.getFluid() != null) {
            Fluid fluid = fluidTank.getFluid().getFluid();

            int rgb = 0xff000000 | fluid.getColor();
            GlStateManager.color(((rgb >> 16) & 0xFF) / 255F, ((rgb >> 8) & 0xFF) / 255F, ((rgb >> 0) & 0xFF) / 255F, 0.5F);

            TextureAtlasSprite fluidTexture = mc.getTextureMapBlocks().getTextureExtry(fluid.getFlowing().toString());
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            int fluidHeight = fluidTank.getFluidAmount() * 74 / fluidTank.getCapacity();
            drawTexturedModalRect(tankX + 1, tankY + 75 - fluidHeight, fluidTexture, 16, fluidHeight);
        }

        mc.getTextureManager().bindTexture(gui);
        drawTexturedModalRect(tankX, tankY, 176, 0, 18, 76);
    }

    @Override
    public void drawScreen(int mX, int mY, float f) {
        super.drawScreen(mX, mY, f);
        if (mX >= tankX && mY >= tankY && mX < tankX + 18 && mY < tankY + 76)
            GuiUtils.drawHoveringText(
                    Arrays.asList(String.format("%s/%s mb of %s", fluidTank.getFluidAmount(), fluidTank.getCapacity(), fluidTank.getFluid() != null ? fluidTank.getFluid().getLocalizedName() : "empty")), mX, mY,
                    mc.displayWidth, mc.displayHeight, -1, mc.fontRenderer);
    }
}
