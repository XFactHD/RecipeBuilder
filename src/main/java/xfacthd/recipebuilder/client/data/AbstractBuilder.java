package xfacthd.recipebuilder.client.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.recipebuilder.client.data.slots.*;
import xfacthd.recipebuilder.client.util.BuilderException;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.*;

public abstract class AbstractBuilder
{
    public static final IFormattableTextComponent MSG_NON_OPT_EMPTY = Utils.translate("msg", "non_opt_empty");
    public static final IFormattableTextComponent MSG_NO_UNLOCK = Utils.translate("msg", "no_unlock");
    public static final ITextComponent MSG_INPUT_EMPTY = Utils.translate("msg", "input_empty");

    private final IRecipeSerializer<?> type;
    private final String modid;
    private final ITextComponent typeName;
    private final ITextComponent modName;
    private final Map<String, RecipeSlot<?>> slots;
    private final ResourceLocation texture;
    private final int texX;
    private final int texY;
    private final int texWidth;
    private final int texHeight;
    private final boolean needAdvancement;

    protected AbstractBuilder(
            IRecipeSerializer<?> type,
            String modid,
            Map<String, RecipeSlot<?>> slots,
            ResourceLocation texture,
            int texX,
            int texY,
            int texWidth,
            int texHeight,
            boolean needAdvancement
    )
    {
        this.type = type;
        this.typeName = getTypeName(type);
        this.modid = modid;
        this.modName = getModName(modid);
        this.slots = slots;
        this.texture = texture;
        this.texX = texX;
        this.texY = texY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.needAdvancement = needAdvancement;
    }

    public final IRecipeSerializer<?> getType() { return type; }

    public final ITextComponent getTypeName() { return typeName; }

    public String getModid() { return modid; }

    public final ITextComponent getModName() { return modName; }

    public final Map<String, RecipeSlot<?>> getSlots() { return slots; }

    public final ResourceLocation getTexture() { return texture; }

    public final int getTexX() { return texX; }

    public final int getTexY() { return texY; }

    public final int getTexWidth() { return texWidth; }

    public final int getTexHeight() { return texHeight; }

    public final boolean needsAdvancement() { return needAdvancement; }

    public final ITextComponent buildRecipe(Map<RecipeSlot<?>, SlotContent<?>> contents, String recipeName, ICriterionInstance criterion, String criterionName)
    {
        Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> byName = new HashMap<>();

        try
        {
            if (needAdvancement && criterion == null)
            {
                throw new BuilderException(MSG_NO_UNLOCK);
            }

            contents.forEach((slot, content) ->
            {
                if (!slot.isOptional() && content.isEmpty())
                {
                    throw new BuilderException(MSG_NON_OPT_EMPTY.append(slot.getName()));
                }

                byName.put(slot.getName(), Pair.of(slot, content));
            });

            validate(byName);
            build(byName, recipeName, criterion, criterionName);
        }
        catch (BuilderException e)
        {
            return e.getComponent();
        }

        return null;
    }

    protected abstract void validate(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents);

    protected abstract void build(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String recipeName, ICriterionInstance criterion, String criterionName);



    protected final ItemStack getItemContent(SlotContent<?> content)
    {
        ItemSlot.ItemContent item = content.cast();
        return item.getContent();
    }

    protected final FluidStack getFluidContent(SlotContent<?> content)
    {
        FluidSlot.FluidContent fluid = content.cast();
        return fluid.getContent();
    }

    protected final <T> ITag<T> getTagContent(SlotContent<?> content)
    {
        SlotContent<T> tContent = content.cast();
        return tContent.getTag();
    }

    protected final Ingredient getContentAsIngredient(SlotContent<?> content, boolean canUseTag)
    {
        if (canUseTag && content.shouldUseTag())
        {
            return Ingredient.of(getTagContent(content));
        }
        else
        {
            return Ingredient.of(getItemContent(content));
        }
    }

    protected final int getIntegerContent(SlotContent<?> content)
    {
        IntegerSlot.IntegerContent intContent = content.cast();
        return intContent.getContent();
    }

    protected final float getFloatContent(SlotContent<?> content)
    {
        FloatSlot.FloatContent floatContent = content.cast();
        return floatContent.getContent();
    }

    public static ITextComponent getTypeName(IRecipeSerializer<?> type)
    {
        ResourceLocation typeKey = ForgeRegistries.RECIPE_SERIALIZERS.getKey(type);
        if (typeKey == null) { throw new IllegalArgumentException("Recipe type has no name!"); }
        return Utils.translate("typename", typeKey.getPath());
    }

    public static ITextComponent getModName(String modid)
    {
        ModFileInfo file = ModList.get().getModFileById(modid);
        Optional<IModInfo> modInfo = file.getMods().stream().filter(mod -> mod.getModId().equals(modid)).findFirst();
        if (!modInfo.isPresent())
        {
            throw new IllegalArgumentException("Unable to get mod from ID: " + modid);
        }

        return new StringTextComponent(modInfo.get().getDisplayName());
    }
}