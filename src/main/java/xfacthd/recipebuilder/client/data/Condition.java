package xfacthd.recipebuilder.client.data;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.block.Block;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.*;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import xfacthd.recipebuilder.common.util.Utils;

public abstract class Condition
{
    public static final IFormattableTextComponent MSG_NO_SUCH_TAG = Utils.translate("msg", "condition.no_such_tag");
    public static final IFormattableTextComponent MSG_NOT_A_BLOCK = Utils.translate("msg", "condition.not_a_block");

    public static final Condition HAS_ITEM = new Condition(Utils.translate(null, "condition.has_item"))
    {
        @Override
        public boolean needsItem() { return true; }

        @Override
        public boolean needsTag() { return false; }

        @Override
        public ITextComponent validateStack(ItemStack stack) { return null; }

        @Override
        public ICriterionInstance toCriterion(ItemStack stack, String tag)
        {
            return RecipeProvider.has(stack.getItem());
        }

        @Override
        public String buildName(ItemStack stack, String tag)
        {
            //noinspection ConstantConditions
            return "has_" + stack.getItem().getRegistryName().getPath();
        }
    };

    public static final Condition HAS_ITEM_TAG = new Condition(Utils.translate(null, "condition.has_item_tag"))
    {
        private final ResourceLocation REGISTRY = new ResourceLocation("minecraft", "item");

        @Override
        public boolean needsItem() { return false; }

        @Override
        public boolean needsTag() { return true; }

        @Override
        public ITextComponent validateTag(String tagName)
        {
            //noinspection ConstantConditions
            ITag.INamedTag<?> tag = TagRegistryManager.get(REGISTRY).bind(tagName);
            try
            {
                tag.getValues();
                return null;
            }
            catch (Exception e)
            {
                return MSG_NO_SUCH_TAG.append(tagName);
            }
        }

        @Override
        public ICriterionInstance toCriterion(ItemStack stack, String tagName)
        {
            //noinspection ConstantConditions
            ITag.INamedTag<?> tag = TagRegistryManager.get(REGISTRY).bind(tagName);
            //noinspection unchecked
            return RecipeProvider.has((ITag<Item>) tag);
        }

        @Override
        public String buildName(ItemStack stack, String tag)
        {
            int colon = tag.indexOf(":");
            return "has_" + (colon != -1 ? tag.substring(colon + 1) : tag);
        }
    };

    public static final Condition ENTERED_BLOCK = new Condition(Utils.translate(null, "condition.entered_block"))
    {
        @Override
        public boolean needsItem() { return true; }

        @Override
        public boolean needsTag() { return false; }

        @Override
        public boolean acceptsItem(ItemStack stack) { return stack.getItem() instanceof BlockItem; }

        @Override
        public ITextComponent validateStack(ItemStack stack)
        {
            if (!acceptsItem(stack))
            {
                return MSG_NOT_A_BLOCK.append(stack.getDisplayName());
            }
            return null;
        }

        @Override
        public ICriterionInstance toCriterion(ItemStack stack, String tag)
        {
            Block block = ((BlockItem) stack.getItem()).getBlock();
            return RecipeProvider.insideOf(block);
        }

        @Override
        public String buildName(ItemStack stack, String tag)
        {
            //noinspection ConstantConditions
            return "in_" + stack.getItem().getRegistryName().getPath();
        }
    };



    private final ITextComponent name;

    public Condition(ITextComponent name) { this.name = name; }

    public ITextComponent getName() { return name; }

    public abstract boolean needsItem();

    public abstract boolean needsTag();

    public boolean acceptsItem(ItemStack stack) { throw new UnsupportedOperationException(); }

    public ITextComponent validateStack(ItemStack stack) { throw new UnsupportedOperationException(); }

    public ITextComponent validateTag(String tag) { throw new UnsupportedOperationException(); }

    public abstract ICriterionInstance toCriterion(ItemStack stack, String tag);

    public abstract String buildName(ItemStack stack, String tag);
}