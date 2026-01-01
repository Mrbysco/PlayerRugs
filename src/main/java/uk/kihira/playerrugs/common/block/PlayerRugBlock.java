package uk.kihira.playerrugs.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;

import javax.annotation.Nullable;

public class PlayerRugBlock extends BaseEntityBlock {
    public static final MapCodec<PlayerRugBlock> CODEC = simpleCodec(PlayerRugBlock::new);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty STANDING = BooleanProperty.create("standing");

    private static final VoxelShape STANDING_EAST = Block.box(0, 0, 4, 1, 16, 12);
    private static final VoxelShape STANDING_WEST = Block.box(15, 0, 4, 16, 16, 12);
    private static final VoxelShape STANDING_NORTH = Block.box(4, 0, 15, 12, 16, 16);
    private static final VoxelShape STANDING_SOUTH = Block.box(4, 0, 0, 12, 16, 1);
    private static final VoxelShape FACING_NORTH_SOUTH = Block.box(4, 0, 0, 12, 1, 16);
    private static final VoxelShape FACING_EAST_WEST = Block.box(1, 0, 4, 16, 1, 12);

    public PlayerRugBlock(Properties builder) {
        super(builder.noCollision().isViewBlocking(PlayerRugBlock::never));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(STANDING, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("SameReturnValue")
    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(STANDING)) {
            switch (state.getValue(FACING)) {
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
        return state.getValue(FACING).get2DDataValue() % 2 != 0 ? FACING_EAST_WEST : FACING_NORTH_SOUTH;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        ItemStack stack = super.getCloneItemStack(level, pos, state, includeData, player);
        if (level.getBlockEntity(pos) instanceof PlayerRugBlockEntity playerRugBlockEntity) {
            playerRugBlockEntity.saveToItem(stack, level.registryAccess());
        }
        return stack;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(STANDING, context.getClickedFace().getAxis().isHorizontal());
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING, STANDING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlayerRugBlockEntity(pos, state);
    }
}
