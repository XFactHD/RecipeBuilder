package xfacthd.recipebuilder.client.data.slots;

import net.minecraft.network.chat.Component;
import xfacthd.recipebuilder.client.data.RecipeSlot;
import xfacthd.recipebuilder.client.data.SlotContent;

public abstract class NumberSlot<T extends SlotContent<?>> extends RecipeSlot<T>
{
    private final Component title;

    protected NumberSlot(String name, boolean optional, Component title)
    {
        super(name, 0, 0, 0, 0, optional);
        this.title = title;
    }

    public Component getTitle() { return title; }
}