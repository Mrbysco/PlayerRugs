package uk.kihira.playerrugs.generator;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import uk.kihira.playerrugs.PlayerRugs;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static uk.kihira.playerrugs.common.RugRegistry.BLOCKS;
import static uk.kihira.playerrugs.common.RugRegistry.PLAYER_RUG;

@EventBusSubscriber(modid = PlayerRugs.MOD_ID, bus = Bus.MOD)
public class DataCreator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            gen.addProvider(new Recipes(gen));
            gen.addProvider(new Loots(gen));
        }
        if (event.includeClient()) {
            gen.addProvider(new Language(gen));
            gen.addProvider(new BlockStates(gen, helper));
            gen.addProvider(new ItemModels(gen, helper));
        }
    }

    private static class Recipes extends RecipeProvider {
        public Recipes(DataGenerator gen) {
            super(gen);
        }

        @Override
        protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
//            getTier(TIER1_BLOCK.get(), ItemTags.LOGS).build(consumer);
        }

        private ShapedRecipeBuilder getTier(IItemProvider item, Tag<Item> resource) {
            return ShapedRecipeBuilder.shapedRecipe(item)
                    .key('W', Items.WATER_BUCKET)
                    .key('L', Items.LAVA_BUCKET)
                    .key('G', Blocks.GLASS)
                    .key('R', resource)
                    .patternLine("RRR")
                    .patternLine("WGL")
                    .patternLine("RRR")
                    .addCriterion("has_lava", hasItem(Items.LAVA_BUCKET))
                    .addCriterion("has_water", hasItem(Items.WATER_BUCKET));
        }
    }

    private static class Loots extends LootTableProvider {
        public Loots(DataGenerator gen) {
            super(gen);
        }

        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> getTables() {
            return ImmutableList.of(
                    Pair.of(Blocks::new, LootParameterSets.BLOCK)
            );
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
            map.forEach((name, table) -> LootTableManager.validateLootTable(validationtracker, name, table));
        }

        private class Blocks extends BlockLootTables {
            @Override
            protected void addTables() {
                this.registerLootTable(PLAYER_RUG.get(), (playerHead) -> {
                    return LootTable.builder().addLootPool(withSurvivesExplosion(playerHead, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(playerHead).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("PlayerProfile", "PlayerProfile")))));
                });
            }

            @Override
            protected Iterable<Block> getKnownBlocks() {
                return (Iterable<Block>)BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
            }
        }
    }

    private static class Language extends LanguageProvider {
        public Language(DataGenerator gen) {
            super(gen, PlayerRugs.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add(PLAYER_RUG.get(), "Player Rug");
        }
    }

    private static class ItemModels extends ItemModelProvider {
        public ItemModels(DataGenerator gen, ExistingFileHelper helper) {
            super(gen, PlayerRugs.MOD_ID, helper);
        }

        @Override
        protected void registerModels() {
            makeTier(PLAYER_RUG.get());
        }

        private void makeTier(Block block) {
            String path = block.getRegistryName().getPath();
            getBuilder(path)
                    .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
        }

        @Override
        public String getName() {
            return "Item Models";
        }
    }

    private static class BlockStates extends BlockStateProvider {

        public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
            super(gen, PlayerRugs.MOD_ID, helper);
        }

        @Override
        protected void registerStatesAndModels() {
            makeState(PLAYER_RUG.get());
        }

        private void makeState(Block block) {
            ModelFile model = models().getExistingFile(modLoc(block.getRegistryName().getPath()));
            getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
        }
    }
}
