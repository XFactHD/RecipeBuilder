package xfacthd.recipebuilder.client.data;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

public abstract class RecipeSlot<T extends SlotContent<?>>
{
    private final String name;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final boolean optional;
    private final boolean supportTags;
    private final ResourceLocation tagKey;
    private final boolean allowTags;

    protected RecipeSlot(String name, int x, int y, int width, int height, boolean optional)
    {
        this(name, x, y, width, height, optional, false, null, false);
    }

    protected RecipeSlot(String name, int x, int y, int width, int height, boolean optional, boolean supportTags, String tagKey, boolean allowTags)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.optional = optional;
        this.supportTags = supportTags;
        this.tagKey = tagKey != null ? new ResourceLocation(tagKey) : null;
        if (allowTags && !supportTags) { throw new IllegalArgumentException("Can't allow tags when they are not supported!"); }
        this.allowTags = allowTags;
    }

    public final String getName() { return name; }

    public final int getX() { return x; }

    public final int getY() { return y; }

    public final int getWidth() { return width; }

    public final int getHeight() { return height; }

    public final boolean isOptional() { return optional; }

    public final boolean supportsTags() { return supportTags; }

    public ResourceLocation getTagKey() { return tagKey; }

    public boolean allowsTags() { return supportTags && allowTags; }

    public abstract T newEmptyContent();

    public boolean canEdit(T content) { return false; }

    public Screen requestEdit(T content) { return null; }

    public void renderContentInBuilder(Screen screen, T content, MatrixStack mstack, int builderX, int builderY, int blitBase, FontRenderer font)
    {
        renderContent(screen, content, mstack, builderX + getX(), builderY + getY(), blitBase, font);
    }

    public abstract void renderContent(Screen screen, T content, MatrixStack mstack, int builderX, int builderY, int blitBase, FontRenderer font);

    public abstract void renderTooltip(Screen screen, T content, MatrixStack mstack, int mouseX, int mouseY, FontRenderer font);
}