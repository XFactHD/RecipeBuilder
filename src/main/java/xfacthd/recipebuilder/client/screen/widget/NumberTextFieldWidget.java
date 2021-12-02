package xfacthd.recipebuilder.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.StringUtil;
import xfacthd.recipebuilder.client.data.slots.*;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class NumberTextFieldWidget extends EditBox
{
    private static final Pattern INTEGER_PATTERN = Pattern.compile("(0|[1-9][0-9]*)");
    private static final Predicate<String> INTEGER_FILTER = s ->
    {
        if (StringUtil.isNullOrEmpty(s)) { return false; }
        if (s.length() > 7) { return false; }
        return INTEGER_PATTERN.matcher(s).matches();
    };
    private static final Pattern FLOAT_PATTERN = Pattern.compile("(0|[1-9][0-9]*).[0-9]+");
    private static final Predicate<String> FLOAT_FILTER = s ->
    {
        if (StringUtil.isNullOrEmpty(s)) { return false; }
        if (s.length() > 7) { return false; }
        return FLOAT_PATTERN.matcher(s).matches();
    };

    private final INumberContent content;

    public NumberTextFieldWidget(Font font, int x, int y, int width, int height, NumberSlot<?> slot, INumberContent content, boolean commitOnChange)
    {
        super(font, x, y, width, height, slot.getTitle());
        this.content = content;

        if (commitOnChange)
        {
            setResponder(this::onTextChanged);
        }

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

    private void onTextChanged(String text) { commit(); }

    public void commit()
    {
        if (content instanceof IntegerSlot.IntegerContent)
        {
            ((IntegerSlot.IntegerContent) content).setContent(Integer.valueOf(getValue()));
        }
        else if (content instanceof FloatSlot.FloatContent)
        {
            ((FloatSlot.FloatContent) content).setContent(Float.valueOf(getValue()));
        }
    }
}