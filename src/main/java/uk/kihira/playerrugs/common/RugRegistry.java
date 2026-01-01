package uk.kihira.playerrugs.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.common.block.PlayerRugBlock;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;
import uk.kihira.playerrugs.common.item.PlayerRugItem;

public class RugRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PlayerRugs.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PlayerRugs.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, PlayerRugs.MOD_ID);

    public static final DeferredBlock<PlayerRugBlock> PLAYER_RUG = BLOCKS.registerBlock("player_rug", PlayerRugBlock::new, () -> Block.Properties.ofFullCopy(Blocks.WHITE_CARPET).strength(0.1F).sound(SoundType.WOOL));
    public static final DeferredItem<PlayerRugItem> PLAYER_RUG_ITEM = ITEMS.registerItem("player_rug", (properties) -> new PlayerRugItem(PLAYER_RUG.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlayerRugBlockEntity>> PLAYER_RUG_BLOCK_ENTITY = BLOCK_ENTITIES.register("player_rug", () ->
            new BlockEntityType<>(PlayerRugBlockEntity::new, RugRegistry.PLAYER_RUG.get())
    );
}
