package com.vulp.druidcraftrg.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.vulp.druidcraftrg.DruidcraftRegrownRegistry;
import com.vulp.druidcraftrg.inventory.container.CrateContainer;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class CrateScreen extends ContainerScreen<CrateContainer> implements IHasContainer<CrateContainer> {
    private static final ResourceLocation CONTAINER_BACKGROUND = DruidcraftRegrownRegistry.location("/textures/gui/container/scrollable_container.png");
    private final int containerRows;
    private int scrollOffset;
    private final int scrollOffsetMax = 108;
    private boolean isScrolling;
    private final boolean canScroll;
    private CrateSearchWidget searchBox;
    private boolean ignoreTextInput;
    private List<Slot> searchList = new ArrayList<>(Collections.emptyList());

    public CrateScreen(CrateContainer container, PlayerInventory playerInventory, ITextComponent displayName) {
        super(container, playerInventory, displayName);
        this.passEvents = false;
        this.containerRows = container.getRowCount();
        this.canScroll = this.containerRows > 6;
        this.imageWidth = 194;
        this.imageHeight = 221;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    public void init() {
        super.init();
        this.searchBox = new CrateSearchWidget(this.font, 99, 6, 80, 9, new TranslationTextComponent("crate.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(false);
        this.searchBox.setTextColor(16777215);
        this.children.add(this.searchBox);
        this.searchBox.setX(99);
    }

    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.searchBox.getValue();
        this.init(minecraft, width, height);
        this.searchBox.setValue(s);
        if (!this.searchBox.getValue().isEmpty()) {
            this.refreshSearchResults();
        }

    }

    public boolean charTyped(char character, int i) {
        if (this.ignoreTextInput) {
            return false;
        } else {
            String s = this.searchBox.getValue();
            if (this.searchBox.charTyped(character, i)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.refreshSearchResults();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean keyPressed(int i, int j, int k) {
        this.ignoreTextInput = false;
        boolean flag = this.hoveredSlot != null && this.hoveredSlot.hasItem();
        boolean flag1 = InputMappings.getKey(i, j).getNumericKeyValue().isPresent();
        if (flag && flag1 && this.checkHotbarKeyPressed(i, j)) {
            this.ignoreTextInput = true;
            return true;
        } else {
            String s = this.searchBox.getValue();
            if (this.searchBox.keyPressed(i, j, k)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.refreshSearchResults();
                }

                return true;
            } else {
                return this.searchBox.isFocused() && this.searchBox.isVisible() && i != 256 || super.keyPressed(i, j, k);
            }
        }
    }

    public boolean keyReleased(int i, int j, int k) {
        this.ignoreTextInput = false;
        return super.keyReleased(i, j, k);
    }

    private void refreshSearchResults() {
        this.searchList = new ArrayList<>(Collections.emptyList());
        if (!this.searchBox.getValue().isEmpty()) {
            String search = this.searchBox.getValue().toLowerCase(Locale.ROOT);
            for (Slot slot : menu.slots) {
                if (!(slot.container instanceof PlayerInventory) && slot.getItem().getItem() != Items.AIR) {
                    for (ITextComponent line : slot.getItem().getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL)) {
                        if (TextFormatting.stripFormatting(line.getString()).toLowerCase(Locale.ROOT).contains(search)) {
                            this.searchList.add(slot);
                            break;
                        }
                    }
                }
            }
        }
    }

    /*private void refreshSearchResults() {
        this.searchList = Collections.emptyList();
        if (!this.searchBox.getValue().isEmpty() || !this.searchBox.getValue().equals("")) {
            String search = this.searchBox.getValue().toLowerCase(Locale.ROOT);
            for (Slot slot : menu.slots) {
                for (ITextComponent line : slot.getItem().getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL)) {
                    if (TextFormatting.stripFormatting(line.getString()).toLowerCase(Locale.ROOT).contains(search)) {
                        if (slot != null) {
                            this.searchList.add(slot);
                        }
                        break;
                    }
                }
            }
        }
    }*/

    @Override
    public void tick() {
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
        super.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        int i = this.leftPos;
        int j = this.topPos;
        this.renderBg(matrixStack, delta, mouseX, mouseY);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawBackground(this, matrixStack, mouseX, mouseY));
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        // super.render(matrixStack, mouseX, mouseY, delta);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)i, (float)j, 0.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        this.hoveredSlot = null;
        int scroll = (int)((float)this.scrollOffset * (((float)this.containerRows - 6.0F) / 6.0F));
        int k = 240;
        int l = 240;
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean mouseInBounds = mouseY > this.topPos + 17 && mouseY < this.topPos + 124;
        // Slot Rendering!
        List<Slot> playerInventorySlots = new java.util.ArrayList<>(Collections.emptyList());
        int ticker = 0;
        for(int i1 = 0; i1 < this.menu.slots.size(); ++i1) {

            // Item and highlight rendering.
            Slot slot = this.menu.slots.get(i1);
            int slotY = slot.y;
            boolean isPlayerInventory = slot.container instanceof PlayerInventory;
            if (!isPlayerInventory) {
                if (slot.isActive()) {
                    this.renderSlotScrollable(matrixStack, slot, scroll);
                }
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                // Highlight, but only if not searching OR search result is correct.
                boolean flag2 = this.searchBox.getValue().isEmpty();
                if (this.isHovering(slot, (double) mouseX, (double) mouseY) && slot.isActive()) {
                    this.hoveredSlot = slot;
                    boolean flag1 = this.searchList.contains(this.hoveredSlot);
                    if (flag2 || flag1) {
                        int k1 = slotY - scroll - 219;
                        if (k1 > 1 && k1 < 123 && mouseInBounds) {
                            int j1 = slot.x;
                            int slotColor = this.getSlotColor(i1);
                            this.fillGradient(matrixStack, j1, MathHelper.clamp(k1, 18, 124), j1 + 16, MathHelper.clamp(k1 + 16, 18, 124), slotColor, slotColor);
                        }
                    }
                }
                // Grey the wrong results.
                if (!flag2) {
                    if (!this.searchList.contains(slot)) {
                        int k1 = slotY - scroll - 220;
                        if (k1 > 0 && k1 < 123) {
                            int j1 = slot.x - 1;
                            this.fillGradient(matrixStack, j1, MathHelper.clamp(k1, 18, 124), j1 + 18, MathHelper.clamp(k1 + 18, 18, 124), -1072689136, -1072689136);
                        }
                    }
                }
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
                ticker++;
            }
            if (isPlayerInventory) {
                playerInventorySlots.add(slot);
            }

        }

        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();




        // The split here sorts out some weird rendering with the items. I have no clue how rendering works, so for now this works as a messy fix.



        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        // super.render(matrixStack, mouseX, mouseY, delta);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)i, (float)j, 0.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);

        renderSearchHighlights(matrixStack, delta, mouseX, mouseY);

        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;
        for (Slot slot : this.searchList) {
            ItemStack slotItem = slot.getItem();
            int stackSize = Math.min(slotItem.getMaxStackSize(), slot.getMaxStackSize(slotItem));
            String s = null;
            if (slotItem.getCount() > stackSize) {
                s = TextFormatting.YELLOW.toString() + stackSize;
                slotItem.setCount(stackSize);
            }
            int slotPos = slot.y - (int) ((float) this.scrollOffset * (((float) this.containerRows - 6.0F) / 6.0F)) - 220;
            if (slotPos > 1 && slotPos < 123) {
                this.itemRenderer.renderGuiItemDecorations(this.font, slotItem, slot.x, slotPos + 1, s);
            }
        }

        // TODO: Labels need sorted! Dragged items also need done.

        this.setBlitOffset(250);
        this.itemRenderer.blitOffset = 250.0F;
        renderForeground(matrixStack, delta, mouseX, mouseY);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0F;
        this.renderLabels(matrixStack, mouseX, mouseY);



        for (int i1 = 0; i1 < playerInventorySlots.size(); i1++) {
            Slot slot = this.menu.slots.get(i1 + ticker);
            int slotY = slot.y;
            if (slot.isActive()) {
                this.renderSlot(matrixStack, slot);
            }
            if (this.isHovering(slot, (double) mouseX, (double) mouseY) && slot.isActive()) {
                this.hoveredSlot = slot;
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                int slotColor = this.getSlotColor(i1 + ticker);
                this.fillGradient(matrixStack, slot.x, slotY, slot.x + 16, slotY + 16, slotColor, slotColor);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, matrixStack, mouseX, mouseY));
        PlayerInventory playerinventory = this.minecraft.player.inventory;
        ItemStack itemstack = this.draggingItem.isEmpty() ? playerinventory.getCarried() : this.draggingItem;
        if (!itemstack.isEmpty()) {
            int j2 = 8;
            int k2 = this.draggingItem.isEmpty() ? 8 : 16;
            String s = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                itemstack = itemstack.copy();
                itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0F));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                itemstack = itemstack.copy();
                itemstack.setCount(this.quickCraftingRemainder);
                if (itemstack.isEmpty()) {
                    s = "" + TextFormatting.YELLOW + "0";
                }
            }

            this.renderFloatingItem(itemstack, mouseX - i - 8, mouseY - j - k2, s);
        }

        if (!this.snapbackItem.isEmpty()) {
            float f = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
            if (f >= 1.0F) {
                f = 1.0F;
                this.snapbackItem = ItemStack.EMPTY;
            }

            int l2 = this.snapbackEnd.x - this.snapbackStartX;
            int i3 = this.snapbackEnd.y - this.snapbackStartY;
            int l1 = this.snapbackStartX + (int)((float)l2 * f);
            int i2 = this.snapbackStartY + (int)((float)i3 * f);
            this.renderFloatingItem(this.snapbackItem, l1, i2, (String)null);
        }

        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();

        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    protected void renderLabels(MatrixStack matrixStack, int p_230451_2_, int p_230451_3_) {
        matrixStack.translate(0.0D, 0.0D, 250.0D);
        this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
        matrixStack.translate(0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (!(hoveredSlot.container instanceof PlayerInventory) && !(mouseY > this.topPos + 17 && mouseY < this.topPos + 124)) {
                return;
            }
            this.renderTooltip(matrixStack, this.hoveredSlot.getItem(), mouseX, mouseY);
        }

    }

    public boolean mouseScrolled(double x, double y, double scrollAmount) {
        if (!this.canScroll) {
            return false;
        } else {
            this.scrollOffset = (int) (this.scrollOffset - scrollAmount * Math.abs(this.containerRows - 21));
            this.scrollOffset = MathHelper.clamp(this.scrollOffset, 0, this.scrollOffsetMax);
            //this.scrollTo(this.scrollOffset);
            return true;
        }
    }

    /*public void scrollTo(float p_148329_1_) {
        int i = (this.containerRows - 6);
        int j = (int)((double)(p_148329_1_ * (float)i) + 0.5D);
        if (j < 0) {
            j = 0;
        }

        for(int k = 0; k < 5; ++k) {
            for(int l = 0; l < 9; ++l) {
                int i1 = l + (k + j) * 9;
                if (i1 >= 0 && i1 < this.items.size()) {
                    CreativeScreen.CONTAINER.setItem(l + k * 9, this.items.get(i1));
                } else {
                    CreativeScreen.CONTAINER.setItem(l + k * 9, ItemStack.EMPTY);
                }
            }
        }

    }

    public void scrollTo(int offset) {
        int i = (this.containerRows - 6);
        // Scroll maths?
    }*/

    public boolean mouseClicked(double x, double y, int buttonNumber) {
        if (buttonNumber == 0) {
            if (this.insideSearchBox(x, y)) {
                this.searchBox.setFocus(true);
                this.searchBox.setCanLoseFocus(false);
                this.searchBox.setVisible(true);
                return true;
            } else {
                this.searchBox.setCanLoseFocus(true);
                this.searchBox.setFocus(false);
            }
            if (this.insideScrollbar(x, y)) {
                this.isScrolling = this.canScroll;
                return true;
            }
        }
        return super.mouseClicked(x, y, buttonNumber);
    }

    public boolean mouseDragged(double x, double y, int buttonNumber, double xMovement, double yMovement) {
        if (this.isScrolling) {
            int i = this.topPos + 18;
            int j = i + 197;
            this.scrollOffset = (int)((float)this.scrollOffsetMax * ((float)y - (float)i - 7.5F) / ((float)(j - i) - 15.0F));
            this.scrollOffset = MathHelper.clamp(this.scrollOffset, 0, this.scrollOffsetMax);
            // scrollTo(this.scrollOffset);
            return true;
        } else {
            return super.mouseDragged(x, y, buttonNumber, xMovement, yMovement);
        }
    }

    public boolean mouseReleased(double x, double y, int buttonNumber) {
        if (buttonNumber == 0) {
            this.isScrolling = false;
        }
        return super.mouseReleased(x, y, buttonNumber);
    }

    protected boolean insideScrollbar(double x, double y) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 174;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 197;
        return x >= (double)k && y >= (double)l && x < (double)i1 && y < (double)j1;
    }

    protected boolean insideSearchBox(double x, double y) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 98;
        int l = j + 5;
        int i1 = k + 90;
        int j1 = l + 12;
        return x >= (double)k && y >= (double)l && x < (double)i1 && y < (double)j1;
    }

    // Item rendering!
    private void renderSlot(MatrixStack matrixStack, Slot slot) {
        int i = slot.x;
        int j = slot.y;
        ItemStack itemstack = slot.getItem();
        boolean flag = false;
        boolean flag1 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemstack1 = this.minecraft.player.inventory.getCarried();
        String s = null;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemstack1.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (Container.canItemQuickReplace(slot, itemstack1, true) && this.menu.canDragTo(slot)) {
                itemstack = itemstack1.copy();
                flag = true;
                Container.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack, slot.getItem().isEmpty() ? 0 : slot.getItem().getCount());
                int k = Math.min(itemstack.getMaxStackSize(), slot.getMaxStackSize(itemstack));
                if (itemstack.getCount() > k) {
                    s = TextFormatting.YELLOW.toString() + k;
                    itemstack.setCount(k);
                }
            } else {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining();
            }
        }

        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;
        if (itemstack.isEmpty() && slot.isActive()) {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
            if (pair != null) {
                TextureAtlasSprite textureatlassprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                this.minecraft.getTextureManager().bind(textureatlassprite.atlas().location());
                blit(matrixStack, i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
                flag1 = true;
            }
        }

        if (!flag1) {
            if (flag) {
                fill(matrixStack, i, j, i + 16, j + 16, -2130706433);
            }

            RenderSystem.enableDepthTest();
            this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemstack, i, j);
            this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, i, j, s);
        }

        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    // Scrollable item rendering!
    private void renderSlotScrollable(MatrixStack matrixStack, Slot slot, int scrollValue) {
        if (slot.y - 219 - scrollValue > 1 && slot.y - 219 - scrollValue < 123) {
            int i = slot.x;
            int j = slot.y - scrollValue - 219;
            ItemStack itemstack = slot.getItem();
            boolean flag = false;
            boolean flag1 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
            ItemStack itemstack1 = this.minecraft.player.inventory.getCarried();
            String s = null;
            if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
                itemstack = itemstack.copy();
                itemstack.setCount(itemstack.getCount() / 2);
            } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemstack1.isEmpty()) {
                if (this.quickCraftSlots.size() == 1) {
                    return;
                }

                if (Container.canItemQuickReplace(slot, itemstack1, true) && this.menu.canDragTo(slot)) {
                    itemstack = itemstack1.copy();
                    flag = true;
                    Container.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack, slot.getItem().isEmpty() ? 0 : slot.getItem().getCount());
                    int k = Math.min(itemstack.getMaxStackSize(), slot.getMaxStackSize(itemstack));
                    if (itemstack.getCount() > k) {
                        s = TextFormatting.YELLOW.toString() + k;
                        itemstack.setCount(k);
                    }
                } else {
                    this.quickCraftSlots.remove(slot);
                    this.recalculateQuickCraftRemaining();
                }
            }

            this.setBlitOffset(100);
            this.itemRenderer.blitOffset = 100.0F;
            if (itemstack.isEmpty() && slot.isActive()) {
                Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
                if (pair != null) {
                    TextureAtlasSprite textureatlassprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                    this.minecraft.getTextureManager().bind(textureatlassprite.atlas().location());
                    blit(matrixStack, i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
                    flag1 = true;
                }
            }
            if (!flag1) {
                if (flag) {
                    fill(matrixStack, i, j, i + 16, j + 16, -2130706433);
                }

                RenderSystem.enableDepthTest();
                this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemstack, i, j);
                this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, i, j, s);
            }

            this.itemRenderer.blitOffset = 0.0F;
            this.setBlitOffset(0);
        }
    }

    private boolean isHovering(Slot slot, double mouseX, double mouseY) {
        if (slot.container instanceof PlayerInventory) {
            return this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
        }
        else return this.isHovering(slot.x, slot.y - 219 - (int)((float)this.scrollOffset * (((float)this.containerRows - 6.0F) / 6.0F)), 16, 16, mouseX, mouseY);
    }

    protected boolean isHovering(int slotX, int slotY, int slotWidth, int slotHeight, double mouseX, double mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        mouseX = mouseX - (double)i;
        mouseY = mouseY - (double)j;
        return mouseX >= (double)(slotX - 1) && mouseX < (double)(slotX + slotWidth + 1) && mouseY >= (double)(slotY - 1) && mouseY < (double)(slotY + slotHeight + 1);
    }

    protected void slotClicked(@Nullable Slot slot, int slotNumber, int buttonNumber, ClickType clickType) {
        if (slot != null) {
            this.searchBox.moveCursorToEnd();
            this.searchBox.setHighlightPos(0);
            boolean flag = slot.container instanceof PlayerInventory;
            if (!flag & (slot.y < 0 || slot.y - 219 - (int)((float)this.scrollOffset * (((float)this.containerRows - 6.0F) / 6.0F)) > 124)) {
                return;
            }
        }
        this.searchBox.moveCursorToEnd();
        this.searchBox.setHighlightPos(this.searchBox.getCursorPosition());
        this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, slotNumber, buttonNumber, clickType, this.minecraft.player);
        refreshSearchResults();
    }

    public Slot findSlot(double mouseX, double mouseY) {
        for(int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = this.menu.slots.get(i);
            if (this.isHovering(slot, mouseX, mouseY) && slot.isActive() && (slot.container instanceof PlayerInventory || (mouseY > this.topPos + 18 && mouseY < this.topPos + 124))) {
                return slot;
            }
        }

        return null;
    }


    private void renderFloatingItem(ItemStack stack, int mouseX, int mouseY, String string) {
        RenderSystem.translatef(0.0F, 0.0F, 32.0F);
        this.setBlitOffset(200);
        this.itemRenderer.blitOffset = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = this.font;
        this.itemRenderer.renderAndDecorateItem(stack, mouseX, mouseY);
        this.itemRenderer.renderGuiItemDecorations(font, stack, mouseX, mouseY - (this.draggingItem.isEmpty() ? 0 : 8), string);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0F;
    }

    private void recalculateQuickCraftRemaining() {
        ItemStack itemstack = this.minecraft.player.inventory.getCarried();
        if (!itemstack.isEmpty() && this.isQuickCrafting) {
            if (this.quickCraftingType == 2) {
                this.quickCraftingRemainder = itemstack.getMaxStackSize();
            } else {
                this.quickCraftingRemainder = itemstack.getCount();

                for(Slot slot : this.quickCraftSlots) {
                    ItemStack itemstack1 = itemstack.copy();
                    ItemStack itemstack2 = slot.getItem();
                    int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
                    Container.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack1, i);
                    int j = Math.min(itemstack1.getMaxStackSize(), slot.getMaxStackSize(itemstack1));
                    if (itemstack1.getCount() > j) {
                        itemstack1.setCount(j);
                    }

                    this.quickCraftingRemainder -= itemstack1.getCount() - i;
                }

            }
        }
    }

    protected void renderSearchHighlights(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(CONTAINER_BACKGROUND);RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i = 0;
        int j = 0;
        List<Integer> slotNumbers = new ArrayList<>(Collections.emptyList());
        for (Slot slot : searchList) {
            slotNumbers.add(slot.index);
        }
        for (int y = 0; y < this.containerRows; y++) {
            int yPos = j + 17 + y * 18 - (int)((float)this.scrollOffset * (((float)this.containerRows - 6.0F) / 6.0F));
            if (yPos < j || yPos > j + 123) {
                continue;
            }
            int ySize = yPos < 18 ? Math.abs(yPos - 20) : 20;
            for (int x = 0; x < 9; x++) {
                if (slotNumbers.contains(x + y * 9)) {
                    if (ySize != 20) {
                        this.blit(matrixStack, i + 7 + x * 18 - 1, j + 19 - 1, 194, 38 - yPos - 1, 20, yPos + 1);
                    } else {
                        this.blit(matrixStack, i + 7 + x * 18 - 1, yPos - (yPos == 0 ? 0 : 1), 194, yPos == 0 ? 19 : 18, 20, yPos == 0 ? 19 : 20);
                    }
                }
            }
        }
    }

    protected void renderForeground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(CONTAINER_BACKGROUND);
        this.blit(matrixStack, 0, 0, 0, 0, 194, 18);
        this.blit(matrixStack, 0, 18, 0, 18, 8, 106);
        this.blit(matrixStack, 168, 18, 168, 18, 26, 106);
        this.blit(matrixStack, 0, 124, 0, 124, 194, 97);
        this.blit(matrixStack, 174, 18 + (int)(180.0F * ((float)this.scrollOffset / (float)this.scrollOffsetMax)), this.canScroll ? 0 : 12, 221, 12, 15);
        matrixStack.translate(0.0D, 0.0D, 300.0D);
        this.searchBox.render(matrixStack, mouseX, mouseY, delta);
        matrixStack.translate(0.0D, 0.0D, -200.0D);
    }

    protected void renderBg(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(CONTAINER_BACKGROUND);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i + 8, j + 18, 8, 18, 160, 106);
        for (int y = 0; y < this.containerRows; y++) {
            int yPos = j + 17 + y * 18 - (int)((float)this.scrollOffset * (((float)this.containerRows - 6.0F) / 6.0F));
            if (yPos < j || yPos > j + 123) {
                continue;
            }
            int ySize = yPos < 18 ? Math.abs(yPos - 18) : 18;
            for (int x = 0; x < 9; x++) {
                this.blit(matrixStack, i + 7 + x * 18, (ySize != 18 ? j + 18 : yPos), 194, 18 - ySize, 18, ySize);
            }
        }

    }

    private class CrateSearchWidget extends TextFieldWidget {

        public CrateSearchWidget(FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent text) {
            super(fontRenderer, x, y, width, height, text);
        }

        @Override
        public void tick() {
            super.tick();
            this.setTextColor(!this.getValue().isEmpty() && searchList.isEmpty() ? 16711680 : 16777215);
        }


    }


}