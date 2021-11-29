package xfacthd.recipebuilder.client.builders.vanilla;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import xfacthd.recipebuilder.client.data.*;
import xfacthd.recipebuilder.client.data.slots.*;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class CookingBuilder extends AbstractBuilder
{
    private static final Map<IRecipeSerializer<?>, ResourceLocation> TEXTURES = Util.make(new HashMap<>(), map ->
    {
        map.put(IRecipeSerializer.SMELTING_RECIPE, new ResourceLocation("minecraft", "textures/gui/container/furnace.png"));
        map.put(IRecipeSerializer.BLASTING_RECIPE, new ResourceLocation("minecraft", "textures/gui/container/blast_furnace.png"));
        map.put(IRecipeSerializer.SMOKING_RECIPE, new ResourceLocation("minecraft", "textures/gui/container/smoker.png"));
        map.put(IRecipeSerializer.CAMPFIRE_COOKING_RECIPE, Utils.location("textures/campfire.png"));
    });
    public static final ITextComponent TITLE_SMELT_TIME = Utils.translate(null, "smelting.slot.time");
    public static final ITextComponent TITLE_EXPERIENCE = Utils.translate(null, "smelting.slot.experience");

    private final int defaultTime;

    public CookingBuilder(IRecipeSerializer<?> type, ItemStack iconStack, int defaultTime) { this(type, iconStack, defaultTime, 1); }

    public CookingBuilder(IRecipeSerializer<?> type, ItemStack iconStack, int defaultTime, int slotInY)
    {
        super(type, "minecraft", iconStack, buildSlotMap(slotInY), TEXTURES.get(type), 55, 16, 82, 54, true);
        this.defaultTime = defaultTime;
    }

    @Override
    protected void validate(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents) { }

    @Override
    protected final void build(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String recipeName, ICriterionInstance criterion, String criterionName)
    {
        Ingredient input = getContentAsIngredient(contents.get("in").getSecond(), true);
        Item output = getItemContent(contents.get("out").getSecond()).getItem();

        int cookingTime = getIntegerContent(contents.get("time").getSecond());
        float experience = getFloatContent(contents.get("experience").getSecond());

        if (cookingTime == 0)
        {
            //Must be set to default cooking time because the recipe serializer only sets the default
            //when the time entry is missing in the JSON file
            cookingTime = defaultTime;
        }

        CookingRecipeBuilder builder = CookingRecipeBuilder.cooking(input, output, experience, cookingTime, (CookingRecipeSerializer<?>) getType())
                .unlockedBy(criterionName, criterion);

        Exporter.exportRecipe(builder::save, builder::save, recipeName);
    }



    private static Map<String, RecipeSlot<?>> buildSlotMap(int slotInY)
    {
        Map<String, RecipeSlot<?>> slots = new HashMap<>();

        slots.put("in", new ItemSlot("in", 1, slotInY, false, true, ItemSlot.SINGLE_ITEM));
        slots.put("out", new ItemSlot("out", 61, 19, false, false, ItemSlot.SINGLE_ITEM));

        slots.put("time", new IntegerSlot("time", true, TITLE_SMELT_TIME));
        slots.put("experience", new FloatSlot("experience", true, TITLE_EXPERIENCE));

        return slots;
    }
}