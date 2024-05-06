package org.ivangeevo.animageddon.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;
import org.ivangeevo.animageddon.item.ModItems;

public class AnimaggedonModItemGroup
{

    public static final ItemGroup GROUP_ANIMAGEDDON = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnimageddonMod.MOD_ID, "group_animageddon"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.group_animageddon"))
                    .icon(() -> new ItemStack(ModItems.GROUP_ANIMAGEDDON))
                    .entries((displayContext, entries) ->
                    {
                        /** ITEMS **/

                        // Uncategorized
                        entries.add(ModItems.DUNG);
                        entries.add(ModItems.DUNG_GOLDEN);

                        entries.add(ModItems.CHICKEN_FEED);

                        entries.add(ModItems.EGG_RAW);
                        entries.add(ModItems.EGG_FRIED);

                        /** BLOCKS **/


                    }
                    ).build());


    public static void registerItemGroups()
    {
        /**
        // Example of adding to existing Item Group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries ->
         {

            entries.add(BTWR_Items.CREEPER_OYSTERS);

        });
         **/
    }
}
