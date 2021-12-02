package xfacthd.recipebuilder.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.ItemStack;
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

    protected class BuilderEntry extends ObjectSelectionList.Entry<BuilderEntry>
    {
        private final AbstractBuilder type;
        private final ItemStack typeIcon;
        private final Component typeTitle;
        private final Component modName;

        private BuilderEntry(AbstractBuilder type)
        {
            this.type = type;
            this.typeIcon = type.getIcon();
            this.typeTitle = type.getTypeName();
            this.modName = type.getModName();
        }

        @Override
        public void render(PoseStack pstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
        {
            if (isMouseOver && !isSelectedItem(index))
            {
                Tesselator tess = Tesselator.getInstance();
                BufferBuilder buffer = tess.getBuilder();

                RenderSystem.disableTexture();

                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                buffer.vertex(left - 1,         top + height + 1, 0.0D).endVertex();
                buffer.vertex(left + width - 3, top + height + 1, 0.0D).endVertex();
                buffer.vertex(left + width - 3, top - 1, 0.0D).endVertex();
                buffer.vertex(left - 1,         top - 1, 0.0D).endVertex();
                tess.end();

                RenderSystem.setShaderColor(0F, 0F, 0F, 1F);
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                buffer.vertex(left,             top + height, 0.0D).endVertex();
                buffer.vertex(left + width - 4, top + height, 0.0D).endVertex();
                buffer.vertex(left + width - 4, top, 0.0D).endVertex();
                buffer.vertex(left,             top, 0.0D).endVertex();
                tess.end();

                RenderSystem.enableTexture();
            }

            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(typeIcon, left + 1, top + 2);

            Font font = BuilderListWidget.this.parent.getFont();

            FormattedText titleLine = FormattedText.composite(font.substrByWidth(typeTitle, listWidth - 6));
            FormattedText modNameLine = FormattedText.composite(font.substrByWidth(modName, listWidth - 6));

            int textY = top + 2;
            font.draw(pstack, Language.getInstance().getVisualOrder(titleLine), left + 20, textY, 0xFFFFFF);
            textY += font.lineHeight;
            font.draw(pstack, Language.getInstance().getVisualOrder(modNameLine), left + 20, textY, 0xCCCCCC);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
        {
            BuilderListWidget.this.parent.selectBuilder(type);
            BuilderListWidget.this.setSelected(this);
            return false;
        }

        @Override
        public Component getNarration() { return typeTitle; }
    }
}