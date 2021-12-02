package xfacthd.recipebuilder.client.screen.widget;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class LocationEntry extends SelectionWidget.SelectionEntry
{
    private final ResourceLocation name;

    public LocationEntry(ResourceLocation name)
    {
        super(new TextComponent(name.toString()));
        this.name = name;
    }

    public ResourceLocation getName() { return name; }
}