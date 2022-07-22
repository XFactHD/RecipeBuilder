package xfacthd.recipebuilder.client.data;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final Map<String, RecipeSlot<?, ?>> slots;
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
            Map<String, RecipeSlot<?, ?>> slots,
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
            RecipeSerializer<?> type,
            String typeSuffix,
            String modid,
            ItemStack iconStack,
            Map<String, RecipeSlot<?, ?>> slots,
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

    public final RecipeSerializer<?> getType() { return type; }

    public final Component getTypeName() { return typeName; }

    public final String getModid() { return modid; }

    public final Component getModName() { return modName; }

    public final ItemStack getIcon() { return iconStack; }

    public final Map<String, RecipeSlot<?, ?>> getSlots() { return slots; }

    public final ResourceLocation getTexture() { return texture; }

    public final int getTexX() { return texX; }

    public final int getTexY() { return texY; }

    public final int getTexWidth() { return texWidth; }

    public final int getTexHeight() { return texHeight; }

    public final boolean needsAdvancement() { return needAdvancement; }

    public final Component buildRecipe(Map<RecipeSlot<?, ?>, SlotContent<?>> contents, String recipeName, CriterionTriggerInstance criterion, String criterionName)
    {
        Map<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> byName = new HashMap<>();

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

    public void drawBackground(Screen screen, PoseStack pstack, int builderX, int builderY)
    {
        RenderSystem.setShaderTexture(0, getTexture());
        screen.blit(pstack, builderX, builderY, getTexX(), getTexY(), getTexWidth(), getTexHeight());
    }

    protected abstract void validate(Map<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> contents);

    protected abstract void build(Map<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> contents, String recipeName, CriterionTriggerInstance criterion, String criterionName);



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

    protected final <T> TagKey<T> getTagContent(SlotContent<?> content)
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

    public static void checkAnyFilledExcept(Map<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> contents, String... except)
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

    protected final List<String> parseTableGridLines(int size, Map<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> contents, Map<Item, Character> itemKeys, Map<TagKey<Item>, Character> tagKeys)
    {
        char[][] grid = new char[size][size];
        Arrays.stream(grid).forEach(arr -> Arrays.fill(arr, ' '));

        char lastChar = 'A';
        for (Map.Entry<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> entry : contents.entrySet())
        {
            String name = entry.getKey();
            if (name.equals("out"))
            {
                continue;
            }

            Pair<RecipeSlot<?, ?>, SlotContent<?>> pair = entry.getValue();

            ItemStack stack = getItemContent(pair.getSecond());
            if (stack.isEmpty())
            {
                continue;
            }

            int line = Integer.parseInt(name.substring(0, 1));
            int col = Integer.parseInt(name.substring(1, 2));

            if (pair.getSecond().shouldUseTag())
            {
                TagKey<Item> tag = getTagContent(pair.getSecond());
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

    public static Component getTypeName(RecipeSerializer<?> type, String suffix)
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