package xfacthd.recipebuilder.client.screen.widget.taglist;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.*;
import xfacthd.recipebuilder.client.data.AbstractBuilder;
import xfacthd.recipebuilder.common.util.Utils;

public abstract class AbstractTagEntry extends ExtendedList.AbstractListEntry<AbstractTagEntry>
{
    public static final ITextComponent TITLE_OPTIONAL = Utils.translate(null, "tag_entry.optional");

    protected TagEntryListWidget parent;
    private final String entryName;
    protected final ITextComponent translatedName;
    protected final ITextComponent rawNameComponent;
    protected final ITextComponent modName;
    private boolean optional = false;
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

        if (optional)
        {
            textX = left + width - 6 - (font.width(TITLE_OPTIONAL) / 2);
            textY = top + 2;

            mstack.pushPose();
            mstack.translate(textX, textY, 0);
            mstack.scale(.5F, .5F, 1);
            font.draw(mstack, TITLE_OPTIONAL, 0, 0, 0xFFFFFF);
            mstack.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        parent.setSelected(this);
        return false;
    }

    void setParent(TagEntryListWidget parent) { this.parent = parent; }

    public String getEntryName() { return entryName; }

    public void setOptional(boolean optional) { this.optional = optional; }

    public boolean isOptional() { return optional; }
}