package org.ivangeevo.animageddon.datagen;

import btwr.btwrsl.lib.util.utils.RecipeProviderUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;
import org.ivangeevo.animageddon.block.ModBlocks;
import org.ivangeevo.animageddon.item.ModItems;
import org.ivangeevo.animageddon.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider
{


    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter)
    {
        //ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHICKEN_FEED).input(ModTags.Items.SEEDS_FOR_CHICKEN).input(Items.BONE_MEAL).criterion("has_bone_meal", RecipeProvider.conditionsFromItem(Items.BONE_MEAL)).offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STRING,2).input(Items.COBWEB).criterion("has_cobweb", RecipeProvider.conditionsFromItem(Items.COBWEB)).offerTo(exporter, Identifier.of(AnimageddonMod.MOD_ID, "string_from_cobweb"));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STRING,2).input(ModBlocks.WEB_BLOCK).criterion("has_web_block", RecipeProvider.conditionsFromItem(ModBlocks.WEB_BLOCK)).offerTo(exporter, Identifier.of(AnimageddonMod.MOD_ID, "string_from_web_block"));


    }
}