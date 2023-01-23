package com.glodblock.github.client.gui.base;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.AEBaseContainer;
import appeng.core.AEConfig;
import appeng.core.localization.GuiText;
import appeng.util.calculators.ArithHelper;
import appeng.util.calculators.Calculator;
import com.glodblock.github.FluidCraft;
import com.glodblock.github.inventory.gui.GuiType;
import com.glodblock.github.network.CPacketSwitchGuis;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public abstract class FCGuiAmount extends AEBaseGui {

    protected GuiTextField amountBox;
    protected GuiTabButton originalGuiBtn;
    protected GuiButton submit;
    protected GuiButton plus1;
    protected GuiButton plus10;
    protected GuiButton plus100;
    protected GuiButton plus1000;
    protected GuiButton minus1;
    protected GuiButton minus10;
    protected GuiButton minus100;
    protected GuiButton minus1000;
    protected GuiType originalGui;
    protected ItemStack myIcon;

    public FCGuiAmount(Container container) {
        super(container);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        final int a = AEConfig.instance.priorityByStacksAmounts(0);
        final int b = AEConfig.instance.priorityByStacksAmounts(1);
        final int c = AEConfig.instance.priorityByStacksAmounts(2);
        final int d = AEConfig.instance.priorityByStacksAmounts(3);

        this.buttonList.add(this.plus1 = new GuiButton(0, this.guiLeft + 20, this.guiTop + 26, 22, 20, "+" + a));
        this.buttonList.add(this.plus10 = new GuiButton(0, this.guiLeft + 48, this.guiTop + 26, 28, 20, "+" + b));
        this.buttonList.add(this.plus100 = new GuiButton(0, this.guiLeft + 82, this.guiTop + 26, 32, 20, "+" + c));
        this.buttonList.add(this.plus1000 = new GuiButton(0, this.guiLeft + 120, this.guiTop + 26, 38, 20, "+" + d));

        this.buttonList.add(this.minus1 = new GuiButton(0, this.guiLeft + 20, this.guiTop + 75, 22, 20, "-" + a));
        this.buttonList.add(this.minus10 = new GuiButton(0, this.guiLeft + 48, this.guiTop + 75, 28, 20, "-" + b));
        this.buttonList.add(this.minus100 = new GuiButton(0, this.guiLeft + 82, this.guiTop + 75, 32, 20, "-" + c));
        this.buttonList.add(this.minus1000 = new GuiButton(0, this.guiLeft + 120, this.guiTop + 75, 38, 20, "-" + d));

        this.buttonList.add(
                this.submit = new GuiButton(0, this.guiLeft + 128, this.guiTop + 51, 38, 20, GuiText.Next.getLocal()));

        setOriginGUI(((AEBaseContainer) this.inventorySlots).getTarget());
        if (this.originalGui != null && this.myIcon != null) {
            this.buttonList.add(
                    this.originalGuiBtn = new GuiTabButton(
                            this.guiLeft + 154, this.guiTop, this.myIcon, this.myIcon.getDisplayName(), itemRender));
        }

        this.amountBox = new GuiTextField(
                this.fontRendererObj, this.guiLeft + 62, this.guiTop + 57, 59, this.fontRendererObj.FONT_HEIGHT);
        this.amountBox.setEnableBackgroundDrawing(false);
        this.amountBox.setMaxStringLength(16);
        this.amountBox.setTextColor(0xFFFFFF);
        this.amountBox.setVisible(true);
        this.amountBox.setFocused(true);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.bindTexture(getBackground());
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void keyTyped(final char character, final int key) {
        if (!this.checkHotbarKeys(key)) {
            if (key == 28) {
                this.actionPerformed(this.submit);
            }
            this.amountBox.textboxKeyTyped(character, key);
            super.keyTyped(character, key);
        }
    }

    @Override
    protected void actionPerformed(final GuiButton btn) {
        super.actionPerformed(btn);
        if (btn == this.originalGuiBtn) {
            FluidCraft.proxy.netHandler.sendToServer(new CPacketSwitchGuis(this.originalGui));
        }
        final boolean isPlus = btn == this.plus1 || btn == this.plus10 || btn == this.plus100 || btn == this.plus1000;
        final boolean isMinus =
                btn == this.minus1 || btn == this.minus10 || btn == this.minus100 || btn == this.minus1000;

        if (isPlus || isMinus) {
            this.addQty(this.getQty(btn));
        }
    }

    protected void addQty(final int i) {
        try {
            long resultI = getAmount();
            if (resultI == 1 && i > 1) {
                resultI = 0;
            }
            resultI += i;
            if (resultI < 1) {
                resultI = 1;
            }
            String out = Long.toString(resultI);
            this.amountBox.setText(out);
        } catch (final NumberFormatException ignore) {
        }
    }

    protected abstract void setOriginGUI(Object target);

    protected abstract String getBackground();

    protected int getAmount() {
        try {
            String out = this.amountBox.getText();
            double result = Calculator.conversion(out);
            if (result <= 0 || Double.isNaN(result)) {
                return 0;
            } else {
                return (int) ArithHelper.round(result, 0);
            }
        } catch (final NumberFormatException e) {
            return 0;
        }
    }
}