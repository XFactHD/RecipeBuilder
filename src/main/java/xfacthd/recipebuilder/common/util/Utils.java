package xfacthd.recipebuilder.common.util;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import xfacthd.recipebuilder.RecipeBuilder;

import java.util.ArrayList;
import java.util.List;

public class Utils
{
    private static final boolean CHECK_TRANSLATED = true;
    private static final List<String> TRANSLATION_KEYS = new ArrayList<>();
    private static boolean doneLoading = false;

    public static ResourceLocation location(String path) { return new ResourceLocation(RecipeBuilder.MOD_ID, path); }

    public static TranslatableComponent translate(String prefix, String postfix)
    {
        String key = prefix != null ? prefix + "." : "";
        key += RecipeBuilder.MOD_ID + "." + postfix;

        if (CHECK_TRANSLATED)
        {
            if (!doneLoading)
            {
                TRANSLATION_KEYS.add(key);
            }
            else if (!Language.getInstance().has(key))
            {
                RecipeBuilder.LOGGER.warn("No translation for key: " + key);
            }
        }

        return new TranslatableComponent(key);
    }

    public static void enteredMainMenu()
    {
        if (!CHECK_TRANSLATED || doneLoading) { return; } // Only spam once

        doneLoading = true;

        Language map = Language.getInstance();
        for (String key : TRANSLATION_KEYS)
        {
            if (!map.has(key))
            {
                RecipeBuilder.LOGGER.warn("No translation for key: " + key);
            }
        }
    }
}