package org.ivangeevo.animageddon.item.items;

import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.ivangeevo.animageddon.item.ModComponents;
import org.ivangeevo.animageddon.item.ModItems;
import org.ivangeevo.animageddon.util.WoolColorsHelper;
import org.ivangeevo.animageddon.util.WoolType;

import java.util.*;

public class WoolItem extends Item implements ItemColorProvider {

    static private final List<List<Integer>> colorConversionArray = new LinkedList<>();
    private static final Map<WoolType, Integer> colorMap = new HashMap<>();


    public WoolItem(Settings settings) {
        super(settings);
    }

    public static final int[] woolColors =
            {
                    0x101010, 0xb3312c, 0x3b511a, 0x51301a, 0x253192, 0x7b2fbe, 0x287697, 0x838383, 0x434343, 0xd88198,
                    0x41cd34, 0xdecf2a, 0x6689d3, 0xc354cd, 0xeb8844, 0xffffff
            };




    //------------- Class Specific Methods ------------//

    static public int averageWoolColorsInGrid(Inventory inventory)
    {
        int iAverageColor = 0;
        int iSumRed = 0;
        int iSumGreen = 0;
        int iSumBlue = 0;
        int iWoolCount = 0;

        for ( int iTempSlot = 0; iTempSlot < inventory.size(); ++iTempSlot )
        {
            ItemStack tempStack = inventory.getStack( iTempSlot );

            if ( tempStack != null )
            {
                if ( tempStack.isOf(ModItems.WOOL) /**|| tempStack.isOf(BTWRTags.Items.WOOL_KNIT_ITEMS)**/)
                {
                    int iWoolColorIndex = MathHelper.clamp( tempStack.getDamage(), 0, 15 );

                    int iWoolColor = WoolColorsHelper.woolColors[iWoolColorIndex];

                    iWoolCount++;

                    iSumRed += ( iWoolColor >> 16 ) & 255;
                    iSumGreen += ( iWoolColor >> 8 ) & 255;
                    iSumBlue += iWoolColor & 255;

                }
            }
        }

        if ( iWoolCount > 0 )
        {
            int iAverageRed = iSumRed / iWoolCount;
            int iAverageGreen = iSumGreen / iWoolCount;
            int iAverageBlue = iSumBlue / iWoolCount;

            iAverageColor = ( iAverageRed << 16 ) | ( iAverageGreen << 8 ) | iAverageBlue;

        }

        return iAverageColor;
    }

    // New method to initialize the color conversion array
    private static void initColorConversionArray() {
        colorConversionArray.clear();

        for (int iTempIndex = 0; iTempIndex < 16; iTempIndex++) {
            List<Integer> tempColorList = new LinkedList<>();
            colorConversionArray.add(iTempIndex, tempColorList);
            tempColorList.add(WoolColorsHelper.woolColors[iTempIndex]);
        }


        // Additional points to aid in coming up with reasonable conversions
        // These are the same colors that are hardcoded in recipes to result from blending dyes
        setHardColorConversionPoint(8, 0, 15);
        setHardColorConversionPoint(9, 1, 15);
        setHardColorConversionPoint(14, 1, 11);
        setHardColorConversionPoint(9, 2, 10);
        setHardColorConversionPoint(5, 4, 1);
        setHardColorConversionPoint(6, 4, 2);
        setHardColorConversionPoint(12, 4, 15);
        setHardColorConversionPoint(13, 5, 9);
        setHardColorConversionPoint(7, 8, 15);
    }

    private static void setHardColorConversionPoint(int iToColorIndex, int iFromColorIndex1, int iFromColorIndex2) {
        int iFromColor1 = WoolColorsHelper.woolColors[iFromColorIndex1];
        int iFromColor2 = WoolColorsHelper.woolColors[iFromColorIndex2];

        int iBlendedRed = (((iFromColor1 >> 16) & 255) + ((iFromColor2 >> 16) & 255)) / 2;
        int iBlendedGreen = (((iFromColor1 >> 8) & 255) + ((iFromColor2 >> 8) & 255)) / 2;
        int iBlendedBlue = ((iFromColor1 & 255) + (iFromColor2 & 255)) / 2;

        int iBlendedColor = (iBlendedRed << 16) | (iBlendedGreen << 8) | iBlendedBlue;

        colorConversionArray.get(iToColorIndex).add(iBlendedColor);
    }

