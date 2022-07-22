package xfacthd.recipebuilder.client.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

public abstract class SlotContent<T>
{
    protected T content;
    protected boolean useTag = false;
    protected TagKey<T> tag = null;

    protected SlotContent(T content) { this.content = content; }

    public void setContent(T content)
    {
        this.content = content;
        setTag(null);
    }

    public abstract void acceptItem(ItemStack stack);

    public abstract void clear();

    public final T getContent() { return content; }

    public void setTag(TagKey<T> tag)
    {
        useTag = tag != null;
        this.tag = tag;
    }

    public final boolean shouldUseTag() { return useTag; }

    public final TagKey<T> getTag()
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