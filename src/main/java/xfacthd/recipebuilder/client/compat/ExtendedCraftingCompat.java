package xfacthd.recipebuilder.client.compat;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.recipebuilder.client.builders.extendedcrafting.*;
import xfacthd.recipebuilder.client.data.AbstractBuilder;

import java.util.List;

public class ExtendedCraftingCompat
{
    public static final String MOD_ID = "extendedcrafting";

    public static void register(List<AbstractBuilder> builders)
    {
        IRecipeSerializer<?> shapedTable = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(MOD_ID, "shaped_table"));
        IRecipeSerializer<?> shapelessTable = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(MOD_ID, "shapeless_table"));
        IRecipeSerializer<?> shapedEnder = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(MOD_ID, "shaped_ender_crafter"));
        IRecipeSerializer<?> shapelessEnder = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(MOD_ID, "shapeless_ender_crafter"));

        builders.add(new ShapedTableBuilder(shapedTable, "basic_table",    "basic_table",    1, 31, 17, 114,  54));
        builders.add(new ShapedTableBuilder(shapedTable, "advanced_table", "advanced_table", 2, 13, 17, 150,  90));
        builders.add(new ShapedTableBuilder(shapedTable, "elite_table",    "elite_table",    3,  7, 17, 186, 126));
        builders.add(new ShapedTableBuilder(shapedTable, "ultimate_table", "ultimate_table", 4,  7, 17, 220, 162));

        builders.add(new ShapelessTableBuilder(shapelessTable, "basic_table",    "basic_table",    1, 31, 17, 114,  54));
        builders.add(new ShapelessTableBuilder(shapelessTable, "advanced_table", "advanced_table", 2, 13, 17, 150,  90));
        builders.add(new ShapelessTableBuilder(shapelessTable, "elite_table",    "elite_table",    3,  7, 17, 186, 126));
        builders.add(new ShapelessTableBuilder(shapelessTable, "ultimate_table", "ultimate_table", 4,  7, 17, 220, 162));

        builders.add(new ShapedTableBuilder(shapedEnder, "ender_crafter", "ender_crafter", 0, 29, 17, 116, 54));
        builders.add(new ShapelessTableBuilder(shapelessEnder, "ender_crafter", "ender_crafter", 0, 29, 17, 116, 54));

        builders.add(new QuantumCompressorBuilder());

        builders.add(new CombinationRecipeBuilder());
    }
}
