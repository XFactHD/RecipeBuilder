package xfacthd.recipebuilder.client.screen.widget;

import net.minecraft.network.chat.Component;
import xfacthd.recipebuilder.client.data.AbstractBuilder;

public class ModEntry extends SelectionWidget.SelectionEntry
{
    private final String modid;

    public ModEntry(String modid) { this(AbstractBuilder.getModName(modid), modid); }

    public ModEntry(Component modName, String modid)
    {
        super(modName);
        this.modid = modid;
    }

    public String getModid() { return modid; }
}