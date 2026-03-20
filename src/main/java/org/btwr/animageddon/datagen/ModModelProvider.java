package org.btwr.animageddon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.item.ModItems;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {

    public static final Model HANDHELD_TWO_LAYERS = item("handheld", TextureKey.LAYER0, TextureKey.LAYER1);

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BURNED_MEAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHICKEN_FEED, Models.GENERATED);
        itemModelGenerator.register(ModItems.CURED_MEAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.NITRE, Models.GENERATED);
        // couldn't figure out how to register the web_block item model (registered manually; look in resources)
        itemModelGenerator.register(ModItems.TANGLED_WEB, Models.GENERATED);

        // animageddon web untangling model
        itemModelGenerator.register(ModItems.WEB_UNTANGLING, Models.HANDHELD);

        // btwr ds web untangling
        /**
        Identifier webUntanglingId = Identifier.of(AnimageddonMod.MOD_ID, "item/web_untangling");
        HANDHELD_TWO_LAYERS.upload(
                webUntanglingId,
                TextureMap.layered(webUntanglingId, ModItems.CHISEL_STONE)
                );
        **/
    }

    private static Model item(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla("item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

}