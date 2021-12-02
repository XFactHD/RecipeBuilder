package xfacthd.recipebuilder.client.util;

import net.minecraft.network.chat.Component;

public class BuilderException extends RuntimeException
{
    private final Component message;

    public BuilderException(Component message) { this.message = message; }

    public Component getComponent() { return message; }
}