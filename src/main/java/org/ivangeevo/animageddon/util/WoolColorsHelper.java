package org.ivangeevo.animageddon.util;

public class WoolColorsHelper {


    public static final String[] woolColorNames = new String[]{
            "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "Light Gray", "Gray", "Pink",
            "Lime", "Yellow", "Light Blue", "Magenta", "Orange", "White"
    };

    public static final int[] woolColors =
            {
                    0x101010, 0xb3312c, 0x3b511a, 0x51301a, 0x253192, 0x7b2fbe, 0x287697, 0x838383, 0x434343, 0xd88198,
                    0x41cd34, 0xdecf2a, 0x6689d3, 0xc354cd, 0xeb8844, 0xffffff
            };

    public static final int WHITE = 0;
    public static final int ORANGE = 1;
    public static final int MAGENTA = 2;
    public static final int LIGHT_BLUE = 3;
    public static final int YELLOW = 4;
    public static final int LIME = 5;
    public static final int PINK = 6;
    public static final int GRAY = 7;
    public static final int LIGHT_GRAY = 8;
    public static final int CYAN = 9;
    public static final int PURPLE = 10;
    public static final int BLUE = 11;
    public static final int BROWN = 12;
    public static final int GREEN = 13;
    public static final int RED = 14;
    public static final int BLACK = 15;

     public static int getWoolColorIndex(int color) {
        return switch (color) {
            case ORANGE -> 1;
            case MAGENTA -> 2;
            case LIGHT_BLUE -> 3;
            case YELLOW -> 4;
            case LIME -> 5;
            case PINK -> 6;
            case GRAY -> 7;
            case LIGHT_GRAY -> 8;
            case CYAN -> 9;
            case PURPLE -> 10;
            case BLUE -> 11;
            case BROWN -> 12;
            case GREEN -> 13;
            case RED -> 14;
            case BLACK -> 15;
            default -> WHITE;
        };
    }
}
