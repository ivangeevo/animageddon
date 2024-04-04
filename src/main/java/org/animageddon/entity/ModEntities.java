package org.animageddon.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.animageddon.AnimageddonMod;
import org.animageddon.entity.projectile.CobwebEntity;

public class ModEntities
{
    public static class Blocks
    {
        //public static BlockEntityType<CobwebEntity> OVEN_BRICK;

        public static void registerBlockEntities()
        {
            /**
            OVEN_BRICK = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AnimageddonMod.MOD_ID,
                    "oven_brick"), FabricBlockEntityTypeBuilder.create(BrickOvenBlockEntity::new,
                    ModBlocks.OVEN_BRICK).build(null));
             **/

        }
    }

    public static class Projectiles
    {
        /**
        public static final EntityType<CobwebEntity> COBWEB_PROJECTILE =
                Registry.register(Registries.ENTITY_TYPE,
                new Identifier(AnimageddonMod.MOD_ID, "cobweb_projectile"),
                FabricEntityTypeBuilder.<CobwebEntity>create(SpawnGroup.MISC, CobwebEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
         **/
    }


}
