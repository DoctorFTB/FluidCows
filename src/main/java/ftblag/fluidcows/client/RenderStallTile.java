package ftblag.fluidcows.client;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.block.stall.StallBlock;
import ftblag.fluidcows.block.stall.StallTileEntity;
import ftblag.fluidcows.gson.FCConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.EnumFacing;

public class RenderStallTile extends TileEntitySpecialRenderer<StallTileEntity> {

    private static EntityCow COW;

    @Override
    public void render(StallTileEntity te, double x, double y, double z, float f, int d, float a) {
        try {
            boolean flag = te.getWorld() != null;
            boolean flag1 = !flag || (te.getBlockType() == FluidCows.stall);

            if (flag1) {
                renderAModelAt(te, x, y, z, f);
                super.render(te, x, y, z, f, d, a);
            }
        } catch (IllegalArgumentException e) {
        }
    }

    public void renderAModelAt(StallTileEntity te, double x, double y, double z, float f) {
        if (te.cow == null) {
            return;
        }
        if (COW == null)
            COW = new EntityCow(Minecraft.getMinecraft().world);

        IBlockState state = te.getWorld().getBlockState(te.getPos());

        EnumFacing facing = state.getValue(StallBlock.FACING);
        if (!state.getValue(StallBlock.HASCOW)) {
            return;
        }

        if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
            facing = facing.getOpposite();
        }

        GlStateManager.pushMatrix();

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.translate(x + .5, y + .1, z + .5);
        GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0);
        GlStateManager.enableLighting();
        GlStateManager.scale(0.55, 0.55, 0.55);
        Minecraft.getMinecraft().getRenderManager().renderEntity(FCConfig.hideFluidCow ? COW : te.cow, 0, 0, 0, 0, 0, true);
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
