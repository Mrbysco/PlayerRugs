package uk.kihira.playerrugs.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction.Source;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import uk.kihira.playerrugs.PlayerRugs;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static uk.kihira.playerrugs.common.RugRegistry.BLOCKS;
import static uk.kihira.playerrugs.common.RugRegistry.PLAYER_RUG;

@EventBusSubscriber(modid = PlayerRugs.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataCreator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(true, new Recipes(packOutput, lookupProvider));
            generator.addProvider(true, new Loots(packOutput, lookupProvider));
        }
        if (event.includeClient()) {
            generator.addProvider(true, new Language(packOutput));
            generator.addProvider(true, new BlockStates(packOutput, helper));
            generator.addProvider(true, new ItemModels(packOutput, helper));
        }
    }

    private static class Recipes extends RecipeProvider {
        public Recipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
        }

        @Override
        protected void buildRecipes(RecipeOutput output, HolderLookup.Provider provider) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PLAYER_RUG.get())
                    .pattern(" P ")
                    .pattern("LLL")
                    .pattern("LLL")
                    .define('P', Items.PLAYER_HEAD)
                    .define('L', Tags.Items.LEATHERS)
                    .unlockedBy("has_player_head", has(Items.PLAYER_HEAD))
                    .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                    .save(output);
        }
    }

    private static class Loots extends LootTableProvider {
        public Loots(PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
            super(packOutput, Set.of(), List.of(
                    new SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)
            ), lookupProvider);
        }

        private static class Blocks extends BlockLootSubProvider {

            protected Blocks(HolderLookup.Provider provider) {
                super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
            }

            @Override
            protected void generate() {
                this.add(PLAYER_RUG.get(), (block) ->
                        LootTable.lootTable()
                                .withPool((LootPool.Builder)this.applyExplosionCondition(block, LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(block)
                                                .apply(CopyComponentsFunction.copyComponents(Source.BLOCK_ENTITY)
                                                        .include(DataComponents.PROFILE)
                                                )))));
            }

            @Override
            protected Iterable<Block> getKnownBlocks() {
                return (Iterable<Block>) BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
            }
        }
    }

    private static class Language extends LanguageProvider {
        public Language(PackOutput packOutput) {
            super(packOutput, PlayerRugs.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add(PLAYER_RUG.get(), "Player Rug");
            add("playerrugs.tooltip", "Player: %s");
        }
    }

    private static class ItemModels extends ItemModelProvider {
        public ItemModels(PackOutput packOutput, ExistingFileHelper helper) {
            super(packOutput, PlayerRugs.MOD_ID, helper);
        }

        @Override
        protected void registerModels() {
            makeUnchecked(PLAYER_RUG);
        }

        @SuppressWarnings("SameParameterValue")
        private void makeUnchecked(DeferredHolder<Block, ? extends Block> registryObject) {
            String path = registryObject.getId().getPath();
            getBuilder(path)
                    .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
        }

        @Override
        public String getName() {
            return "Item Models";
        }
    }

    private static class BlockStates extends BlockStateProvider {

        public BlockStates(PackOutput packOutput, ExistingFileHelper helper) {
            super(packOutput, PlayerRugs.MOD_ID, helper);
        }

        @Override
        protected void registerStatesAndModels() {
            makeState(PLAYER_RUG);
        }

        @SuppressWarnings("SameParameterValue")
        private void makeState(DeferredHolder<Block, ? extends Block> registryObject) {
            ModelFile model = models().getExistingFile(modLoc(registryObject.getId().getPath()));
            getVariantBuilder(registryObject.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
        }
    }
}
