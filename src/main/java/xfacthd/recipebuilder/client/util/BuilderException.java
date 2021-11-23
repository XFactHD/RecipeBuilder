package xfacthd.recipebuilder.client.util;

import net.minecraft.util.text.ITextComponent;

public class BuilderException extends RuntimeException
{
    private final ITextComponent message;

    public BuilderException(ITextComponent message) { this.message = message; }

    public ITextComponent getComponent() { return message; }
}