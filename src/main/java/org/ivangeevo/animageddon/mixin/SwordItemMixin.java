package org.ivangeevo.animageddon.mixin;

import btwr.btwr_sl.tag.BTWRConventionalTags;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SwordItem.class)
public abstract class SwordItemMixin {

    // Making sword able to efficiently break the modded web block as well as the vanilla one
    @ModifyArgs(method = "createToolComponent",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    private static void modifyToolComponentArgs(Args args) {
        args.set(0, ToolComponent.Rule.ofAlwaysDropping(BTWRConventionalTags.Blocks.WEB_BLOCKS, 15.0F));
    }



}
