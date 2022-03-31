package com.vulp.druidcraftrg.client.gui.screen.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.vulp.druidcraftrg.DruidcraftRegrownRegistry;
import com.vulp.druidcraftrg.inventory.container.CrateContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.client.event.ContainerScreenEvent;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class CrateScreen extends AbstractContainerScreen<CrateContainer> implements MenuAccess<CrateContainer> {
    private static final ResourceLocation CONTAINER_BACKGROUND = DruidcraftRegrownRegistry.location("/textures/gui/container/scrollable_container.png");
    private final int containerRows;
    private int scrollOffset;
    private final int scrollOffsetMax = 108;
    private boolean isScrolling;
    private final boolean canScroll;
    private CrateSearchWidget searchBox;
    private boolean ignoreTextInput;
    private List<Slot> searchList = new ArrayList<>(Collections.emptyList());

    public CrateScreen(CrateContainer container, Inventory playerInventory, Component displayName) {
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
        this.searchBox = new CrateSearchWidget(this.font, 99, 6, 80, 9, new TranslatableComponent("crate.search"));
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
        boolean flag1 = InputConstants.getKey(i, j).getNumericKeyValue().isPresent();
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
                if (!(slot.container instanceof Inventory) && slot.getItem().getItem() != Items.AIR) {
                    for (Component line : slot.getItem().getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL)) {
                        if (ChatFormatting.stripFormatting(line.getString()).toLowerCase(Locale.ROOT).contains(search)) {
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
    protected void containerTick() {
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
        super.containerTick();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(poseStack);
        int i = this.leftPos;
        int j = this.topPos;
        this.renderBg(poseStack, delta, mouseX, mouseY);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new ContainerScreenEvent.DrawBackground(this, poseStack, mouseX, mouseY));
        RenderSystem.disableDepthTest();
        // super.render(poseStack, mouseX, mouseY, delta);
        PoseStack mainPose = RenderSystem.getModelViewStack();
        mainPose.pushPose();
        mainPose.translate((float)i, (float)j, 0.0F);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.hoveredSlot = null;
        this.hoveredSlot = null;
        int scroll = (int)((float)this.scrollOffset * (((float)this.containerRows - 6.0F) / 6.0F));
        int k = 240;
        int l = 240;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        boolean mouseInBounds = mouseY > this.topPos + 17 && mouseY < this.topPos + 124;
        // Slot Rendering!
        List<Slot> playerInventorySlots = new java.util.ArrayList<>(Collections.emptyList());
        int ticker = 0;
        for(int i1 = 0; i1 < this.menu.slots.size(); ++i1) {

            // Item and highlight rendering.
            Slot slot = this.menu.slots.get(i1);
            int slotY = slot.y;
            boolean isPlayerInventory = slot.container instanceof Inventory;
            if (!isPlayerInventory) {
                if (slot.isActive()) {
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    this.renderSlotScrollable(poseStack, slot, scroll);
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
                            this.fillGradient(poseStack, j1, Mth.clamp(k1, 18, 124), j1 + 16, Mth.clamp(k1 + 16, 18, 124), slotColor, slotColor);
                        }
                    }
                }
                // Grey the wrong results.
                if (!flag2) {
                    if (!this.searchList.contains(slot)) {
                        int k1 = slotY - scroll - 220;
                        if (k1 > 0 && k1 < 123) {
                            int j1 = slot.x - 1;
                            this.fillGradient(poseStack, j1, Mth.clamp(k1, 18, 124), j1 + 18, Mth.clamp(k1 + 18, 18, 124), -1072689136, -1072689136);
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
        
        RenderSystem.applyModelViewMatrix();
        mainPose.popPose();
        RenderSystem.enableDepthTest();




        // The split here sorts out some weird rendering with the items. I have no clue how rendering works, so for now this works as a messy fix.



        RenderSystem.disableDepthTest();
        // super.render(poseStack, mouseX, mouseY, delta);
        mainPose.pushPose();
        mainPose.translate((float)i, (float)j, 0.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        renderSearchHighlights(poseStack, delta, mouseX, mouseY);

        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;
        for (Slot slot : this.searchList) {
            ItemStack slotItem = slot.getItem();
            int stackSize = Math.min(slotItem.getMaxStackSize(), slot.getMaxStackSize(slotItem));
            String s = null;
            if (slotItem.getCount() > stackSize) {
                s = ChatFormatting.YELLOW.toString() + stackSize;
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
        renderForeground(poseStack, delta, mouseX, mouseY);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0F;
        this.renderLabels(poseStack, mouseX, mouseY);



        for (int i1 = 0; i1 < playerInventorySlots.size(); i1++) {
            Slot slot = this.menu.slots.get(i1 + ticker);
            int slotY = slot.y;
            if (slot.isActive()) {
                this.renderSlot(poseStack, slot);
            }
            if (this.isHovering(slot, (double) mouseX, (double) mouseY) && slot.isActive()) {
                this.hoveredSlot = slot;
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                int slotColor = this.getSlotColor(i1 + ticker);
                this.fillGradient(poseStack, slot.x, slotY, slot.x + 16, slotY + 16, slotColor, slotColor);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new ContainerScreenEvent.DrawForeground(this, poseStack, mouseX, mouseY));
        InventoryMenu playerinventory = this.minecraft.player.inventoryMenu;
        ItemStack itemstack = this.draggingItem.isEmpty() ? playerinventory.getCarried() : this.draggingItem;
        if (!itemstack.isEmpty()) {
            int j2 = 8;
            int k2 = this.draggingItem.isEmpty() ? 8 : 16;
            String s = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                itemstack = itemstack.copy();
                itemstack.setCount(Mth.ceil((float)itemstack.getCount() / 2.0F));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                itemstack = itemstack.copy();
                itemstack.setCount(this.quickCraftingRemainder);
                if (itemstack.isEmpty()) {
                    s = "" + ChatFormatting.YELLOW + "0";
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

        mainPose.popPose();
        RenderSystem.enableDepthTest();

        this.renderTooltip(poseStack, mouseX, mouseY);

        // TODO: You haven't touched this code, just split it into chunks and attempted to split the slots into two, which you commented a better way of doing.
        // Split between old code and fresh code:
        if (!true) {
            this.renderBg(poseStack, delta, mouseX, mouseY);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ContainerScreenEvent.DrawBackground(this, poseStack, mouseX, mouseY));
            RenderSystem.disableDepthTest();
            super.render(poseStack, mouseX, mouseY, delta);
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            posestack.translate((double)i, (double)j, 0.0D);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.hoveredSlot = null;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            List<Slot> playerSlots = new java.util.ArrayList<>(Collections.emptyList());
            List<Slot> crateSlots = new java.util.ArrayList<>(Collections.emptyList());
            // Don't do this! Do a check regarding how many rows container has, and judge slots off of that.
            for (Slot slot : this.menu.slots) {
                if (slot.container instanceof InventoryMenu) {
                    playerSlots.add(slot);
                } else {
                    crateSlots.add(slot);
                }
            }
            // RENDER SLOTS AND THE HIGHLIGHTS IF APPLICABLE -----------------------------------------------------------
            for(int k = 0; k < this.menu.slots.size(); ++k) {
                Slot slot = this.menu.slots.get(k);
                if (slot.isActive()) {
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    this.renderSlot(poseStack, slot);
                }

                if (this.isHovering(slot, (double)mouseX, (double)mouseY) && slot.isActive()) {
                    this.hoveredSlot = slot;
                    int l = slot.x;
                    int i1 = slot.y;
                    renderSlotHighlight(poseStack, l, i1, this.getBlitOffset(), this.getSlotColor(k));
                }
            }
            // ---------------------------------------------------------------------------------------------------------

            // Rendering of inventory titles ---------------------------------------------------------------------------
            this.renderLabels(poseStack, mouseX, mouseY);
            // ---------------------------------------------------------------------------------------------------------

            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ContainerScreenEvent.DrawForeground(this, poseStack, mouseX, mouseY));
            // Renders the currently mouse-held stack ------------------------------------------------------------------
            ItemStack currentStack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
            if (!currentStack.isEmpty()) {
                int l1 = 8;
                int i2 = this.draggingItem.isEmpty() ? 8 : 16;
                String s = null;
                if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                    currentStack = currentStack.copy();
                    currentStack.setCount(Mth.ceil((float)currentStack.getCount() / 2.0F));
                } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                    currentStack = currentStack.copy();
                    currentStack.setCount(this.quickCraftingRemainder);
                    if (currentStack.isEmpty()) {
                        s = ChatFormatting.YELLOW + "0";
                    }
                }

                this.renderFloatingItem(currentStack, mouseX - i - 8, mouseY - j - i2, s);
            }
            if (!this.snapbackItem.isEmpty()) {
                float f = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
                if (f >= 1.0F) {
                    f = 1.0F;
                    this.snapbackItem = ItemStack.EMPTY;
                }

                int j2 = this.snapbackEnd.x - this.snapbackStartX;
                int k2 = this.snapbackEnd.y - this.snapbackStartY;
                int j1 = this.snapbackStartX + (int)((float)j2 * f);
                int k1 = this.snapbackStartY + (int)((float)k2 * f);
                this.renderFloatingItem(this.snapbackItem, j1, k1, (String)null);
            }
            // ---------------------------------------------------------------------------------------------------------

            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.enableDepthTest();
        }

    }

    protected void renderLabels(PoseStack poseStack, int p_230451_2_, int p_230451_3_) {
        poseStack.translate(0.0D, 0.0D, 250.0D);
        this.font.draw(poseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        this.font.draw(poseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
        poseStack.translate(0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        if (this.minecraft.player.inventoryMenu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (!(hoveredSlot.container instanceof InventoryMenu) && !(mouseY > this.topPos + 17 && mouseY < this.topPos + 124)) {
                return;
            }
            this.renderTooltip(poseStack, this.hoveredSlot.getItem(), mouseX, mouseY);
        }

    }

    public boolean mouseScrolled(double x, double y, double scrollAmount) {
        if (!this.canScroll) {
            return false;
        } else {
            this.scrollOffset = (int) (this.scrollOffset - scrollAmount * Math.abs(this.containerRows - 21));
            this.scrollOffset = Mth.clamp(this.scrollOffset, 0, this.scrollOffsetMax);
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
            this.scrollOffset = Mth.clamp(this.scrollOffset, 0, this.scrollOffsetMax);
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
    private void renderSlot(PoseStack poseStack, Slot slot) {
        int i = slot.x;
        int j = slot.y;
        ItemStack itemstack = slot.getItem();
        boolean flag = false;
        boolean flag1 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemstack1 = this.minecraft.player.inventoryMenu.getCarried();
        String s = null;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemstack1.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (AbstractContainerMenu.canItemQuickReplace(slot, itemstack1, true) && this.menu.canDragTo(slot)) {
                itemstack = itemstack1.copy();
                flag = true;
                AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack, slot.getItem().isEmpty() ? 0 : slot.getItem().getCount());
                int k = Math.min(itemstack.getMaxStackSize(), slot.getMaxStackSize(itemstack));
                if (itemstack.getCount() > k) {
                    s = ChatFormatting.YELLOW.toString() + k;
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
                RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
                blit(poseStack, i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
                flag1 = true;
            }
        }

        if (!flag1) {
            if (flag) {
                fill(poseStack, i, j, i + 16, j + 16, -2130706433);
            }

            RenderSystem.enableDepthTest();
            this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemstack, i, j, 0);
            this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, i, j, s);
        }

        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    // Scrollable item rendering!
    private void renderSlotScrollable(PoseStack poseStack, Slot slot, int scrollValue) {
        if (slot.y - 219 - scrollValue > 1 && slot.y - 219 - scrollValue < 123) {
            int i = slot.x;
            int j = slot.y - scrollValue - 219;
            ItemStack itemstack = slot.getItem();
            boolean flag = false;
            boolean flag1 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
            ItemStack itemstack1 = this.minecraft.player.inventoryMenu.getCarried();
            String s = null;
            if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
                itemstack = itemstack.copy();
                itemstack.setCount(itemstack.getCount() / 2);
            } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemstack1.isEmpty()) {
                if (this.quickCraftSlots.size() == 1) {
                    return;
                }

                if (AbstractContainerMenu.canItemQuickReplace(slot, itemstack1, true) && this.menu.canDragTo(slot)) {
                    itemstack = itemstack1.copy();
                    flag = true;
                    AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack, slot.getItem().isEmpty() ? 0 : slot.getItem().getCount());
                    int k = Math.min(itemstack.getMaxStackSize(), slot.getMaxStackSize(itemstack));
                    if (itemstack.getCount() > k) {
                        s = ChatFormatting.YELLOW.toString() + k;
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
                    RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
                    blit(poseStack, i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
                    flag1 = true;
                }
            }
            if (!flag1) {
                if (flag) {
                    fill(poseStack, i, j, i + 16, j + 16, -2130706433);
                }

                RenderSystem.enableDepthTest();
                this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemstack, i, j, 0);
                this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, i, j, s);
            }

            this.itemRenderer.blitOffset = 0.0F;
            this.setBlitOffset(0);
        }
    }

    private boolean isHovering(Slot slot, double mouseX, double mouseY) {
        if (slot.container instanceof InventoryMenu) {
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
            boolean flag = slot.container instanceof InventoryMenu;
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
            if (this.isHovering(slot, mouseX, mouseY) && slot.isActive() && (slot.container instanceof InventoryMenu || (mouseY > this.topPos + 18 && mouseY < this.topPos + 124))) {
                return slot;
            }
        }

        return null;
    }

    private void renderFloatingItem(ItemStack stack, int mouseX, int mouseY, String string) {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.translate(0.0D, 0.0D, 32.0D);
        RenderSystem.applyModelViewMatrix();
        this.setBlitOffset(200);
        this.itemRenderer.blitOffset = 200.0F;
        Font font = RenderProperties.get(stack).getFont(stack);
        if (font == null) font = this.font;
        this.itemRenderer.renderAndDecorateItem(stack, mouseX, mouseY);
        this.itemRenderer.renderGuiItemDecorations(font, stack, mouseX, mouseY - (this.draggingItem.isEmpty() ? 0 : 8), string);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0F;
    }

    private void recalculateQuickCraftRemaining() {
        ItemStack itemstack = this.minecraft.player.inventoryMenu.getCarried();
        if (!itemstack.isEmpty() && this.isQuickCrafting) {
            if (this.quickCraftingType == 2) {
                this.quickCraftingRemainder = itemstack.getMaxStackSize();
            } else {
                this.quickCraftingRemainder = itemstack.getCount();

                for(Slot slot : this.quickCraftSlots) {
                    ItemStack itemstack1 = itemstack.copy();
                    ItemStack itemstack2 = slot.getItem();
                    int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
                    AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack1, i);
                    int j = Math.min(itemstack1.getMaxStackSize(), slot.getMaxStackSize(itemstack1));
                    if (itemstack1.getCount() > j) {
                        itemstack1.setCount(j);
                    }

                    this.quickCraftingRemainder -= itemstack1.getCount() - i;
                }

            }
        }
    }

    protected void renderSearchHighlights(PoseStack poseStack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
                        this.blit(poseStack, i + 7 + x * 18 - 1, j + 19 - 1, 194, 38 - yPos - 1, 20, yPos + 1);
                    } else {
                        this.blit(poseStack, i + 7 + x * 18 - 1, yPos - (yPos == 0 ? 0 : 1), 194, yPos == 0 ? 19 : 18, 20, yPos == 0 ? 19 : 20);
                    }
                }
            }
        }
    }

    protected void renderForeground(PoseStack poseStack, float delta, int mouseX, int mouseY) {
        PoseStack mainPose = RenderSystem.getModelViewStack();
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        this.blit(poseStack, 0, 0, 0, 0, 194, 18);
        this.blit(poseStack, 0, 18, 0, 18, 8, 106);
        this.blit(poseStack, 168, 18, 168, 18, 26, 106);
        this.blit(poseStack, 0, 124, 0, 124, 194, 97);
        this.blit(poseStack, 174, 18 + (int)(180.0F * ((float)this.scrollOffset / (float)this.scrollOffsetMax)), this.canScroll ? 0 : 12, 221, 12, 15);
        mainPose.translate(0.0D, 0.0D, 300.0D);
        this.searchBox.render(poseStack, mouseX, mouseY, delta);
        mainPose.translate(0.0D, 0.0D, -200.0D);
    }

    protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(poseStack, i + 8, j + 18, 8, 18, 160, 106);
        for (int y = 0; y < this.containerRows; y++) {
            int yPos = j + 17 + y * 18 - (int)((float)this.scrollOffset * (((float)this.containerRows - 6.0F) / 6.0F));
            if (yPos < j || yPos > j + 123) {
                continue;
            }
            int ySize = yPos < 18 ? Math.abs(yPos - 18) : 18;
            for (int x = 0; x < 9; x++) {
                this.blit(poseStack, i + 7 + x * 18, (ySize != 18 ? j + 18 : yPos), 194, 18 - ySize, 18, ySize);
            }
        }

    }

    private class CrateSearchWidget extends EditBox {

        public CrateSearchWidget(Font fontRenderer, int x, int y, int width, int height, TranslatableComponent text) {
            super(fontRenderer, x, y, width, height, text);
        }

        @Override
        public void tick() {
            super.tick();
            this.setTextColor(!this.getValue().isEmpty() && searchList.isEmpty() ? 16711680 : 16777215);
        }


    }


}