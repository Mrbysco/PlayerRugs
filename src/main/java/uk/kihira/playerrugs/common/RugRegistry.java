package uk.kihira.playerrugs.common;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.common.blocks.PlayerRugBlock;
import uk.kihira.playerrugs.common.items.PlayerRugItem;
import uk.kihira.playerrugs.common.tileentities.PlayerRugTE;

public class RugRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PlayerRugs.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PlayerRugs.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, PlayerRugs.MOD_ID);

    public static final RegistryObject<Block> PLAYER_RUG = BLOCKS.register("player_rug", () -> new PlayerRugBlock(Block.Properties.create(Material.CARPET).hardnessAndResistance(0.1F).sound(SoundType.CLOTH)));
    public static final RegistryObject<Item> PLAYER_RUG_ITEM = ITEMS.register("player_rug", () -> new PlayerRugItem(PLAYER_RUG.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));

    public static final RegistryObject<TileEntityType<PlayerRugTE>> PLAYER_RUG_TILE = TILES.register("player_rug_tile", () -> TileEntityType.Builder.create(PlayerRugTE::new, RugRegistry.PLAYER_RUG.get()).build(null));
}
