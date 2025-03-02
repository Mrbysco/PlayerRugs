package uk.kihira.playerrugs.generator;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegistryObject;
import uk.kihira.playerrugs.PlayerRugs;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static uk.kihira.playerrugs.common.RugRegistry.BLOCKS;
import static uk.kihira.playerrugs.common.RugRegistry.PLAYER_RUG;

@EventBusSubscriber(modid = PlayerRugs.MOD_ID, bus = Bus.MOD)
public class DataCreator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(true, new Recipes(packOutput));
            generator.addProvider(true, new Loots(packOutput));
        }
        if (event.includeClient()) {
            generator.addProvider(true, new Language(packOutput));
            generator.addProvider(true, new BlockStates(packOutput, helper));
            generator.addProvider(true, new ItemModels(packOutput, helper));
        }
    }

    private static class Recipes extends RecipeProvider {
        public Recipes(PackOutput packOutput) {
            super(packOutput);
        }

        @Override
        protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PLAYER_RUG.get())
                    .pattern(" P ")
                    .pattern("LLL")
                    .pattern("LLL")
                    .define('P', Items.PLAYER_HEAD)
                    .define('L', Tags.Items.LEATHER)
                    .unlockedBy("has_player_head", has(Items.PLAYER_HEAD))
                    .unlockedBy("has_leather", has(Tags.Items.LEATHER))
                    .save(consumer);
        }
    }

    private static class Loots extends LootTableProvider {

        public Loots(PackOutput packOutput) {
            super(packOutput, Set.of(), List.of(
                    new SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)
            ));
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
            map.forEach((name, table) -> table.validate(validationtracker));
        }

        private static class Blocks extends BlockLootSubProvider {

            protected Blocks() {
                super(Set.of(), FeatureFlags.REGISTRY.allFlags());
            }

            @Override
            protected void generate() {
                this.add(PLAYER_RUG.get(), createNameableBlockEntityTable(PLAYER_RUG.get()));
            }

            @Override
            protected Iterable<Block> getKnownBlocks() {
                return (Iterable<Block>) BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
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
        }
    }

    private static class ItemModels extends ItemModelProvider {
        public ItemModels(PackOutput packOutput, ExistingFileHelper helper) {
            super(packOutput, PlayerRugs.MOD_ID, helper);
        }

        @Override
        protected void registerModels() {
            makeTier(PLAYER_RUG);
        }

        private void makeTier(RegistryObject<? extends Block> registryObject) {
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

        private void makeState(RegistryObject<? extends Block> registryObject) {
            ModelFile model = models().getExistingFile(modLoc(registryObject.getId().getPath()));
            getVariantBuilder(registryObject.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
        }
    }
}
