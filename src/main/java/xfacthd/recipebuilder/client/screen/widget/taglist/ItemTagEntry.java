package xfacthd.recipebuilder.client.screen.widget.taglist;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemTagEntry extends AbstractTagEntry
{
    private final ItemStack item;

    public ItemTagEntry(String itemName)
    {
        super(itemName, getTranslation(itemName), 18);
        this.item = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)));
    }

    @Override
    public void render(MatrixStack mstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
    {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(item, left + 2, top + 7);
        super.render(mstack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTicks);
    }



    private static ITextComponent getTranslation(String name)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return item.getName(item.getDefaultInstance());
    }
}