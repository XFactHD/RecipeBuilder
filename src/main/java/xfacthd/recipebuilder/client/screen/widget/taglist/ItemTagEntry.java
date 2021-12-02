package xfacthd.recipebuilder.client.screen.widget.taglist;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    public void render(PoseStack pstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
    {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(item, left + 2, top + 7);
        super.render(pstack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTicks);
    }



    private static Component getTranslation(String name)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return item.getName(item.getDefaultInstance());
    }
}