    static public int getClosestColorIndex(int iColor)
    {
        int iClosestIndex = -1;
        int iClosestColorDistanceSq = 0;

        int iColorRed = ( iColor >> 16 ) & 255;
        int iColorGreen = ( iColor >> 8 ) & 255;
        int iColorBlue = iColor & 255;

        if (colorConversionArray == null )
        {
            initColorConversionArray();
        }

        if ( MathHelper.abs( iColorRed - iColorGreen ) > 5 || MathHelper.abs( iColorRed - iColorBlue ) > 5 ) // skip straight to grey scale if there isn't much difference between colors
        {
            for ( int iTempIndex = 0; iTempIndex < 16; iTempIndex++ )
            {
                List<Integer> tempColorList = colorConversionArray.get(iTempIndex);

                for (int iTempColor : tempColorList) {
                    int iTempColorRed = (iTempColor >> 16) & 255;
                    int iTempColorGreen = (iTempColor >> 8) & 255;
                    int iTempColorBlue = iTempColor & 255;

                    int iTempRedDelta = iTempColorRed - iColorRed;
                    int iTempGreenDelta = iTempColorGreen - iColorGreen;
                    int iTempBlueDelta = iTempColorBlue - iColorBlue;

                    // weighting for better aproximation based on wikipedia article on color difference

                    int iTempColorDistanceSq = 2 * iTempRedDelta * iTempRedDelta +
                            4 * iTempGreenDelta * iTempGreenDelta +
                            3 * iTempBlueDelta * iTempBlueDelta;

                    if (iClosestIndex == -1 || iTempColorDistanceSq < iClosestColorDistanceSq) {
                        iClosestIndex = iTempIndex;
                        iClosestColorDistanceSq = iTempColorDistanceSq;
                    }
                }
            }
        }

        if ( iClosestIndex == -1 || iClosestColorDistanceSq > 15000 )
        {
            // go gray scale if no match was found or if the distance to the closest match is too large

            int iColorTotal = iColorRed + iColorGreen + iColorBlue;

            if ( iColorTotal < 125 )
            {
                iClosestIndex = 0;
            }
            else if ( iColorTotal < 297 )
            {
                iClosestIndex = 8;
            }
            else if ( iColorTotal < 579 )
            {
                iClosestIndex = 7;
            }
            else
            {
                iClosestIndex = 15;
            }
        }

        return iClosestIndex;
    }


    static {
        colorMap.put(WoolType.WHITE, 0xFFFFFF);
        colorMap.put(WoolType.ORANGE, 0XEB8844);
        colorMap.put(WoolType.MAGENTA, 0XBB44BB);
        colorMap.put(WoolType.LIGHT_BLUE, 0X6699D8);
        colorMap.put(WoolType.YELLOW, 0XDBD85A);
        colorMap.put(WoolType.LIME, 0X6B8F35);
        colorMap.put(WoolType.PINK, 0XF48B8B);
        colorMap.put(WoolType.GRAY, 0X434343);
        colorMap.put(WoolType.LIGHT_GRAY, 0X9E9E9E);
        colorMap.put(WoolType.CYAN, 0X4B679D);
        colorMap.put(WoolType.PURPLE, 0XAA47BC);
        colorMap.put(WoolType.BLUE, 0X3C44AA);
        colorMap.put(WoolType.BROWN, 0X835432);
        colorMap.put(WoolType.GREEN, 0X5E7F3C);
        colorMap.put(WoolType.RED, 0XB02E26);
        colorMap.put(WoolType.BLACK, 0X1E1B1B);
    }



    @Override
    public int getHerbivoreFoodValue(int iItemDamage) {
        return 0;
    }

    @Override
    public Item setHerbivoreFoodValue(int iFoodValue) {
        return null;
    }

    @Override
    public Item setAsBasicHerbivoreFood() {
        return null;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return tintIndex;
    }
}