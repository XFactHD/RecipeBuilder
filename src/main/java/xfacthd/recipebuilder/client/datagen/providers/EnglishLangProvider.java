package xfacthd.recipebuilder.client.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.recipebuilder.RecipeBuilder;
import xfacthd.recipebuilder.client.RBClient;
import xfacthd.recipebuilder.client.builders.extendedcrafting.CombinationRecipeBuilder;
import xfacthd.recipebuilder.client.builders.extendedcrafting.QuantumCompressorBuilder;
import xfacthd.recipebuilder.client.compat.ExtendedCraftingCompat;
import xfacthd.recipebuilder.client.data.Condition;
import xfacthd.recipebuilder.client.builders.vanilla.*;
import xfacthd.recipebuilder.client.screen.*;
import xfacthd.recipebuilder.client.screen.edit.EditItemSlotScreen;
import xfacthd.recipebuilder.client.screen.widget.taglist.AbstractTagEntry;
import xfacthd.recipebuilder.common.container.RecipeBuilderContainer;
import xfacthd.recipebuilder.client.data.AbstractBuilder;
import xfacthd.recipebuilder.common.container.TagBuilderContainer;
import xfacthd.recipebuilder.common.util.Utils;

public class EnglishLangProvider extends LanguageProvider
{
    public EnglishLangProvider(DataGenerator gen) { super(gen, RecipeBuilder.MOD_ID, "en_us"); }

