package uk.kihira.playerrugs.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.common.block.PlayerRugBlock;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;
import uk.kihira.playerrugs.common.item.PlayerRugItem;

public class RugRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PlayerRugs.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PlayerRugs.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PlayerRugs.MOD_ID);

    public static final RegistryObject<PlayerRugBlock> PLAYER_RUG = BLOCKS.register("player_rug", () -> new PlayerRugBlock(Block.Properties.copy(Blocks.WHITE_CARPET).strength(0.1F).sound(SoundType.WOOL)));
    public static final RegistryObject<PlayerRugItem> PLAYER_RUG_ITEM = ITEMS.register("player_rug", () -> new PlayerRugItem(PLAYER_RUG.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<PlayerRugBlockEntity>> PLAYER_RUG_BLOCK_ENTITY = BLOCK_ENTITIES.register("player_rug", () ->
            BlockEntityType.Builder.of(PlayerRugBlockEntity::new, RugRegistry.PLAYER_RUG.get()).build(null));
}
