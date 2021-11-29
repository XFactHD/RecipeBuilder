package xfacthd.recipebuilder.client.screen.widget.taglist;

import xfacthd.recipebuilder.client.screen.TagBuilderScreen;
import xfacthd.recipebuilder.client.screen.widget.ScissoredList;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class TagEntryListWidget extends ScissoredList<AbstractTagEntry>
{
    private final Consumer<AbstractTagEntry> onSelected;

    public TagEntryListWidget(TagBuilderScreen parent, int width, int top, int bottom, Consumer<AbstractTagEntry> onSelected)
    {
        super(parent.getMinecraft(), width, bottom - top, top, bottom, parent.getFont().lineHeight * 3 + 8);
        this.onSelected = onSelected;
        setRenderTopAndBottom(false);
    }

    @Override
    protected int getScrollbarPosition() { return x0 + listWidth; }

    @Override
    public int getRowWidth() { return listWidth; }

    @Override
    protected AbstractTagEntry getEntryAtPosition(double pMouseX, double pMouseY)
    {
        return super.getEntryAtPosition(pMouseX, pMouseY + 2);
    }

    @Override
    public void setSelected(@Nullable AbstractTagEntry entry)
    {
        super.setSelected(entry);
        onSelected.accept(entry);
    }

    @Override
    public int addEntry(AbstractTagEntry entry)
    {
        entry.setParent(this);
        return super.addEntry(entry);
    }

    public boolean contains(String entry)
    {
        for (AbstractTagEntry tagEntry : children())
        {
            if (tagEntry.getEntryName().equals(entry))
            {
                return true;
            }
        }
        return false;
    }
}