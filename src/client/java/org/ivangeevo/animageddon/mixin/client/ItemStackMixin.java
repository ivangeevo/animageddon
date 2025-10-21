package org.ivangeevo.animageddon.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    // Shows detailed data for the components of an itemstack in its item's tooltip
    // (slightly laggy, but mostly used it for debugging)
    //@Inject(method = "getTooltip", at = @At("TAIL"))
    private void appendComponentDebug(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type,
                                      CallbackInfoReturnable<List<Text>> cir) {

        // Only run client-side and when F3+H (advanced tooltips) is enabled
        if (!MinecraftClient.getInstance().options.advancedItemTooltips) return;

        ItemStack stack = (ItemStack) (Object) this;
        List<Text> tooltip = cir.getReturnValue();

        var components = stack.getComponents();
        if (components.isEmpty()) return;

        // Add components to the tooltip
        components.forEach(component -> {
            tooltip.add(Text.literal("§8 - " + component.getClass().getSimpleName() + ": " + component.toString()));
        });
    }
}



