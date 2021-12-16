package xfacthd.recipebuilder.client.screen.widget.taglist;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import xfacthd.recipebuilder.client.data.AbstractBuilder;
import xfacthd.recipebuilder.common.util.Utils;

public abstract class AbstractTagEntry extends ObjectSelectionList.Entry<AbstractTagEntry>
{
    public static final Component TITLE_OPTIONAL = Utils.translate(null, "tag_entry.optional");

    protected TagEntryListWidget parent;
    private final String entryName;
    protected final Component translatedName;
    protected final Component rawNameComponent;
    protected final Component modName;
    private boolean optional = false;
    private final int textOffX;

    protected AbstractTagEntry(String name, Component translatedName, int textOffX)
    {
        this.entryName = name;
        this.translatedName = translatedName;
        this.rawNameComponent = new TextComponent(name);
        this.modName = AbstractBuilder.getModName(name.contains(":") ? name.split(":")[0] : "minecraft");
        this.textOffX = textOffX;
    }

    @Override
    public void render(PoseStack pstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
    {
        Font font = Minecraft.getInstance().font;

        int textX = left + 2 + textOffX;
        int textY = top + 2;

        font.draw(pstack, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(translatedName, width))), textX, textY, 0xFFFFFF);
        textY += font.lineHeight;

        font.draw(pstack, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(rawNameComponent, width))), textX, textY, 0xFFFFFF);
        textY += font.lineHeight;

        font.draw(pstack, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(modName, width))), textX, textY, 0xCCCCCC);

        if (optional)
        {
            textX = left + width - 6 - (font.width(TITLE_OPTIONAL) / 2);
            textY = top + 2;

            pstack.pushPose();
            pstack.translate(textX, textY, 0);
            pstack.scale(.5F, .5F, 1);
            font.draw(pstack, TITLE_OPTIONAL, 0, 0, 0xFFFFFF);
            pstack.popPose();
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

    @Override
    public Component getNarration() { return translatedName; }
}