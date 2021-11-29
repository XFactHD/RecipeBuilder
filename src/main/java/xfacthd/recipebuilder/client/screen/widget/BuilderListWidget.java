package xfacthd.recipebuilder.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import org.lwjgl.opengl.GL11;
import xfacthd.recipebuilder.client.RBClient;
import xfacthd.recipebuilder.client.screen.RecipeBuilderScreen;
import xfacthd.recipebuilder.client.data.AbstractBuilder;

import java.util.ArrayList;
import java.util.List;

public class BuilderListWidget extends ScissoredList<BuilderListWidget.BuilderEntry>
{
    private final List<BuilderEntry> entries = new ArrayList<>();
    private final RecipeBuilderScreen parent;

    public BuilderListWidget(RecipeBuilderScreen parent, int width, int top, int bottom)
    {
        super(parent.getMinecraft(), width, bottom - top, top, bottom, parent.getFont().lineHeight * 2 + 8);
        this.parent = parent;
        setRenderTopAndBottom(false);

        RBClient.BUILDERS.forEach((type, builder) -> addEntry(new BuilderEntry(builder)));
    }

    @Override
    protected int getScrollbarPosition() { return x0 + listWidth; }

    @Override
    public int getRowWidth() { return listWidth; }

    @Override
    protected BuilderEntry getEntryAtPosition(double pMouseX, double pMouseY)
    {
        return super.getEntryAtPosition(pMouseX, pMouseY + 2);
    }

    @Override
    protected int addEntry(BuilderEntry pEntry)
    {
        entries.add(pEntry);
        return super.addEntry(pEntry);
    }

    public void setSelected(AbstractBuilder type)
    {
        for (BuilderEntry entry : children())
        {
            if (entry.type == type)
            {
                setSelected(entry);
                break;
            }
        }
    }

    public void filter(ModEntry filter)
    {
        children().clear();
        if (filter.getModid().isEmpty())
        {
            children().addAll(entries);
        }
        else
        {
            entries.stream()
                    .filter(entry -> entry.type.getModid().equals(filter.getModid()))
                    .forEach(children()::add);
        }
    }

    protected class BuilderEntry extends ExtendedList.AbstractListEntry<BuilderEntry>
    {
        private final AbstractBuilder type;
        private final ItemStack typeIcon;
        private final ITextComponent typeTitle;
        private final ITextComponent modName;

        private BuilderEntry(AbstractBuilder type)
        {
            this.type = type;
            this.typeIcon = type.getIcon();
            this.typeTitle = type.getTypeName();
            this.modName = type.getModName();
        }

        @Override
        public void render(MatrixStack mstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
        {
            if (isMouseOver && !isSelectedItem(index))
            {
                Tessellator tess = Tessellator.getInstance();
                BufferBuilder buffer = tess.getBuilder();

                RenderSystem.disableTexture();

                //noinspection deprecation
                RenderSystem.color4f(1F, 1F, 1F, 1F);
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
                buffer.vertex(left - 1,         top + height + 1, 0.0D).endVertex();
                buffer.vertex(left + width - 3, top + height + 1, 0.0D).endVertex();
                buffer.vertex(left + width - 3, top - 1, 0.0D).endVertex();
                buffer.vertex(left - 1,         top - 1, 0.0D).endVertex();
                tess.end();

                //noinspection deprecation
                RenderSystem.color4f(0F, 0F, 0F, 1F);
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
                buffer.vertex(left,             top + height, 0.0D).endVertex();
                buffer.vertex(left + width - 4, top + height, 0.0D).endVertex();
                buffer.vertex(left + width - 4, top, 0.0D).endVertex();
                buffer.vertex(left,             top, 0.0D).endVertex();
                tess.end();

                RenderSystem.enableTexture();
            }

            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(typeIcon, left + 1, top + 2);

            FontRenderer font = BuilderListWidget.this.parent.getFont();

            ITextProperties titleLine = ITextProperties.composite(font.substrByWidth(typeTitle, listWidth - 6));
            ITextProperties modNameLine = ITextProperties.composite(font.substrByWidth(modName, listWidth - 6));

            int textY = top + 2;
            font.draw(mstack, LanguageMap.getInstance().getVisualOrder(titleLine), left + 20, textY, 0xFFFFFF);
            textY += font.lineHeight;
            font.draw(mstack, LanguageMap.getInstance().getVisualOrder(modNameLine), left + 20, textY, 0xCCCCCC);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
        {
            BuilderListWidget.this.parent.selectBuilder(type);
            BuilderListWidget.this.setSelected(this);
            return false;
        }
    }
}