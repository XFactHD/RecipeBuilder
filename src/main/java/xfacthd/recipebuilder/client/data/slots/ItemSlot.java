package xfacthd.recipebuilder.client.data.slots;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import xfacthd.recipebuilder.client.data.RecipeSlot;
import xfacthd.recipebuilder.client.data.SlotContent;
import xfacthd.recipebuilder.client.screen.edit.EditItemSlotScreen;

import java.util.function.Function;

public class ItemSlot extends RecipeSlot<ItemSlot.ItemContent>
{
    public static final Function<ItemStack, Integer> SINGLE_ITEM = stack -> 1;

    private final Function<ItemStack, Integer> countModifier;

    public ItemSlot(String name, int x, int y, boolean optional, boolean allowTags) { this(name, x, y, optional, allowTags, ItemStack::getCount); }

    public ItemSlot(String name, int x, int y, boolean optional, boolean allowTags, Function<ItemStack, Integer> countModifier)
    {
        super(name, x, y, 16, 16, optional, true, "item", allowTags);
        this.countModifier = countModifier;
    }

    @Override
    public boolean canEdit(ItemContent content) { return allowsTags() && !content.isEmpty() && !content.getContent().getItem().getTags().isEmpty(); }

    @Override
    public Screen requestEdit(ItemContent content) { return new EditItemSlotScreen(this, content); }

    @Override
    public ItemContent newEmptyContent() { return new ItemContent(ItemStack.EMPTY); }

    @Override
    public void renderContent(Screen screen, ItemContent content, MatrixStack mstack, int slotX, int slotY, int blitBase, FontRenderer font)
    {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        screen.setBlitOffset(blitBase + 100);
        itemRenderer.blitOffset = blitBase + 100.0F;

        RenderSystem.enableDepthTest();
        //noinspection ConstantConditions
        itemRenderer.renderAndDecorateItem(Minecraft.getInstance().player, content.getContent(), slotX, slotY);
        itemRenderer.renderGuiItemDecorations(font, content.getContent(), slotX, slotY, null);

        itemRenderer.blitOffset = 0.0F;
        screen.setBlitOffset(0);
    }

    @Override
    public void renderTooltip(Screen screen, ItemContent content, MatrixStack mstack, int mouseX, int mouseY, FontRenderer font)
    {
        ItemStack stack = content.getContent();

        FontRenderer stackFont = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(stack);
        screen.renderWrappedToolTip(mstack, screen.getTooltipFromItem(stack), mouseX, mouseY, (stackFont == null ? font : stackFont));
        net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
    }

    public class ItemContent extends SlotContent<ItemStack>
    {
        public ItemContent(ItemStack content) { super(content); }

        @Override
        public void acceptItem(ItemStack stack)
        {
            ItemStack copy = stack.copy();
            copy.setCount(countModifier.apply(stack));
            setContent(copy);
        }

        @Override
        public void clear() { setContent(ItemStack.EMPTY); }

        @Override
        public boolean isEmpty() { return content.isEmpty(); }
    }
}