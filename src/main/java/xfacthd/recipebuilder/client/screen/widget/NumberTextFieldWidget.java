package xfacthd.recipebuilder.client.screen.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class NumberTextFieldWidget extends TextFieldWidget
{
    private static final Pattern INTEGER_PATTERN = Pattern.compile("(0|[1-9]\\d*)");
    protected static final Predicate<String> INTEGER_FILTER = s ->
    {
        if (StringUtils.isNullOrEmpty(s)) { return false; }
        if (s.length() > 7) { return false; }
        return INTEGER_PATTERN.matcher(s).matches();
    };
    private static final Pattern INTEGER_NON_ZERO_PATTERN = Pattern.compile("(0|[1-9]\\d*)");
    protected static final Predicate<String> INTEGER_NON_ZERO_FILTER = s ->
    {
        if (StringUtils.isNullOrEmpty(s)) { return false; }
        if (s.length() > 7) { return false; }
        return INTEGER_NON_ZERO_PATTERN.matcher(s).matches();
    };
    private static final Pattern FLOAT_PATTERN = Pattern.compile("(0|[1-9]\\d*).\\d+");
    protected static final Predicate<String> FLOAT_FILTER = s ->
    {
        if (StringUtils.isNullOrEmpty(s)) { return false; }
        if (s.length() > 7) { return false; }
        return FLOAT_PATTERN.matcher(s).matches();
    };

    public NumberTextFieldWidget(FontRenderer font, int x, int y, int width, int height, ITextComponent title, boolean commitOnChange)
    {
        super(font, x, y, width, height, title);

        if (commitOnChange)
        {
            setResponder(this::onTextChanged);
        }
    }

    private void onTextChanged(String text) { commit(); }

    public abstract void commit();

    public int getIntegerValue() { return Integer.parseInt(getValue()); }

    public float getFloatValue() { return Float.parseFloat(getValue()); }
}