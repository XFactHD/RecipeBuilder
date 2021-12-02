package xfacthd.recipebuilder.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.recipebuilder.client.data.Exporter;
import xfacthd.recipebuilder.client.screen.widget.*;
import xfacthd.recipebuilder.client.screen.widget.taglist.*;
import xfacthd.recipebuilder.client.util.ClientUtils;
import xfacthd.recipebuilder.common.container.TagBuilderContainer;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TagBuilderScreen extends AbstractContainerScreen<TagBuilderContainer>
{
    public static final Component TITLE_TEXT_TAGNAME = Utils.translate(null, "builder.text.tagname");
    public static final Component TITLE_TEXT_ADD = Utils.translate(null, "builder.text.add_enty");
    public static final Component TITLE_TAG_TYPE = Utils.translate(null, "builder.select.tagtype");
    public static final Component TITLE_TAG_REPLACE = Utils.translate(null, "builder.tag.replace");
    public static final Component TITLE_BTN_ADD_ENTRY = Utils.translate(null, "builder.btn.add_entry");
    public static final Component TITLE_BTN_REMOVE_ENTRY = Utils.translate(null, "builder.btn.remove_entry");
    public static final Component MSG_NAME_EMPTY = Utils.translate("msg", "builder.tag_name.empty");
    public static final Component MSG_NAME_NO_NS = Utils.translate("msg", "builder.tag_name.no_ns");
    public static final Component MSG_ENTRY_NAME_EMPTY = Utils.translate("msg", "builder.tag_entry.name_empty");
    public static final Component MSG_ENTRY_UNKNOWN = Utils.translate("msg", "builder.tag_entry.unknown");
    public static final Component MSG_ENTRY_EXISTS = Utils.translate("msg", "builder.tag_entry.exists");
    public static final MutableComponent MSG_SUCCESS = Utils.translate("msg", "builder.tag.success");
    public static final Component MSG_SUCCESS_LOCAL = Utils.translate("msg", "builder.tag.success_local");
    private static final int WIDTH = 424;
    private static final int HEIGHT = 250;
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("([a-z0-9_.-]+)");
    private static final Predicate<String> TAG_NAME_FILTER = s ->
    {
        if (StringUtil.isNullOrEmpty(s)) { return true; }
        if (s.contains("..")) { return false; } //Name is used as part of the path -> must deny jumping up the directory tree
        return TAG_NAME_PATTERN.matcher(s).matches();
    };

    private EditBox tagName = null;
    private SelectionWidget<TypeEntry> tagType = null;
    private Checkbox tagReplace = null;
    private EditBox tagEntryName = null;
    private Button removeEntry = null;
    private TagEntryListWidget entryList = null;
    private Component pathComponent;

    public TagBuilderScreen(TagBuilderContainer container, Inventory playerInv, Component title)
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
        inventoryLabelY = imageHeight - ClientUtils.BORDER - ClientUtils.INVENTORY_HEIGHT - font.lineHeight + 2;

        //Left side
        int leftFieldX = leftPos + titleLabelX;
        int leftFieldY = topPos + titleLabelY + font.lineHeight + RecipeBuilderScreen.TEXT_PADDING;
        tagName = addRenderableWidget(new HintedTextFieldWidget(font, leftFieldX + 1, leftFieldY + 1, RecipeBuilderScreen.BUTTON_WIDTH - 2, 18, tagName, TITLE_TEXT_TAGNAME));
        tagName.setFilter(TAG_NAME_FILTER);
        leftFieldY += RecipeBuilderScreen.BUTTON_INTERVAL;

        TypeEntry selected = tagType != null ? tagType.getSelected() : null;
        tagType = addWidget(new SelectionWidget<>(leftFieldX, leftFieldY, RecipeBuilderScreen.BUTTON_WIDTH, TITLE_TAG_TYPE, this::onTypeSelected));
        leftFieldY += RecipeBuilderScreen.BUTTON_INTERVAL;

        tagReplace = addRenderableWidget(new Checkbox(leftFieldX, leftFieldY, RecipeBuilderScreen.BUTTON_WIDTH, 20, TITLE_TAG_REPLACE, tagReplace != null && tagReplace.selected()));
        leftFieldY += RecipeBuilderScreen.BUTTON_INTERVAL * 2;

        tagEntryName = addRenderableWidget(new HintedTextFieldWidget(font, leftFieldX + 1, leftFieldY + 1, RecipeBuilderScreen.BUTTON_WIDTH - 2, 18, tagEntryName, TITLE_TEXT_ADD));
        leftFieldY += RecipeBuilderScreen.BUTTON_INTERVAL;

        addRenderableWidget(new Button(leftFieldX, leftFieldY, RecipeBuilderScreen.BUTTON_WIDTH, 20, TITLE_BTN_ADD_ENTRY, btn -> onAddEntry()));

        //Right side
        int buttonX = leftPos + imageWidth - RecipeBuilderScreen.BUTTON_WIDTH - (ClientUtils.BORDER * 2);
        int buttonY = topPos + titleLabelY + font.lineHeight + RecipeBuilderScreen.TEXT_PADDING;

        addRenderableWidget(new Button(buttonX, buttonY, RecipeBuilderScreen.BUTTON_WIDTH, 20, RecipeBuilderScreen.TITLE_BTN_BUILD, btn -> buildTag()));
        buttonY += RecipeBuilderScreen.BUTTON_INTERVAL;

        removeEntry = addRenderableWidget(new Button(buttonX, buttonY, RecipeBuilderScreen.BUTTON_WIDTH, 20, TITLE_BTN_REMOVE_ENTRY, this::removeSelected));
        removeEntry.active = false;
        buttonY += RecipeBuilderScreen.BUTTON_INTERVAL;

        addRenderableWidget(new Button(buttonX, buttonY, RecipeBuilderScreen.BUTTON_WIDTH, 20, RecipeBuilderScreen.TITLE_BTN_RESET, btn -> clearTag()));

        //Center
        List<AbstractTagEntry> entries = entryList != null ? entryList.children() : Collections.emptyList();

        int listTop = topPos + titleLabelY + font.lineHeight + RecipeBuilderScreen.TEXT_PADDING;
        int listBottom = topPos + inventoryLabelY - RecipeBuilderScreen.TEXT_PADDING;
        entryList = addWidget(new TagEntryListWidget(this, ClientUtils.INVENTORY_WIDTH, listTop, listBottom, entry -> removeEntry.active = true));
        entryList.setLeftPos(leftPos + (imageWidth / 2) - (ClientUtils.INVENTORY_WIDTH / 2));
        entries.forEach(entryList::addEntry);

        //Add tag types
        TypeEntry typeItem = buildTagTypes();
        tagType.setSelected(selected != null ? selected : typeItem, false);

        pathComponent = RecipeBuilderScreen.buildPackPathComponent();
    }

    @Override
    public void render(PoseStack pstack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(pstack);
        super.render(pstack, mouseX, mouseY, partialTicks);
        tagType.render(pstack, mouseX, mouseY, partialTicks);
        renderTooltip(pstack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack pstack, float partialTicks, int mouseX, int mouseY)
    {
        ClientUtils.drawScreenBackground(this, pstack, leftPos, topPos, imageWidth, imageHeight);

        int invX = (imageWidth / 2) - (ClientUtils.INVENTORY_WIDTH / 2);
        int invY = imageHeight - ClientUtils.BORDER - ClientUtils.INVENTORY_HEIGHT;
        ClientUtils.drawInventoryBackground(this, pstack, leftPos + invX, topPos + invY, false);

        entryList.render(pstack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (tagEntryName.isMouseOver(mouseX, mouseY) && tagType.getSelected().allowItem())
        {
            ItemStack held = menu.getCarried();
            if (!held.isEmpty() && tagType.getSelected().acceptsItem(held))
            {
                tagEntryName.setValue(tagType.getSelected().getEntryName(held));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        if (mc().options.keyInventory.matches(pKeyCode, pScanCode) && (tagName.isFocused() || tagEntryName.isFocused()))
        {
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void buildTag()
    {
        String name = tagName.getValue();

        if (name.isEmpty())
        {
            mc().pushGuiLayer(MessageScreen.error(MSG_NAME_EMPTY));
            return;
        }

        try
        {
            //Make sure the name is actually a valid ResourceLocation
            new ResourceLocation(name);
        }
        catch (ResourceLocationException e)
        {
            mc().pushGuiLayer(MessageScreen.error(new TextComponent(e.getMessage())));
            return;
        }

        List<String> entries = entryList.children().stream().map(AbstractTagEntry::getEntryName).collect(Collectors.toList());
        Exporter.exportTag(tagType.getSelected().getType(), name, entries, tagReplace.selected());

        List<Component> messages = new ArrayList<>();

        messages.add(MSG_SUCCESS);
        messages.add(pathComponent);
        if (mc().isLocalServer()) { messages.add(MSG_SUCCESS_LOCAL); }

        mc().pushGuiLayer(MessageScreen.info(messages));
    }

    private void clearTag()
    {
        tagName.setValue("");
        entryList.children().clear();

        if (tagReplace.selected())
        {
            //"Unselect" checkbox
            tagReplace.onPress();
        }
    }

    private void removeSelected(Button btn)
    {
        if (entryList.getSelected() == null) { return; }

        entryList.children().remove(entryList.getSelected());
        btn.active = false;
    }

    private void onTypeSelected(TypeEntry entry) { entryList.children().clear(); }

    private void onAddEntry()
    {
        String entry = tagEntryName.getValue();

        if (entry.isEmpty())
        {
            mc().pushGuiLayer(MessageScreen.error(MSG_ENTRY_NAME_EMPTY));
            return;
        }

        ResourceLocation entryName;
        try
        {
            entryName = new ResourceLocation(entry);
        }
        catch (ResourceLocationException e)
        {
            mc().pushGuiLayer(MessageScreen.error(new TextComponent(e.getMessage())));
            return;
        }

        if (!tagType.getSelected().getType().containsKey(entryName))
        {
            mc().pushGuiLayer(MessageScreen.error(MSG_ENTRY_UNKNOWN));
            return;
        }

        if (entryList.contains(entry))
        {
            mc().pushGuiLayer(MessageScreen.error(MSG_ENTRY_EXISTS));
            return;
        }

        entryList.addEntry(tagType.getSelected().buildListEntry(entry));

        tagEntryName.setValue("");
    }

    @SuppressWarnings("ConstantConditions")
    private TypeEntry buildTagTypes()
    {
        //Items
        TypeEntry typeItem = new TypeEntry(ForgeRegistries.ITEMS, true, ItemTagEntry::new);
        tagType.addEntry(typeItem);

        //Blocks
        Predicate<ItemStack> isBlock = stack -> stack.getItem() instanceof BlockItem;
        tagType.addEntry(new TypeEntry(ForgeRegistries.BLOCKS, true, isBlock, ItemTagEntry::new));

        //Fluids
        tagType.addEntry(new TypeEntry(ForgeRegistries.FLUIDS, true, TagBuilderScreen::containsSingleFluid, TagBuilderScreen::getContainedFluidName, FluidTagEntry::new));

        //EntityTypes
        Predicate<ItemStack> isEgg = stack -> stack.getItem() instanceof SpawnEggItem;
        Function<ItemStack, String> eggToName = stack -> ((SpawnEggItem)stack.getItem()).getType(null).getRegistryName().toString();
        tagType.addEntry(new TypeEntry(ForgeRegistries.ENTITIES, true, isEgg, eggToName, NoIconTagEntry::entity));

        //TileEnityTypes
        tagType.addEntry(new TypeEntry(ForgeRegistries.BLOCK_ENTITIES, false, NoIconTagEntry::tileEntity));

        //Effects
        Predicate<ItemStack> isPotion = stack -> stack.getItem() instanceof PotionItem && PotionUtils.getPotion(stack).getEffects().size() == 1;
        Function<ItemStack, String> potionToName = stack -> PotionUtils.getPotion(stack).getEffects().get(0).getEffect().getRegistryName().toString();
        tagType.addEntry(new TypeEntry(ForgeRegistries.MOB_EFFECTS, true, isPotion, potionToName, NoIconTagEntry::potion));

        //Enchantments
        Predicate<ItemStack> isEnchBook = stack -> stack.getItem() instanceof EnchantedBookItem && EnchantmentHelper.getEnchantments(stack).size() == 1;
        Function<ItemStack, String> bookToName = stack -> EnchantmentHelper.getEnchantments(stack).keySet().toArray(new Enchantment[0])[0].getRegistryName().toString();
        tagType.addEntry(new TypeEntry(ForgeRegistries.ENCHANTMENTS, true, isEnchBook, bookToName, NoIconTagEntry::enchantment));

        return typeItem;
    }



    private static boolean containsSingleFluid(ItemStack stack)
    {
        LazyOptional<IFluidHandlerItem> optional = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        if (!optional.isPresent())
        {
            return false;
        }

        return optional.map(handler ->
        {
            if (handler.getTanks() != 1)
            {
                return false;
            }
            return !handler.getFluidInTank(0).isEmpty();
        }).orElse(false);
    }

    private static String getContainedFluidName(ItemStack stack)
    {
        //noinspection ConstantConditions
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                .orElseThrow(IllegalStateException::new)
                .getFluidInTank(0)
                .getFluid()
                .getRegistryName()
                .toString();
    }

    private Minecraft mc()
    {
        if (minecraft == null) { throw new IllegalStateException("Screen not initialized!"); }
        return minecraft;
    }

    public Font getFont() { return font; }



    protected static class TypeEntry extends SelectionWidget.SelectionEntry
    {
        private final IForgeRegistry<?> type;
        private final boolean allowItem;
        private final Predicate<ItemStack> itemFilter;
        private final Function<ItemStack, String> nameConverter;
        private final Function<String, AbstractTagEntry> entryBuilder;

        public TypeEntry(IForgeRegistry<?> type, boolean allowItem, Function<String, AbstractTagEntry> entryBuilder)
        {
            this(type, allowItem, stack -> allowItem, entryBuilder);
        }

        public TypeEntry(IForgeRegistry<?> type, boolean allowItem, Predicate<ItemStack> itemFilter, Function<String, AbstractTagEntry> entryBuilder)
        {
            //noinspection ConstantConditions
            this(type, allowItem, itemFilter, stack -> stack.getItem().getRegistryName().toString(), entryBuilder);
        }

        public TypeEntry(IForgeRegistry<?> type, boolean allowItem, Predicate<ItemStack> itemFilter, Function<ItemStack, String> nameConverter, Function<String, AbstractTagEntry> entryBuilder)
        {
            super(Utils.translate("tag_type", type.getRegistryName().toString()));
            this.type = type;
            this.allowItem = allowItem;
            this.itemFilter = itemFilter;
            this.nameConverter = nameConverter;
            this.entryBuilder = entryBuilder;
        }

        public IForgeRegistry<?> getType() { return type; }

        public boolean allowItem() { return allowItem; }

        public boolean acceptsItem(ItemStack stack) { return itemFilter.test(stack); }

        public String getEntryName(ItemStack stack) { return nameConverter.apply(stack); }

        public AbstractTagEntry buildListEntry(String name) { return entryBuilder.apply(name); }
    }
}