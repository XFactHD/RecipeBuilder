package xfacthd.recipebuilder.client.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.data.LanguageProvider;
import xfacthd.recipebuilder.RecipeBuilder;
import xfacthd.recipebuilder.client.data.Condition;
import xfacthd.recipebuilder.client.data.builders.*;
import xfacthd.recipebuilder.client.screen.*;
import xfacthd.recipebuilder.client.screen.edit.EditItemSlotScreen;
import xfacthd.recipebuilder.common.container.BuilderContainer;
import xfacthd.recipebuilder.client.data.BuilderType;

public class EnglishLangProvider extends LanguageProvider
{
    public EnglishLangProvider(DataGenerator gen) { super(gen, RecipeBuilder.MOD_ID, "en_us"); }

    @Override
    protected void addTranslations()
    {
        add(BuilderContainer.TITLE, "Recipe Builder");
        add(BuilderScreen.TITLE_BTN_BUILD, "Build");
        add(BuilderScreen.TITLE_BTN_RESET, "Reset slots");
        add(BuilderScreen.TITLE_TEXT_RECIPENAME, "Name (Optional)");
        add(BuilderScreen.TITLE_BTN_CONDITION, "Set condition");
        add(BuilderScreen.TITLE_BTN_PARAMETERS, "Edit parameters");
        add(BuilderScreen.MSG_SUCCESS, "The recipe has been exported successfully. You can find the generated datapack here: ");
        add(BuilderScreen.MSG_SUCCESS_LOCAL, "The recipe has also been placed in a generated datapack in the running World. To activate the new recipe, run the \"/reload\" command.");
        add(BuilderScreen.HOVER_MSG_CLICK_TO_OPEN, "Click to open the datapack in the file explorer");
        add(BuilderScreen.FILTER_ALL, "[All]");
        add(MessageScreen.INFO_TITLE, "Builder Info");
        add(MessageScreen.ERROR_TITLE, "Builder Error");
        add(MessageScreen.TITLE_BTN_OK, "Ok");
        add(SelectConditionScreen.TITLE, "Select unlock condition");
        add(SelectConditionScreen.TITLE_SELECT, "-- Please select --");
        add(SelectConditionScreen.TITLE_STACK, "Target item: ");
        add(SelectConditionScreen.TITLE_TAG, "Tag name");
        add(SelectConditionScreen.MSG_NO_CONDITION, "No condition selected");
        add(SelectConditionScreen.MSG_MISSING_DATA, "Missing required data");
        add(EditParametersScreen.TITLE, "Edit parameters");
        add(EditSlotScreen.TITLE_USE_TAG, "Use tag");
        add(EditSlotScreen.MSG_NO_TAG_SELECTED, "No tag selected");
        add(EditItemSlotScreen.TITLE, "Edit item");
        add(BuilderType.MSG_NO_UNLOCK, "No advancement criterion set");
        add(BuilderType.MSG_INPUT_EMPTY, "The input may not be empty");
        add(BuilderType.MSG_NON_OPT_EMPTY, "A non-optional Slot is empty: ");
        add(Condition.HAS_ITEM.getName(), "Has item (item-based)");
        add(Condition.HAS_ITEM_TAG.getName(), "Has item (tag-based)");
        add(Condition.ENTERED_BLOCK.getName(), "Entered block");
        add(Condition.MSG_NO_SUCH_TAG, "Unknown tag: ");
        add(Condition.MSG_NOT_A_BLOCK, "Not a block: ");
        add(CookingBuilder.TITLE_SMELT_TIME, "Time (Optional)");
        add(CookingBuilder.TITLE_EXPERIENCE, "XP (Optional)");

        add(IRecipeSerializer.SHAPED_RECIPE, "Shaped Crafting");
        add(IRecipeSerializer.SHAPELESS_RECIPE, "Shapeless Crafting");
        add(IRecipeSerializer.SMELTING_RECIPE, "Smelting");
        add(IRecipeSerializer.BLASTING_RECIPE, "Blasting");
        add(IRecipeSerializer.SMOKING_RECIPE, "Smoking");
        add(IRecipeSerializer.CAMPFIRE_COOKING_RECIPE, "Campfire Cooking");
        add(IRecipeSerializer.SMITHING, "Smithing");
        add(IRecipeSerializer.STONECUTTER, "Stonecutting");
    }

    private void add(IRecipeSerializer<?> serializer, String value) { add(BuilderType.getTypeName(serializer), value); }

    private void add(ITextComponent component, String value) { add(component.getString(), value); }
}