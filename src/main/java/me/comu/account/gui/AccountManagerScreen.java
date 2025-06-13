package me.comu.account.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class AccountManagerScreen extends Screen {

    private final List<ClickableWidget> altButtons = new ArrayList<>();

    public AccountManagerScreen() {
        super(Text.literal("Account Manager"));
    }

    @Override
    protected void init() {
        altButtons.clear();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            MinecraftClient.getInstance().setScreen(null); // Go back to main menu
        }).dimensions(centerX - 100, centerY + 60, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Add Alt"), button -> {
        }).dimensions(centerX - 100, centerY - 20, 200, 20).build());

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
