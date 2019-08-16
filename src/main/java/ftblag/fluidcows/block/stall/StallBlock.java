package ftblag.fluidcows.block.stall;

import ftblag.fluidcows.FluidCows;
import ftblag.fluidcows.base.BaseBlock;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

public class StallBlock extends BaseBlock implements ITileEntityProvider {

    private static final AxisAlignedBB STALL_BOX = new AxisAlignedBB(0.065, 0, 0.06, 0.935, 0.96 ,1);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool HASCOW = PropertyBool.create("cow");

    public StallBlock() {
        super(Material.ROCK, "stall");
        setHardness(2);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HASCOW, false));
        GameRegistry.registerTileEntity(StallTileEntity.class, "stall_te");
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        return worldIn.getTileEntity(pos).receiveClientEvent(id, param);
    }

    public static void update(World world, BlockPos pos, boolean cow) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == FluidCows.stall && state.getValue(HASCOW) != cow)
            world.setBlockState(pos, FluidCows.stall.getDefaultState().withProperty(FACING, state.getValue(FACING)).withProperty(HASCOW, cow));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;
        if (player.isSneaking())
            player.openGui(FluidCows.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
        else {
            FluidUtil.interactWithFluidHandler(player, hand, (StallTileEntity) world.getTileEntity(pos));
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        StallTileEntity te = (StallTileEntity) world.getTileEntity(pos);

        te.spawnEntity();
        for (ItemStack stack : te.getInventory()) {
            if (!stack.isEmpty()) {
                world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack));
            }
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(HASCOW, false);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(HASCOW, false), 2);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new StallTileEntity();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return STALL_BOX;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta)).withProperty(HASCOW, (meta >> 2) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i |= state.getValue(FACING).getHorizontalIndex();
        i |= (state.getValue(HASCOW) ? 1 : 0) << 2;
        return i;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HASCOW);
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
