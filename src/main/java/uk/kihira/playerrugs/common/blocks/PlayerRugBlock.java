package uk.kihira.playerrugs.common.blocks;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import uk.kihira.playerrugs.common.tileentities.PlayerRugTE;
import uk.kihira.playerrugs.common.util.ProfileHelper;

import javax.annotation.Nullable;

public class PlayerRugBlock extends ContainerBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty STANDING = BooleanProperty.create("standing");

    private static final VoxelShape STANDING_EAST = Block.makeCuboidShape(0, 0, 4, 1, 16, 12);
    private static final VoxelShape STANDING_WEST = Block.makeCuboidShape(15, 0, 4, 16, 16, 12);
    private static final VoxelShape STANDING_NORTH = Block.makeCuboidShape(4, 0, 15, 12, 16, 16);
    private static final VoxelShape STANDING_SOUTH = Block.makeCuboidShape(4, 0, 0, 12, 16, 1);
    private static final VoxelShape FACING_NORTH_SOUTH = Block.makeCuboidShape(4, 0, 0, 12, 1, 16);
    private static final VoxelShape FACING_EAST_WEST = Block.makeCuboidShape(16, 0, 12, 1, 1, 4);

    public PlayerRugBlock(AbstractBlock.Properties builder) {
        super(builder.notSolid().setOpaque(PlayerRugBlock::isntSolid));
        setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(STANDING, false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private static boolean isntSolid(BlockState state, IBlockReader reader, BlockPos pos) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (stack.hasTag() && stack.getTag() != null && world.getTileEntity(pos) != null) {
            PlayerRugTE rugTE = (PlayerRugTE)world.getTileEntity(pos);
            GameProfile gameProfile = NBTUtil.readGameProfile(stack.getChildTag("PlayerProfile"));
            rugTE.setPlayerProfile(gameProfile);
            rugTE.markDirty();
        }
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
        if (te instanceof PlayerRugTE) {
            spawnAsEntity(worldIn, pos, ProfileHelper.getPlayerRugStack(((PlayerRugTE) te).getPlayerProfile()));
        }
        else super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.get(STANDING)) {
            switch (state.get(FACING)) {
                case NORTH:
                    return STANDING_NORTH;
                case SOUTH:
                    return STANDING_SOUTH;
                case WEST:
                    return STANDING_WEST;
                case EAST:
                    return STANDING_EAST;
            }
        }
        return state.get(FACING).getHorizontalIndex() % 2 != 0 ? FACING_EAST_WEST : FACING_NORTH_SOUTH;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity te = world.getTileEntity(pos);
        if(te != null) {
            return ProfileHelper.getPlayerRugStack(((PlayerRugTE) world.getTileEntity(pos)).getPlayerProfile());
        } else {
            return super.getPickBlock(state, target, world, pos, player);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return (BlockState)this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(STANDING, context.getFace().getAxis().isHorizontal());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, STANDING});
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return new PlayerRugTE();
    }
}