    @Override
    protected void addTranslations()
    {
        add(RecipeBuilderContainer.TITLE, "Recipe Builder");
        add(RecipeBuilderScreen.TITLE_BTN_BUILD, "Build");
        add(RecipeBuilderScreen.TITLE_BTN_RESET, "Reset slots");
        add(RecipeBuilderScreen.TITLE_TEXT_RECIPENAME, "Name (Optional)");
        add(RecipeBuilderScreen.TITLE_BTN_CONDITION, "Set condition");
        add(RecipeBuilderScreen.TITLE_BTN_PARAMETERS, "Edit parameters");
        add(RecipeBuilderScreen.MSG_NAME_INVALID, "The recipe name is invalid: ");
        add(RecipeBuilderScreen.MSG_SUCCESS, "The recipe has been exported successfully. You can find the generated datapack here: ");
        add(RecipeBuilderScreen.MSG_SUCCESS_LOCAL, "The recipe has also been placed in a generated datapack in the running World. To activate the new recipe, run the \"/reload\" command.");
        add(RecipeBuilderScreen.HOVER_MSG_CLICK_TO_OPEN, "Click to open the datapack in the file explorer");
        add(RecipeBuilderScreen.FILTER_ALL, "[All]");
        add(TagBuilderContainer.TITLE, "Tag Builder");
        add(TagBuilderScreen.TITLE_TEXT_TAGNAME, "Tag name");
        add(TagBuilderScreen.TITLE_TEXT_ADD, "Entry name");
        add(TagBuilderScreen.TITLE_TAG_TYPE, "Tag type");
        add(TagBuilderScreen.TITLE_TAG_REPLACE, "Replace existing");
        add(TagBuilderScreen.TITLE_BTN_ADD_ENTRY, "Add entry");
        add(TagBuilderScreen.TITLE_BTN_OPTIONAL, "Make optional");
        add(TagBuilderScreen.TITLE_BTN_NON_OPTIONAL, "Make non-optional");
        add(TagBuilderScreen.TITLE_BTN_REMOVE_ENTRY, "Remove entry");
        add(TagBuilderScreen.TITLE_BTN_RESET, "Clear inputs");
        add(TagBuilderScreen.MSG_NAME_EMPTY, "The tag name must not be empty");
        add(TagBuilderScreen.MSG_NAME_INVALID, "The tag name is invalid: ");
        add(TagBuilderScreen.MSG_ENTRY_NAME_EMPTY, "The entry name must not be empty");
        add(TagBuilderScreen.MSG_ENTRY_UNKNOWN, "Unknown registry entry: ");
        add(TagBuilderScreen.MSG_ENTRY_EXISTS, "This entry already exists");
        add(TagBuilderScreen.MSG_SUCCESS, "The tag has been exported successfully. You can find the generated datapack here: ");
        add(TagBuilderScreen.MSG_SUCCESS_LOCAL, "The tag has also been placed in a generated datapack in the running World. To activate the new tag, run the \"/reload\" command.");
        add(AbstractTagEntry.TITLE_OPTIONAL, "Optional");
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
        add(AbstractBuilder.MSG_NO_UNLOCK, "No advancement criterion set");
        add(AbstractBuilder.MSG_INPUT_EMPTY, "The input may not be empty");
        add(AbstractBuilder.MSG_NON_OPT_EMPTY, "A non-optional Slot is empty: ");
        add(Condition.HAS_ITEM.getName(), "Has item (item-based)");
        add(Condition.HAS_ITEM_TAG.getName(), "Has item (tag-based)");
        add(Condition.ENTERED_BLOCK.getName(), "Entered block");
        add(Condition.MSG_NO_SUCH_TAG, "Unknown tag: ");
        add(Condition.MSG_NOT_A_BLOCK, "Not a block: ");
        add(CookingBuilder.TITLE_SMELT_TIME, "Time (Optional)");
        add(CookingBuilder.TITLE_EXPERIENCE, "XP (Optional)");
        add(RBClient.KEY_BIND_OPEN_RECIPE_BUILDER.get().getCategory(), "RecipeBuilder");
        add(RBClient.KEY_BIND_OPEN_RECIPE_BUILDER.get().getName(), "Open recipe builder");
        add(RBClient.KEY_BIND_OPEN_TAG_BUILDER.get().getName(), "Open tag builder");
        add(QuantumCompressorBuilder.TITLE_INPUT_COUNT, "Input count");
        add(QuantumCompressorBuilder.TITLE_POWER_COST, "Power cost");
        add(QuantumCompressorBuilder.TITLE_POWER_RATE, "Power rate");
        add(QuantumCompressorBuilder.MSG_INVALID_INPUT_COUNT, "Invalid input count, must be positiv!");
        add(QuantumCompressorBuilder.MSG_INVALID_POWER_COST, "Invalid power cost, must be positive and higher than the power rate!");
        add(QuantumCompressorBuilder.MSG_INVALID_POWER_RATE, "Invalid power rate, must be positive!");
        add(CombinationRecipeBuilder.TITLE_POWER_COST, "Power cost");
        add(CombinationRecipeBuilder.TITLE_POWER_RATE, "Power rate");
        add(CombinationRecipeBuilder.MSG_INVALID_POWER_COST, "Invalid power cost, must be positive and higher than the power rate!");
        add(CombinationRecipeBuilder.MSG_INVALID_POWER_RATE, "Invalid power rate, must be positive!");

        //Vanilla serializers
        add(IRecipeSerializer.SHAPED_RECIPE, "Shaped Crafting");
        add(IRecipeSerializer.SHAPELESS_RECIPE, "Shapeless Crafting");
        add(IRecipeSerializer.SMELTING_RECIPE, "Smelting");
        add(IRecipeSerializer.BLASTING_RECIPE, "Blasting");
        add(IRecipeSerializer.SMOKING_RECIPE, "Smoking");
        add(IRecipeSerializer.CAMPFIRE_COOKING_RECIPE, "Campfire Cooking");
        add(IRecipeSerializer.SMITHING, "Smithing");
        add(IRecipeSerializer.STONECUTTER, "Stonecutting");

        //Extended Crafting serializers
        IRecipeSerializer<?> extendedCraftingShaped = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "shaped_table"));
        add(extendedCraftingShaped, "tier_1", "Basic Table (Shaped)");
        add(extendedCraftingShaped, "tier_2", "Advanced Table (Shaped)");
        add(extendedCraftingShaped, "tier_3", "Elite Table (Shaped)");
        add(extendedCraftingShaped, "tier_4", "Ultimate Table (Shaped)");

        IRecipeSerializer<?> extendedCraftingShapeless = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "shapeless_table"));
        add(extendedCraftingShapeless, "tier_1", "Basic Table (Shapeless)");
        add(extendedCraftingShapeless, "tier_2", "Advanced Table (Shapeless)");
        add(extendedCraftingShapeless, "tier_3", "Elite Table (Shapeless)");
        add(extendedCraftingShapeless, "tier_4", "Ultimate Table (Shapeless)");

        add(ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "shaped_ender_crafter")), "Ender Crafter (Shaped)");
        add(ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "shapeless_ender_crafter")), "Ender Crafter (Shapeless)");

        add(ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "compressor")), "Quantum Compression");

        add(ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "combination")), "Combination Crafting");

        //Tag Types
        add(ForgeRegistries.BLOCKS, "Blocks");
        add(ForgeRegistries.ITEMS, "Items");
        add(ForgeRegistries.FLUIDS, "Fluids");
        add(ForgeRegistries.ENTITIES, "EntityTypes");
        add(ForgeRegistries.TILE_ENTITIES, "BlockEntityTypes");
        add(ForgeRegistries.POTIONS, "Effects");
        add(ForgeRegistries.ENCHANTMENTS, "Enchantments");
    }

    private void add(IRecipeSerializer<?> serializer, String value) { add(serializer, null, value); }

    private void add(IRecipeSerializer<?> serializer, String typeSuffix, String value) { add(AbstractBuilder.getTypeName(serializer, typeSuffix), value); }

    private void add(IForgeRegistry<?> registry, String value) { add(Utils.translate("tag_type", registry.getRegistryName().toString()), value); }

    private void add(ITextComponent component, String value) { add(component.getString(), value); }
}