package org.btwr.animageddon.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import org.btwr.animageddon.entity.ModEntities;
import org.btwr.animageddon.entity.projectile.SpiderWebEntity;

public class ModUseEvents {

    public static void register() {

        // test event
        /**
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (world.isClient) return TypedActionResult.pass(stack);

            if (stack.isOf(Items.STICK)) {
                SpiderWebEntity webEntity = new SpiderWebEntity(ModEntities.SPIDER_WEB, world);
                Vec3d look = player.getRotationVec(1.0F);
                webEntity.setPosition(
                        player.getX() + look.x * 1.5,
                        player.getEyeY() - 0.1,
                        player.getZ() + look.z * 1.5
                );

                webEntity.setVelocity(look.x, look.y, look.z, 1.5f, 0f);

                world.spawnEntity(webEntity);

                return TypedActionResult.success(stack);
            }

            return TypedActionResult.pass(stack);
        });
         **/
    }
}
