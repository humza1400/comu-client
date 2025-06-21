package me.comu.account.gui;

import me.comu.Comu;
import me.comu.account.Account;
import me.comu.account.auth.MicrosoftLogin;
import me.comu.account.auth.openauth.microsoft.MicrosoftAuthResult;
import me.comu.account.auth.openauth.microsoft.MicrosoftAuthenticationException;
import me.comu.account.auth.openauth.microsoft.MicrosoftAuthenticator;
import me.comu.logging.Logger;
import me.comu.notification.Notification;
import me.comu.notification.NotificationType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AccountInputScreen extends Screen {
    private final boolean isDirectLogin;
    private final Screen parent;

    private TextFieldWidget emailBox;
    private TextFieldWidget passwordBox;
    private TextFieldWidget comboBox;

    private ButtonWidget loginOrAddButton;

    public AccountInputScreen(boolean isDirectLogin, Screen parent) {
        super(Text.literal(isDirectLogin ? "Direct Login" : "Add Account"));
        this.isDirectLogin = isDirectLogin;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int midX = width / 2;
        int boxWidth = 200;
        int boxHeight = 20;
        int verticalSpacing = 35;
        int currentY = height / 4;

        addDrawableChild(ButtonWidget.builder(Text.literal("Login with Microsoft"), b -> {
            MicrosoftLogin.startLoginFlow();
            MinecraftClient.getInstance().setScreen(new MicrosoftRedirectInputScreen(new AccountManagerScreen()));
        }).dimensions(midX - boxWidth / 2, currentY, boxWidth, 20).build());

        currentY += verticalSpacing + 8;

        emailBox = new TextFieldWidget(textRenderer, midX - boxWidth / 2, currentY, boxWidth, boxHeight, Text.literal("Email"));
        emailBox.setMaxLength(256);
        addSelectableChild(emailBox);
        currentY += verticalSpacing;

        passwordBox = new TextFieldWidget(textRenderer, midX - boxWidth / 2, currentY, boxWidth, boxHeight, Text.literal("Password"));
        passwordBox.setMaxLength(256);
        addSelectableChild(passwordBox);
        currentY += verticalSpacing;

        comboBox = new TextFieldWidget(textRenderer, midX - boxWidth / 2, currentY, boxWidth, boxHeight, Text.literal("Email:Password"));
        comboBox.setMaxLength(256);
        addSelectableChild(comboBox);
        currentY += verticalSpacing + 6;

        loginOrAddButton = addDrawableChild(ButtonWidget.builder(Text.literal(isDirectLogin ? "Login" : "Add"), b -> onSubmit()).dimensions(midX - boxWidth / 2, currentY, boxWidth, 20).build());
        currentY += verticalSpacing;

        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> client.setScreen(parent)).dimensions(midX - boxWidth / 2, currentY, boxWidth, 20).build());

    }

    private void onSubmit() {
        String email = emailBox.getText().trim();
        String password = passwordBox.getText().trim();
        String combo = comboBox.getText().trim();

        if (!combo.isEmpty() && combo.contains(":")) {
            String[] split = combo.split(":", 2);
            email = split[0].trim();
            password = split[1].trim();
        }

        if (email.isEmpty() || password.isEmpty()) {
            Logger.getLogger().print("Missing email or password.", Logger.LogType.ERROR);
            return;
        }

        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult result = authenticator.loginWithCredentials(email, password);
            Account account = new Account(email, result.getProfile().getName(), MicrosoftLogin.formatUUID(result.getProfile().getId()).toString(), result.getRefreshToken(), System.currentTimeMillis(), System.currentTimeMillis(), false);

            if (isDirectLogin) {
                MicrosoftLogin.setUserMicrosoft(email, password);
                Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.POSITIVE, "Success", "Logged in as " + result.getProfile().getName()));
                Logger.getLogger().print("Logged in as " + result.getProfile().getName());
            } else {
                Comu.getInstance().getAccountManager().addAccount(account);
                Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.POSITIVE, "Success", "Added account " + result.getProfile().getName()));
                Logger.getLogger().print("Added account: " + result.getProfile().getName());
            }

            client.setScreen(parent);

        } catch (MicrosoftAuthenticationException e) {
            Logger.getLogger().print("Authentication failed: " + e.getMessage(), Logger.LogType.ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);

        ctx.drawCenteredTextWithShadow(textRenderer, isDirectLogin ? "Direct Login" : "Add Account", width / 2, 20, 0xFFFFFF);

        int labelOffset = 11;

        ctx.drawTextWithShadow(textRenderer, Text.literal("Email"), emailBox.getX(), emailBox.getY() - labelOffset, 0xAAAAAA);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Password"), passwordBox.getX(), passwordBox.getY() - labelOffset, 0xAAAAAA);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Email:Password"), comboBox.getX(), comboBox.getY() - labelOffset, 0xAAAAAA);

        emailBox.render(ctx, mouseX, mouseY, delta);
        passwordBox.render(ctx, mouseX, mouseY, delta);
        comboBox.render(ctx, mouseX, mouseY, delta);
    }


    @Override
    public boolean shouldPause() {
        return false;
    }
}
