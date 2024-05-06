package org.ivangeevo.animageddon.entity;

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
