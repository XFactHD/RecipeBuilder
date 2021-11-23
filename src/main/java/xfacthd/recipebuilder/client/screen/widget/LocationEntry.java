package xfacthd.recipebuilder.client.screen.widget;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class LocationEntry extends SelectionWidget.SelectionEntry
{
    private final ResourceLocation name;

    public LocationEntry(ResourceLocation name)
    {
        super(new StringTextComponent(name.toString()));
        this.name = name;
    }

    public ResourceLocation getName() { return name; }
}