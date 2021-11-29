package xfacthd.recipebuilder.client.screen.widget.taglist;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.*;
import xfacthd.recipebuilder.client.data.AbstractBuilder;

public abstract class AbstractTagEntry extends ExtendedList.AbstractListEntry<AbstractTagEntry>
{
    protected TagEntryListWidget parent;
    private final String entryName;
    protected final ITextComponent translatedName;
    protected final ITextComponent rawNameComponent;
    protected final ITextComponent modName;
    private final int textOffX;

    protected AbstractTagEntry(String name, ITextComponent translatedName, int textOffX)
    {
        this.entryName = name;
        this.translatedName = translatedName;
        this.rawNameComponent = new StringTextComponent(name);
        this.modName = AbstractBuilder.getModName(name.contains(":") ? name.split(":")[0] : "minecraft");
        this.textOffX = textOffX;
    }

    @Override
    public void render(MatrixStack mstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
    {
        FontRenderer font = Minecraft.getInstance().font;

        int textX = left + 2 + textOffX;
        int textY = top + 2;

        font.draw(mstack, LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(font.substrByWidth(translatedName, width))), textX, textY, 0xFFFFFF);
        textY += font.lineHeight;

        font.draw(mstack, LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(font.substrByWidth(rawNameComponent, width))), textX, textY, 0xFFFFFF);
        textY += font.lineHeight;

        font.draw(mstack, LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(font.substrByWidth(modName, width))), textX, textY, 0xCCCCCC);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        parent.setSelected(this);
        return false;
    }

    void setParent(TagEntryListWidget parent) { this.parent = parent; }

    public String getEntryName() { return entryName; }
}