package com.direwolf20.laserio.client.screens;

import com.direwolf20.laserio.client.screens.widgets.ChannelButton;
import com.direwolf20.laserio.client.screens.widgets.DireButton;
import com.direwolf20.laserio.common.LaserIO;
import com.direwolf20.laserio.common.containers.CardItemContainer;
import com.direwolf20.laserio.common.containers.customslot.CardItemSlot;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.common.items.cards.CardItem;
import com.direwolf20.laserio.common.items.filters.BaseFilter;
import com.direwolf20.laserio.common.network.PacketHandler;
import com.direwolf20.laserio.common.network.packets.PacketOpenFilter;
import com.direwolf20.laserio.common.network.packets.PacketUpdateCard;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CardItemScreen extends AbstractContainerScreen<CardItemContainer> {
    private final ResourceLocation GUI = new ResourceLocation(LaserIO.MODID, "textures/gui/itemcard.png");

    protected final CardItemContainer container;
    private byte currentMode;
    private byte currentChannel;
    private byte currentItemExtractAmt;
    private ItemStack card;
    //private boolean isWhitelist;
    //private boolean isNBTFilter;

    public CardItemScreen(CardItemContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        this.container = container;
        this.card = container.cardItem;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void init() {
        super.init();
        List<AbstractWidget> leftWidgets = new ArrayList<>();

        currentMode = BaseCard.getTransferMode(card);
        currentChannel = BaseCard.getChannel(card);
        currentItemExtractAmt = BaseCard.getItemExtractAmt(card);

        int baseX = width / 2, baseY = height / 2;
        int left = baseX - 85;
        int top = baseY - 80;

        DireButton plusButton = new DireButton(left + 130, top + 11, 10, 10, new TranslatableComponent("-"), (button) -> {
            int change = -1;
            if (Screen.hasShiftDown()) change *= 10;
            if (Screen.hasControlDown()) change *= 64;
            currentItemExtractAmt = (byte) (Math.max(currentItemExtractAmt + change, 1));
        });
        DireButton minusButton = new DireButton(left + 155, top + 11, 10, 10, new TranslatableComponent("+"), (button) -> {
            int change = 1;
            if (Screen.hasShiftDown()) change *= 10;
            if (Screen.hasControlDown()) change *= 64;
            currentItemExtractAmt = (byte) (Math.min(currentItemExtractAmt + change, 64));
        });

        leftWidgets.add(new Button(left, top, 50, 20, new TranslatableComponent(BaseCard.TransferMode.values()[currentMode].name(), currentMode), (button) -> {
            currentMode = BaseCard.nextTransferMode(card);
            button.setMessage(new TranslatableComponent(BaseCard.TransferMode.values()[currentMode].name(), currentMode));
            if (currentMode == 1) {
                addRenderableWidget(plusButton);
                addRenderableWidget(minusButton);
            } else {
                removeWidget(plusButton);
                removeWidget(minusButton);
            }
        }));

        leftWidgets.add(new ChannelButton(left, top + 35, 20, 20, currentChannel, (button) -> {
            currentChannel = BaseCard.nextChannel(card);
            ((ChannelButton) button).setChannel(currentChannel);
        }));

        if (showExtractAmt()) {
            leftWidgets.add(plusButton);
            leftWidgets.add(minusButton);
        }

        // Lay the buttons out, too lazy to figure out the math every damn time.
        // Ordered by where you add them.
        for (int i = 0; i < leftWidgets.size(); i++) {
            addRenderableWidget(leftWidgets.get(i));
        }
    }

    private boolean showExtractAmt() {
        return card.getItem() instanceof CardItem && BaseCard.getNamedTransferMode(card) == BaseCard.TransferMode.EXTRACT;
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        if (showExtractAmt()) {
            font.draw(stack, new TranslatableComponent("screen.laserio.extractamt").getString(), 132, 5, Color.DARK_GRAY.getRGB());
            String extractAmt = Integer.toString(currentItemExtractAmt);
            font.draw(stack, new TranslatableComponent(extractAmt).getString(), 150 - font.width(extractAmt) / 3, 15, Color.DARK_GRAY.getRGB());
        }
        //super.renderLabels(matrixStack, x, y);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        PacketHandler.sendToServer(new PacketUpdateCard(currentMode, currentChannel, currentItemExtractAmt));
        super.onClose();
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_keyPressed_1_, p_keyPressed_2_);
        if (p_keyPressed_1_ == 256 || minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            onClose();

            return true;
        }

        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }


    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private static TranslatableComponent getTrans(String key, Object... args) {
        return new TranslatableComponent(LaserIO.MODID + "." + key, args);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (hoveredSlot == null || hoveredSlot.getItem().isEmpty() || !(hoveredSlot.getItem().getItem() instanceof BaseFilter))
            return super.mouseClicked(x, y, btn);

        if (btn == 1 && hoveredSlot instanceof CardItemSlot) { //Right click
            int slot = hoveredSlot.getSlotIndex();
            PacketHandler.sendToServer(new PacketOpenFilter(slot));
            return true;
        }
        return super.mouseClicked(x, y, btn);
    }
}