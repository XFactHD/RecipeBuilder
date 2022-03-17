package xfacthd.recipebuilder.client.builders.extendedcrafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.recipebuilder.client.compat.ExtendedCraftingCompat;
import xfacthd.recipebuilder.client.data.*;

import java.util.*;

public class ShapelessTableBuilder extends AbstractBuilder
{
    private final int tier;

    public ShapelessTableBuilder(RecipeSerializer<?> type, String itemName, String textureName, int tier, int texX, int texY, int texWidth, int texHeight)
    {
        //noinspection ConstantConditions
        super(
                type,
                tier > 0 ? "tier_" + tier : null,
                ExtendedCraftingCompat.MOD_ID,
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, itemName)).getDefaultInstance(),
                ShapedTableBuilder.buildSlotMap(tier),
                new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "textures/gui/" + textureName + ".png"),
                texX, texY,
                texWidth, texHeight,
                false
        );

        this.tier = tier;
    }

    @Override
    public void drawBackground(Screen screen, PoseStack pstack, int builderX, int builderY)
    {
        if (tier == 4)
        {
            RenderSystem.setShaderTexture(0, getTexture());
            GuiComponent.blit(pstack, builderX, builderY, screen.getBlitOffset(), getTexX(), getTexY(), getTexWidth(), getTexHeight(), 512, 512);
        }
        else
        {
            super.drawBackground(screen, pstack, builderX, builderY);
        }
    }

    @Override
    protected void validate(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents)
    {
        checkAnyFilledExcept(contents, "out");
    }

    @Override
    protected void build(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String recipeName, CriterionTriggerInstance criterion, String criterionName)
    {
        ItemStack out = getItemContent(contents.get("out").getSecond());

        List<Ingredient> ingredients = new ArrayList<>();

        for (Pair<RecipeSlot<?>, SlotContent<?>> pair : contents.values())
        {
            if (pair.getFirst().getName().equals("out"))
            {
                continue;
            }

            ItemStack stack = getItemContent(pair.getSecond());
            if (!stack.isEmpty())
            {
                if (pair.getSecond().shouldUseTag())
                {
                    ingredients.add(Ingredient.of(getTagContent(pair.getSecond())));
                }
                else
                {
                    ingredients.add(Ingredient.of(stack.getItem()));
                }
            }
        }

        Exporter.exportRecipe(
                cons -> cons.accept(new ShapelessTableResult(out.getItem().getRegistryName(), tier, out, ingredients)),
                (cons, name) -> cons.accept(new ShapelessTableResult(new ResourceLocation(name), tier, out, ingredients)),
                recipeName
        );
    }



    private class ShapelessTableResult implements FinishedRecipe
    {
        private final ResourceLocation id;
        private final int tier;
        private final Item result;
        private final int count;
        private final List<Ingredient> ingredients;

        private ShapelessTableResult(ResourceLocation id, int tier, ItemStack result, List<Ingredient> ingredients)
        {
            this.id = id;
            this.tier = tier;
            this.result = result.getItem();
            this.count = result.getCount();
            this.ingredients = ingredients;
        }

        public void serializeRecipeData(JsonObject json)
        {
            JsonArray ingredientArray = new JsonArray();
            ingredients.forEach(ing -> ingredientArray.add(ing.toJson()));
            json.add("ingredients", ingredientArray);

            JsonObject resultObj = new JsonObject();
            //noinspection ConstantConditions
            resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
            if (count > 1)
            {
                resultObj.addProperty("count", count);
            }
            json.add("result", resultObj);

            if (tier > 0)
            {
                json.addProperty("tier", tier);
            }
        }

        public RecipeSerializer<?> getType() { return ShapelessTableBuilder.this.getType(); }

        public ResourceLocation getId() { return id; }

        public JsonObject serializeAdvancement() { return null; }

        public ResourceLocation getAdvancementId() { return null; }
    }
}
