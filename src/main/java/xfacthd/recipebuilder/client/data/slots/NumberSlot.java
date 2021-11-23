package xfacthd.recipebuilder.client.data.slots;

import net.minecraft.util.text.ITextComponent;
import xfacthd.recipebuilder.client.data.RecipeSlot;
import xfacthd.recipebuilder.client.data.SlotContent;

public abstract class NumberSlot<T extends SlotContent<?>> extends RecipeSlot<T>
{
    private final ITextComponent title;

    protected NumberSlot(String name, boolean optional, ITextComponent title)
    {
        super(name, 0, 0, 0, 0, optional);
        this.title = title;
    }

    public ITextComponent getTitle() { return title; }
}