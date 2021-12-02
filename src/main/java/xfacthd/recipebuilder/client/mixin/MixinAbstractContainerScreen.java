package xfacthd.recipebuilder.client.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import xfacthd.recipebuilder.client.screen.SelectConditionScreen;

/**
 * Adapt Z height of the item rendered in the slot to the current height of the Screen stack
 * TODO: try to convert this into a Forge PR
 */
@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen
{
    @ModifyArg(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;setBlitOffset(I)V"))
    private int setScreenBlitOffsetSlot(int offset)
    {
        if (((Object) this) instanceof SelectConditionScreen && offset == 100)
        {
            return offset + 2000;
        }
        return offset;
    }

    @ModifyArg(method = "renderFloatingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;setBlitOffset(I)V"))
    private int setScreenBlitOffsetFloating(int offset)
    {
        if (((Object) this) instanceof SelectConditionScreen && offset == 200)
        {
            return offset + 2000;
        }
        return offset;
    }



    @ModifyConstant(method = "renderSlot", constant = @Constant(floatValue = 100F))
    private float setItemRendererBlitOffsetSlot(float offset)
    {
        if (((Object) this) instanceof SelectConditionScreen)
        {
            return offset + 2000;
        }
        return offset;
    }

    @ModifyConstant(method = "renderFloatingItem", constant = @Constant(floatValue = 200F))
    private float setItemRendererBlitOffsetFloating(float offset)
    {
        if (((Object) this) instanceof SelectConditionScreen)
        {
            return offset + 2000;
        }
        return offset;
    }
}