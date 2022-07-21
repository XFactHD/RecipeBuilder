package xfacthd.recipebuilder.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import xfacthd.recipebuilder.RecipeBuilder;
import xfacthd.recipebuilder.client.RBClient;
import xfacthd.recipebuilder.client.data.slots.INumberContent;
import xfacthd.recipebuilder.client.data.slots.NumberSlot;
import xfacthd.recipebuilder.client.screen.widget.*;
import xfacthd.recipebuilder.client.util.ClientUtils;
import xfacthd.recipebuilder.common.container.RecipeBuilderContainer;
import xfacthd.recipebuilder.client.data.*;
import xfacthd.recipebuilder.common.util.Utils;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RecipeBuilderScreen extends ContainerScreen<RecipeBuilderContainer>
{
    public static final ITextComponent TITLE_BTN_CONDITION = Utils.translate(null, "builder.btn.condition");
    public static final ITextComponent TITLE_BTN_PARAMETERS = Utils.translate(null, "builder.btn.parameters");
    public static final ITextComponent TITLE_BTN_BUILD = Utils.translate(null, "builder.btn.build");
    public static final ITextComponent TITLE_BTN_RESET = Utils.translate(null, "builder.btn.reset");
    public static final ITextComponent TITLE_TEXT_RECIPENAME = Utils.translate(null, "builder.text.recipename");
    public static final IFormattableTextComponent MSG_NAME_INVALID = Utils.translate("msg", "builder.recipe_name.invalid");
    public static final ITextComponent MSG_SUCCESS = Utils.translate("msg", "builder.success");
    public static final ITextComponent MSG_SUCCESS_LOCAL = Utils.translate("msg", "builder.success_local");
    public static final ITextComponent HOVER_MSG_CLICK_TO_OPEN = Utils.translate("hover", "recipebuilder.builder.path.click");
    public static final ITextComponent FILTER_ALL = Utils.translate(null, "recipebuilder.builder.filter.all");
    static final int WIDTH = 504;
    static final int HEIGHT = 270;
    private static final int BORDER = 4;
    public static final int TEXT_PADDING = 3;
    private static final int LIST_WIDTH = 130;
    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_INTERVAL = 25;
    private static final Pattern NAME_PATTERN = Pattern.compile("([a-z\\d_-]+)(:?)([a-z\\d/_-]*)");
    private static final Predicate<String> NAME_FILTER = s ->
    {
        if (StringUtils.isNullOrEmpty(s)) { return true; }
        if (s.contains("..")) { return false; } //Name is used as part of the path -> must deny jumping up the directory tree
        return NAME_PATTERN.matcher(s).matches();
    };

    private final Map<RecipeSlot<?>, SlotContent<?>> recipeSlots = new HashMap<>();
    private AbstractBuilder currentBuilder = null;
    private Condition recipeCondition = null;
    private ItemStack conditionStack = ItemStack.EMPTY;
    private String conditionTag = "";

    private SelectionWidget<ModEntry> builderFilter = null;
    private BuilderListWidget builderList = null;
    private TextFieldWidget recipeName = null;
    private Button buttonCondition = null;
    private Button buttonParameters = null;
    private Button buttonBuild = null;
    private ITextComponent pathComponent;
    private int builderX = 0;
    private int builderY = 0;

    public RecipeBuilderScreen(RecipeBuilderContainer container, PlayerInventory playerInv, ITextComponent title)
    {
        super(container, playerInv, title);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init()
    {
        super.init();

        inventoryLabelX = (imageWidth / 2) - (ClientUtils.INVENTORY_WIDTH / 2) + 4;
        inventoryLabelY = imageHeight - BORDER - ClientUtils.INVENTORY_HEIGHT - font.lineHeight + 2;

        String oldFilter = builderFilter != null ? builderFilter.getSelected().getModid() : null;
        ModEntry[] nextFilter = new ModEntry[] { new ModEntry(FILTER_ALL, "") };

        int topOffset = titleLabelY + font.lineHeight + TEXT_PADDING;
        //Can't use method reference for callback because builderList might be null
        builderFilter = new SelectionWidget<>(leftPos + titleLabelX, topPos + topOffset, LIST_WIDTH, new StringTextComponent(""), entry -> builderList.filter(entry));
        builderFilter.addEntry(nextFilter[0]);
        RBClient.BUILDERS.values().stream().map(AbstractBuilder::getModid).distinct().forEach(modid ->
        {
            ModEntry entry = new ModEntry(modid);
            builderFilter.addEntry(entry);
            if (modid.equals(oldFilter))
            {
                nextFilter[0] = entry;
            }
        });
        addWidget(builderFilter);

        builderList = new BuilderListWidget(this, LIST_WIDTH, topPos + topOffset + 22, topPos + imageHeight - BORDER - TEXT_PADDING);
        builderList.setLeftPos(leftPos + titleLabelX);
        addWidget(builderList);

        int buttonX = leftPos + imageWidth - BUTTON_WIDTH - (BORDER * 2);
        int buttonY = topPos + titleLabelY + font.lineHeight + RecipeBuilderScreen.TEXT_PADDING;

        recipeName = addButton(new HintedTextFieldWidget(font, buttonX + 1, buttonY + 1, BUTTON_WIDTH - 2, 18, recipeName, TITLE_TEXT_RECIPENAME));
        recipeName.setFilter(NAME_FILTER);
        recipeName.active = false;
        buttonY += BUTTON_INTERVAL;

        buttonCondition = addButton(new Button(buttonX, buttonY, BUTTON_WIDTH, 20, TITLE_BTN_CONDITION, btn -> selectCondition()));
        buttonCondition.active = false;
        buttonY += BUTTON_INTERVAL;

        buttonParameters = addButton(new Button(buttonX, buttonY, BUTTON_WIDTH, 20, TITLE_BTN_PARAMETERS, btn -> editParameters()));
        buttonParameters.active = false;
        buttonY += BUTTON_INTERVAL;

        buttonBuild = addButton(new Button(buttonX, buttonY, BUTTON_WIDTH, 20, TITLE_BTN_BUILD, btn -> buildRecipe()));
        buttonBuild.active = false;
        buttonY += BUTTON_INTERVAL;

        buttonY += BUTTON_INTERVAL;
        addButton(new Button(buttonX, buttonY, BUTTON_WIDTH, 20, TITLE_BTN_RESET, btn -> clearRecipe()));

        if (currentBuilder != null)
        {
            builderList.setSelected(currentBuilder);
            selectBuilder(currentBuilder);
        }

        builderFilter.setSelected(nextFilter[0], true);

        pathComponent = buildPackPathComponent();
    }

    @Override
    public void render(MatrixStack mstack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(mstack);
        super.render(mstack, mouseX, mouseY, partialTicks);
        renderTooltip(mstack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack mstack, float partialTicks, int mouseX, int mouseY)
    {
        ClientUtils.drawScreenBackground(this, mstack, leftPos, topPos, imageWidth, imageHeight);

        if (currentBuilder != null)
        {
            ClientUtils.drawBuilderBackground(this, mstack, builderX - 3, builderY - 3, currentBuilder.getTexWidth() + 6, currentBuilder.getTexHeight() + 6);

            currentBuilder.drawBackground(this, mstack, builderX, builderY);
        }

        int invX = (imageWidth / 2) - (ClientUtils.INVENTORY_WIDTH / 2);
        int invY = imageHeight - ClientUtils.BORDER - ClientUtils.INVENTORY_HEIGHT;
        ClientUtils.drawInventoryBackground(this, mstack, leftPos + invX, topPos + invY, false);

        builderList.render(mstack, mouseX, mouseY, partialTicks);
        builderFilter.render(mstack, mouseX, mouseY, partialTicks);
        renderSlots(mstack, mouseX, mouseY);
    }

    @Override
    public void tick()
    {
        super.tick();
        recipeName.tick();
    }

    private void renderSlots(MatrixStack mstack, int mouseX, int mouseY)
    {
        for (Map.Entry<RecipeSlot<?>, SlotContent<?>> entry : recipeSlots.entrySet())
        {
            RecipeSlot<?> slot = entry.getKey();
            SlotContent<?> content = entry.getValue();

            if (slot instanceof NumberSlot) { continue; } //NumberSlots don't render anything themselves

            if (!entry.getValue().isEmpty())
            {
                slot.renderContentInBuilder(this, content.cast(), mstack, builderX, builderY, 0, font);
            }

            int x = builderX + slot.getX();
            int y = builderY + slot.getY();
            if (mouseX >= x && mouseX <= x + slot.getWidth() && mouseY >= y && mouseY <= y + slot.getHeight())
            {
                renderHoveredSlot(mstack, slot);

                if (player().inventory.getCarried().isEmpty() && !entry.getValue().isEmpty())
                {
                    slot.renderTooltip(this, content.cast(), mstack, mouseX, mouseY, font);
                }
            }
        }
    }

    private void renderHoveredSlot(MatrixStack mstack, RecipeSlot<?> slot)
    {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);

        int x = builderX + slot.getX();
        int y = builderY + slot.getY();
        fillGradient(mstack, x, y, x + slot.getWidth(), y + slot.getHeight(), 0x80ffffff, 0x80ffffff);

        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (Map.Entry<RecipeSlot<?>, SlotContent<?>> entry : recipeSlots.entrySet())
        {
            RecipeSlot<?> slot = entry.getKey();
            SlotContent<?> content = entry.getValue();

            if (slot instanceof NumberSlot) { continue; } //NumberSlots are not clickable

            int x = builderX + slot.getX();
            int y = builderY + slot.getY();
            if (mouseX >= x && mouseX <= x + slot.getWidth() && mouseY >= y && mouseY <= y + slot.getHeight())
            {
                if (button == 0) //Left mouse button
                {
                    ItemStack carried = player().inventory.getCarried();
                    if (carried.isEmpty() && !entry.getValue().isEmpty())
                    {
                        content.acceptItem(ItemStack.EMPTY);
                    }
                    else if (!carried.isEmpty())
                    {
                        content.acceptItem(carried);
                    }
                    return true;
                }
                else if (button == 1 && slot.canEdit(content.cast())) //Right mouse button
                {
                    mc().pushGuiLayer(slot.requestEdit(content.cast()));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        if (mc().options.keyInventory.matches(pKeyCode, pScanCode) && recipeName.isFocused())
        {
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public void selectBuilder(AbstractBuilder type)
    {
        boolean changed = currentBuilder != type;
        currentBuilder = type;

        if (changed)
        {
            recipeSlots.clear();
            type.getSlots().forEach((name, slot) -> recipeSlots.put(slot, slot.newEmptyContent()));
        }

        int listRight = builderList.getRight() + 12;
        int screenTop = topPos + BORDER + 3;

        builderX = leftPos + (imageWidth / 2) - (currentBuilder.getTexWidth() / 2);
        if (builderX < listRight)
        {
            builderX += (listRight - builderX);
        }

        int top = titleLabelY + font.lineHeight + TEXT_PADDING;
        int bottom = imageHeight - BORDER - ClientUtils.INVENTORY_HEIGHT;
        builderY = topPos + ((bottom - top) / 2) - (currentBuilder.getTexHeight() / 2);
        if (builderY < screenTop)
        {
            builderY += (screenTop - builderY);
        }

        recipeName.active = true;
        buttonCondition.active = currentBuilder.needsAdvancement();
        buttonParameters.active = recipeSlots.values().stream().anyMatch(content -> content instanceof INumberContent);
        buttonBuild.active = !currentBuilder.needsAdvancement() || recipeCondition != null;

        if (changed)
        {
            setCriterion(null, ItemStack.EMPTY, "");
        }
    }

    private void selectCondition()
    {
        if (currentBuilder.needsAdvancement())
        {
            SelectConditionScreen screen = new SelectConditionScreen(this);
            mc().pushGuiLayer(screen);
            if (recipeCondition != null)
            {
                screen.setExistingSelection(recipeCondition, conditionStack, conditionTag);
            }
        }
    }

    public void setCriterion(Condition condition, ItemStack stack, String tag)
    {
        recipeCondition = condition;
        conditionStack = stack;
        conditionTag = tag;

        buttonBuild.active = (currentBuilder != null && !currentBuilder.needsAdvancement()) || condition != null;
    }

    private void editParameters()
    {
        Map<NumberSlot<?>, INumberContent> parameters = new HashMap<>();
        recipeSlots.forEach((slot, content) ->
        {
            if (slot instanceof NumberSlot)
            {
                parameters.put((NumberSlot<?>) slot, (INumberContent) content);
            }
        });

        mc().pushGuiLayer(new EditParametersScreen(parameters));
    }

    private void buildRecipe()
    {
        if (currentBuilder != null)
        {
            String name = recipeName.getValue();
            if (name.endsWith(":"))
            {
                mc().pushGuiLayer(MessageScreen.error(MSG_NAME_INVALID.copy().append(name)));
                return;
            }

            ITextComponent error = currentBuilder.buildRecipe(
                    recipeSlots,
                    name,
                    recipeCondition != null ? recipeCondition.toCriterion(conditionStack, conditionTag) : null,
                    recipeCondition != null ? recipeCondition.buildName(conditionStack, conditionTag) : ""
            );

            if (error != null)
            {
                mc().pushGuiLayer(MessageScreen.error(error));
            }
            else
            {
                List<ITextComponent> messages = new ArrayList<>();

                messages.add(MSG_SUCCESS);
                messages.add(pathComponent);
                if (mc().isLocalServer()) { messages.add(MSG_SUCCESS_LOCAL); }

                mc().pushGuiLayer(MessageScreen.info(messages));
            }
        }
    }

    private void clearRecipe()
    {
        recipeSlots.forEach((slot, content) -> content.clear());

        setCriterion(null, ItemStack.EMPTY, "");
    }

    public static ITextComponent buildPackPathComponent()
    {
        Path datapack = Minecraft.getInstance().gameDirectory.toPath().resolve(RecipeBuilder.MOD_ID + "/generated_pack").toAbsolutePath().normalize();
        return new StringTextComponent(datapack.toString())
                .setStyle(Style.EMPTY
                        .withColor(TextFormatting.DARK_GRAY)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, HOVER_MSG_CLICK_TO_OPEN))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, datapack.toString()))
                );
    }



    public FontRenderer getFont() { return font; }

    private Minecraft mc()
    {
        if (minecraft == null) { throw new IllegalStateException("Screen not initialized!"); }
        return minecraft;
    }

    private PlayerEntity player()
    {
        PlayerEntity player = mc().player;
        if (player == null) { throw new IllegalStateException("No player available!"); }
        return player;
    }
}