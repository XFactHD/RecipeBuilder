package xfacthd.recipebuilder.client.screen.widget;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class NotifyingCheckboxButton extends Checkbox
{
    private final Consumer<NotifyingCheckboxButton> pressable;

    public NotifyingCheckboxButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean pSelected, Consumer<NotifyingCheckboxButton> pressable)
    {
        super(pX, pY, pWidth, pHeight, pMessage, pSelected);
        this.pressable = pressable;
    }

    public NotifyingCheckboxButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean pSelected, boolean pShowLabel, Consumer<NotifyingCheckboxButton> pressable)
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