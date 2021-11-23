package xfacthd.recipebuilder.client.data;

import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

public abstract class SlotContent<T>
{
    protected T content;
    protected boolean useTag = false;
    protected ITag<T> tag = null;

    protected SlotContent(T content) { this.content = content; }

    public void setContent(T content)
    {
        this.content = content;
        setTag(null);
    }

    public abstract void acceptItem(ItemStack stack);

    public abstract void clear();

    public final T getContent() { return content; }

    public void setTag(ITag<T> tag)
    {
        useTag = tag != null;
        this.tag = tag;
    }

    public final boolean shouldUseTag() { return useTag; }

    public final ITag<T> getTag()
    {
        if (!useTag)
        {
            throw new IllegalStateException("Tried to access tag when configured for specific content!");
        }
        return tag;
    }

    public abstract boolean isEmpty();

    @SuppressWarnings("unchecked")
    public final <E extends SlotContent<?>> E cast() { return (E) this; }
}