package xfacthd.recipebuilder.client.screen.widget;

import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;

public class NotifyingCheckboxButton extends CheckboxButton
{
    private final Consumer<NotifyingCheckboxButton> pressable;

    public NotifyingCheckboxButton(int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage, boolean pSelected, Consumer<NotifyingCheckboxButton> pressable)
    {
        super(pX, pY, pWidth, pHeight, pMessage, pSelected);
        this.pressable = pressable;
    }

    public NotifyingCheckboxButton(int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage, boolean pSelected, boolean pShowLabel, Consumer<NotifyingCheckboxButton> pressable)
    {
        super(pX, pY, pWidth, pHeight, pMessage, pSelected, pShowLabel);
        this.pressable = pressable;
    }

    @Override
    public void onPress()
    {
        super.onPress();
        if (pressable != null)
        {
            pressable.accept(this);
        }
    }
}