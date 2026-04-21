package org.btwr.animageddon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.AnimageddonMod;
import org.btwr.animageddon.block.ModBlocks;
import org.btwr.animageddon.item.ModItems;
import org.btwr.animageddon.tag.ModTags;
import org.btwr.shared_library.api.item.ProgressiveCraftingItem;
import org.btwr.shared_library.recipe.ExtendedShapelessRecipe;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHICKEN_FEED)
                .input(ModTags.Items.SEEDS_FOR_CHICKEN)
                .input(Items.BONE_MEAL)
                .criterion("has_bone_meal", RecipeProvider.conditionsFromItem(Items.BONE_MEAL))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STRING,2)
                .input(Items.COBWEB)
                .criterion("has_cobweb", RecipeProvider.conditionsFromItem(Items.COBWEB))
                .offerTo(exporter, Identifier.of(AnimageddonMod.MOD_ID, "string_from_cobweb"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STRING,2)
                .input(ModBlocks.WEB_BLOCK)
                .criterion("has_web_block", conditionsFromItem(ModBlocks.WEB_BLOCK))
                .offerTo(exporter, Identifier.of(AnimageddonMod.MOD_ID, "string_from_web_block"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GUNPOWDER,2)
                .input(ModItems.NITRE)
                .input(Items.COAL)
                .criterion("has_nitre", conditionsFromItem(ModItems.NITRE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CURED_MEAT)
                .input(ConventionalItemTags.RAW_MEAT_FOODS)
                .input(ModItems.NITRE)
                .criterion("has_nitre", conditionsFromItem(ModItems.NITRE))
                .offerTo(exporter);

        // Web Untangling
        ItemStack webUntanglingResult = new ItemStack(ModItems.WEB_UNTANGLING);
        webUntanglingResult.setDamage(ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE - 1);

        ExtendedShapelessRecipe.JsonBuilder.create(RecipeCategory.MISC, webUntanglingResult)
                .withToolDamage()
                .input(ModItems.TANGLED_WEB)
                .criterion(hasItem(ModItems.TANGLED_WEB), conditionsFromItem(ModItems.TANGLED_WEB))
                .offerTo(exporter);
    }

}