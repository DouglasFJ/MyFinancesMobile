package com.douglasfj.myfinances.utils;

import androidx.annotation.ColorRes;

import com.douglasfj.myfinances.R;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil {

    public static final Map<String, Map<String, Integer>> COLOR_MAP = new HashMap<>();

    static {
        // Emerald Palette
        Map<String, Integer> mapEmerald = new HashMap<>();
        mapEmerald.put("50", R.color.emerald_50);
        mapEmerald.put("100", R.color.emerald_100);
        mapEmerald.put("200", R.color.emerald_200);
        mapEmerald.put("300", R.color.emerald_300);
        mapEmerald.put("400", R.color.emerald_400);
        mapEmerald.put("500", R.color.emerald_500);
        mapEmerald.put("600", R.color.emerald_600);
        mapEmerald.put("700", R.color.emerald_700);
        mapEmerald.put("800", R.color.emerald_800);
        mapEmerald.put("900", R.color.emerald_900);
        mapEmerald.put("950", R.color.emerald_950);

        // Amber Palette
        Map<String, Integer> mapAmber = new HashMap<>();
        mapAmber.put("50", R.color.amber_50);
        mapAmber.put("100", R.color.amber_100);
        mapAmber.put("200", R.color.amber_200);
        mapAmber.put("300", R.color.amber_300);
        mapAmber.put("400", R.color.amber_400);
        mapAmber.put("500", R.color.amber_500);
        mapAmber.put("600", R.color.amber_600);
        mapAmber.put("700", R.color.amber_700);
        mapAmber.put("800", R.color.amber_800);
        mapAmber.put("900", R.color.amber_900);
        mapAmber.put("950", R.color.amber_950);

        // Red Palette
        Map<String, Integer> mapRed = new HashMap<>();
        mapRed.put("50", R.color.red_50);
        mapRed.put("100", R.color.red_100);
        mapRed.put("200", R.color.red_200);
        mapRed.put("300", R.color.red_300);
        mapRed.put("400", R.color.red_400);
        mapRed.put("500", R.color.red_500);
        mapRed.put("600", R.color.red_600);
        mapRed.put("700", R.color.red_700);
        mapRed.put("800", R.color.red_800);
        mapRed.put("900", R.color.red_900);
        mapRed.put("950", R.color.red_950);

        // Neutral Palette
        Map<String, Integer> mapNeutral = new HashMap<>();
        mapNeutral.put("50", R.color.neutral_50);
        mapNeutral.put("100", R.color.neutral_100);
        mapNeutral.put("200", R.color.neutral_200);
        mapNeutral.put("300", R.color.neutral_300);
        mapNeutral.put("400", R.color.neutral_400);
        mapNeutral.put("500", R.color.neutral_500);
        mapNeutral.put("600", R.color.neutral_600);
        mapNeutral.put("700", R.color.neutral_700);
        mapNeutral.put("800", R.color.neutral_800);
        mapNeutral.put("900", R.color.neutral_900);
        mapNeutral.put("950", R.color.neutral_950);

        COLOR_MAP.put("red", mapRed);
        COLOR_MAP.put("emerald", mapEmerald);
        COLOR_MAP.put("amber", mapAmber);
        COLOR_MAP.put("neutral", mapNeutral);
    }

    public static @ColorRes int getColorIDByNameAndWeight(String colorName, String weightName) {
        Map<String, Integer> mapSelectedColor = COLOR_MAP.get(colorName);
        return mapSelectedColor.get(weightName);
    }
}
