package uk.kihira.playerrugs.generator;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.client.renderer.PlayerRugInventoryRenderer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static uk.kihira.playerrugs.common.RugRegistry.BLOCKS;
import static uk.kihira.playerrugs.common.RugRegistry.PLAYER_RUG;

@EventBusSubscriber
public class DataCreator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new Recipes.Runner(packOutput, lookupProvider));
        generator.addProvider(true, new Loots(packOutput, lookupProvider));

        generator.addProvider(true, new Language(packOutput));
        generator.addProvider(true, new Models(packOutput));

    }

    private static class Recipes extends RecipeProvider {
        public Recipes(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            super(provider, recipeOutput);
        }

        @Override
        protected void buildRecipes() {
            shaped(RecipeCategory.MISC, PLAYER_RUG.get())
                    .pattern(" P ")
                    .pattern("LLL")
                    .pattern("LLL")
                    .define('P', Items.PLAYER_HEAD)
                    .define('L', Tags.Items.LEATHERS)
                    .unlockedBy("has_player_head", has(Items.PLAYER_HEAD))
                    .unlockedBy("has_leather", has(Tags.Items.LEATHERS))
                    .save(output);
        }

        public static class Runner extends RecipeProvider.Runner {
            public Runner(PackOutput output, CompletableFuture<Provider> completableFuture) {
                super(output, completableFuture);
            }

            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
                return new Recipes(provider, recipeOutput);
            }

            @Override
            public String getName() {
                return "Player Rugs Recipes";
            }
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
                                .withPool(this.applyExplosionCondition(block, LootPool.lootPool()
                                                .setRolls(ConstantValue.exactly(1.0F))
                                                .add(LootItem.lootTableItem(block)
                                                        .apply(
                                                                CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                                                        .include(DataComponents.PROFILE)
                                                        )
                                                )
                                        )
                                )
                );
            }

            @Override
            protected Iterable<Block> getKnownBlocks() {
                return BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
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

            addConfig("server", "Server", "Server Settings");
            addConfig("easyCrafting", "Easy Crafting", "If true, allows rugs to be renamed in anvils to get that players rug.");
        }

        /**
         * Add the translation for a config entry
         *
         * @param path        The path of the config entry
         * @param name        The name of the config entry
         * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
         */
        private void addConfig(String path, String name, @Nullable String description) {
            this.add(PlayerRugs.MOD_ID + ".configuration." + path, name);
            if (description != null && !description.isEmpty())
                this.add(PlayerRugs.MOD_ID + ".configuration." + path + ".tooltip", description);
        }
    }

    private static class Models extends ModelProvider {
        public Models(PackOutput packOutput) {
            super(packOutput, PlayerRugs.MOD_ID);
        }

        private static final ModelTemplate PLAYER_RUG_TEMPLATE = ModelTemplates.create("playerrugs:player_rug", TextureSlot.PARTICLE);

        @Override
        protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
            blockModels.createParticleOnlyBlock(PLAYER_RUG.get(), Blocks.SOUL_SAND);
            Item item = PLAYER_RUG.get().asItem();
            Identifier identifier = PLAYER_RUG_TEMPLATE.create(item, TextureMapping.particle(PLAYER_RUG.get()), blockModels.modelOutput);
            ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.specialModel(identifier, new PlayerRugInventoryRenderer.Unbaked());
            itemModels.itemModelOutput.accept(item, itemmodel$unbaked);
        }
    }
}
