package xfacthd.recipebuilder.client.data;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.StaticTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import xfacthd.recipebuilder.common.util.Utils;

public abstract class Condition
{
    public static final MutableComponent MSG_NO_SUCH_TAG = Utils.translate("msg", "condition.no_such_tag");
    public static final MutableComponent MSG_NOT_A_BLOCK = Utils.translate("msg", "condition.not_a_block");

    public static final Condition HAS_ITEM = new Condition(Utils.translate(null, "condition.has_item"))
    {
        @Override
        public boolean needsItem() { return true; }

        @Override
        public boolean needsTag() { return false; }

        @Override
        public Component validateStack(ItemStack stack) { return null; }

        @Override
        public CriterionTriggerInstance toCriterion(ItemStack stack, String tag)
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
        public Component validateTag(String tagName)
        {
            //noinspection ConstantConditions
            Tag.Named<?> tag = StaticTags.get(REGISTRY).bind(tagName);
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
        public CriterionTriggerInstance toCriterion(ItemStack stack, String tagName)
        {
            //noinspection ConstantConditions
            Tag.Named<?> tag = StaticTags.get(REGISTRY).bind(tagName);
            //noinspection unchecked
            return RecipeProvider.has((Tag<Item>) tag);
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
        public Component validateStack(ItemStack stack)
        {
            if (!acceptsItem(stack))
            {
                return MSG_NOT_A_BLOCK.append(stack.getDisplayName());
            }
            return null;
        }

        @Override
        public CriterionTriggerInstance toCriterion(ItemStack stack, String tag)
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



    private final Component name;

    public Condition(Component name) { this.name = name; }

    public Component getName() { return name; }

    public abstract boolean needsItem();

    public abstract boolean needsTag();

    public boolean acceptsItem(ItemStack stack) { throw new UnsupportedOperationException(); }

    public Component validateStack(ItemStack stack) { throw new UnsupportedOperationException(); }

    public Component validateTag(String tag) { throw new UnsupportedOperationException(); }

    public abstract CriterionTriggerInstance toCriterion(ItemStack stack, String tag);

    public abstract String buildName(ItemStack stack, String tag);
}