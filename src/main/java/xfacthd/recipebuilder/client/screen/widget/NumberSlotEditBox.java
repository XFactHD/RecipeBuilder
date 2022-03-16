package xfacthd.recipebuilder.client.screen.widget;

import net.minecraft.client.gui.Font;
import xfacthd.recipebuilder.client.data.slots.*;

public class NumberSlotEditBox extends NumberEditBox
{
    private final INumberContent content;

    public NumberSlotEditBox(Font font, int x, int y, int width, int height, NumberSlot<?> slot, INumberContent content, boolean commitOnChange)
    {
        super(font, x, y, width, height, slot.getTitle(), commitOnChange);
        this.content = content;

        if (content instanceof IntegerSlot.IntegerContent)
        {
            setValue(Integer.toString(((IntegerSlot.IntegerContent) content).getContent()));
            setFilter(INTEGER_FILTER);
        }
        else if (content instanceof FloatSlot.FloatContent)
        {
            setValue(Float.toString(((FloatSlot.FloatContent) content).getContent()));
            setFilter(FLOAT_FILTER);
        }
    }

    @Override
    public void commit()
    {
        if (content instanceof IntegerSlot.IntegerContent)
        {
            ((IntegerSlot.IntegerContent) content).setContent(getIntegerValue());
        }
        else if (content instanceof FloatSlot.FloatContent)
        {
            ((FloatSlot.FloatContent) content).setContent(getFloatValue());
        }
    }
}
