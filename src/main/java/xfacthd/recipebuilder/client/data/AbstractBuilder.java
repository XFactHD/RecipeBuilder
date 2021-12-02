package xfacthd.recipebuilder.client.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.recipebuilder.client.data.slots.*;
import xfacthd.recipebuilder.client.util.BuilderException;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.*;

public abstract class AbstractBuilder
{
    public static final MutableComponent MSG_NON_OPT_EMPTY = Utils.translate("msg", "non_opt_empty");
    public static final MutableComponent MSG_NO_UNLOCK = Utils.translate("msg", "no_unlock");
    public static final Component MSG_INPUT_EMPTY = Utils.translate("msg", "input_empty");

    private final RecipeSerializer<?> type;
    private final String modid;
    private final Component typeName;
    private final Component modName;
    private final ItemStack iconStack;
    private final Map<String, RecipeSlot<?>> slots;
    private final ResourceLocation texture;
    private final int texX;
    private final int texY;
    private final int texWidth;
    private final int texHeight;
    private final boolean needAdvancement;

    protected AbstractBuilder(
            RecipeSerializer<?> type,
            String modid,
            ItemStack iconStack,
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
        this.iconStack = iconStack;
        this.slots = slots;
        this.texture = texture;
        this.texX = texX;
        this.texY = texY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.needAdvancement = needAdvancement;
    }

    public final RecipeSerializer<?> getType() { return type; }

    public final Component getTypeName() { return typeName; }

    public String getModid() { return modid; }

    public final Component getModName() { return modName; }

    public final ItemStack getIcon() { return iconStack; }

    public final Map<String, RecipeSlot<?>> getSlots() { return slots; }

    public final ResourceLocation getTexture() { return texture; }

    public final int getTexX() { return texX; }

    public final int getTexY() { return texY; }

    public final int getTexWidth() { return texWidth; }

    public final int getTexHeight() { return texHeight; }

    public final boolean needsAdvancement() { return needAdvancement; }

    public final Component buildRecipe(Map<RecipeSlot<?>, SlotContent<?>> contents, String recipeName, CriterionTriggerInstance criterion, String criterionName)
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

    protected abstract void build(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String recipeName, CriterionTriggerInstance criterion, String criterionName);



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

    protected final <T> Tag<T> getTagContent(SlotContent<?> content)
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

    public static Component getTypeName(RecipeSerializer<?> type)
    {
        ResourceLocation typeKey = ForgeRegistries.RECIPE_SERIALIZERS.getKey(type);
        if (typeKey == null) { throw new IllegalArgumentException("Recipe type has no name!"); }
        return Utils.translate("typename", typeKey.getPath());
    }

    public static Component getModName(String modid)
    {
        IModFileInfo file = ModList.get().getModFileById(modid);
        Optional<IModInfo> modInfo = file.getMods().stream().filter(mod -> mod.getModId().equals(modid)).findFirst();
        if (modInfo.isEmpty())
        {
            throw new IllegalArgumentException("Unable to get mod from ID: " + modid);
        }

        return new TextComponent(modInfo.get().getDisplayName());
    }
}