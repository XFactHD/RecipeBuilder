package xfacthd.recipebuilder.client.data;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
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
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractBuilder
{
    public static final IFormattableTextComponent MSG_NON_OPT_EMPTY = Utils.translate("msg", "non_opt_empty");
    public static final IFormattableTextComponent MSG_NO_UNLOCK = Utils.translate("msg", "no_unlock");
    public static final ITextComponent MSG_INPUT_EMPTY = Utils.translate("msg", "input_empty");

    private final IRecipeSerializer<?> type;
    private final String modid;
    private final ITextComponent typeName;
    private final ITextComponent modName;
    private final ItemStack iconStack;
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
        this(type, null, modid, iconStack, slots, texture, texX, texY, texWidth, texHeight, needAdvancement);
    }

    protected AbstractBuilder(
            IRecipeSerializer<?> type,
            String typeSuffix,
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
        this.typeName = getTypeName(type, typeSuffix);
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

    public final IRecipeSerializer<?> getType() { return type; }

    public final ITextComponent getTypeName() { return typeName; }

    public final String getModid() { return modid; }

    public final ITextComponent getModName() { return modName; }

    public final ItemStack getIcon() { return iconStack; }

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

    public void drawBackground(Screen screen, MatrixStack mstack, int builderX, int builderY)
    {
        Minecraft.getInstance().textureManager.bind(getTexture());
        screen.blit(mstack, builderX, builderY, getTexX(), getTexY(), getTexWidth(), getTexHeight());
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

    public static void checkAnyFilledExcept(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String... except)
    {
        List<String> ignore = Arrays.asList(except);
        AtomicBoolean foundInput = new AtomicBoolean();

        contents.forEach((name, pair) ->
        {
            if (!ignore.contains(name))
            {
                if (!pair.getSecond().isEmpty())
                {
                    foundInput.set(true);
                }
            }
        });

        if (!foundInput.get())
        {
            throw new BuilderException(MSG_INPUT_EMPTY);
        }
    }

    protected final List<String> parseTableGridLines(int size, Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, Map<Item, Character> itemKeys, Map<ITag<Item>, Character> tagKeys)
    {
        char[][] grid = new char[size][size];
        Arrays.stream(grid).forEach(arr -> Arrays.fill(arr, ' '));

        char lastChar = 'A';
        for (Map.Entry<String, Pair<RecipeSlot<?>, SlotContent<?>>> entry : contents.entrySet())
        {
            String name = entry.getKey();
            if (name.equals("out"))
            {
                continue;
            }

            Pair<RecipeSlot<?>, SlotContent<?>> pair = entry.getValue();

            ItemStack stack = getItemContent(pair.getSecond());
            if (stack.isEmpty())
            {
                continue;
            }

            int line = Integer.parseInt(name.substring(0, 1));
            int col = Integer.parseInt(name.substring(1, 2));

            if (pair.getSecond().shouldUseTag())
            {
                ITag<Item> tag = getTagContent(pair.getSecond());
                if (!tagKeys.containsKey(tag))
                {
                    tagKeys.put(tag, lastChar);
                    lastChar++;
                }

                grid[line][col] = tagKeys.get(tag);
            }
            else
            {
                if (!itemKeys.containsKey(stack.getItem()))
                {
                    itemKeys.put(stack.getItem(), lastChar);
                    lastChar++;
                }

                grid[line][col] = itemKeys.get(stack.getItem());
            }
        }

        List<String> lines = new ArrayList<>();
        for (char[] gridLine : grid)
        {
            String line = String.valueOf(gridLine);
            if (!line.trim().isEmpty())
            {
                lines.add(line);
            }
        }
        return lines;
    }

    public static ITextComponent getTypeName(IRecipeSerializer<?> type, String suffix)
    {
        ResourceLocation typeKey = ForgeRegistries.RECIPE_SERIALIZERS.getKey(type);
        if (typeKey == null) { throw new IllegalArgumentException("Recipe type has no name!"); }

        String name = typeKey.getPath();
        if (suffix != null)
        {
            name += "_" + suffix;
        }
        return Utils.translate("typename", name);
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