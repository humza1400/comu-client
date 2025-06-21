package me.comu.account.gui;

import me.comu.account.Account;
import me.comu.account.auth.MicrosoftLogin;
import me.comu.Comu;
import me.comu.notification.Notification;
import me.comu.notification.NotificationType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.UUID;

public class MicrosoftRedirectInputScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget urlInput;

    public MicrosoftRedirectInputScreen(Screen parent) {
        super(Text.literal("Paste Microsoft Redirect URL"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int midX = width / 2;
        int boxWidth = 300;
        int boxHeight = 20;

        urlInput = new TextFieldWidget(textRenderer, midX - boxWidth / 2, height / 2 - 10, boxWidth, boxHeight, Text.literal("Paste redirect URL"));
        urlInput.setMaxLength(9999);
        addSelectableChild(urlInput);

        addDrawableChild(ButtonWidget.builder(Text.literal("Submit"), b -> handleRedirectUrl()).dimensions(midX - 75, height / 2 + 25, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> client.setScreen(parent)).dimensions(midX - 75, height / 2 + 55, 150, 20).build());
    }

    private void handleRedirectUrl() {
        try {
            String redirectedUrl = urlInput.getText().trim();
            String accessToken = MicrosoftLogin.extractFragmentValue(redirectedUrl, "access_token");
            String refreshToken = MicrosoftLogin.extractFragmentValue(redirectedUrl, "refresh_token");

            var session = MicrosoftLogin.completeMinecraftLogin(accessToken);
            MicrosoftLogin.setSession(session);

            UUID uuid = session.getUuidOrNull();
            if (uuid == null) throw new IllegalStateException("Session UUID is null");

            Account account = new Account(
                    session.getUsername(),
                    session.getUsername(),
                    uuid.toString(),
                    refreshToken,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    false
            );


            Comu.getInstance().getAccountManager().addAccount(account);
            Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.POSITIVE, "Success", "Logged in as " + session.getUsername()));
            client.setScreen(parent);

        } catch (Exception e) {
            e.printStackTrace();
            Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.NEGATIVE, "Login Failed", e.getMessage()));
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "Paste the full redirected URL", width / 2, height / 2 - 30, 0xFFFFFF);
        urlInput.render(ctx, mouseX, mouseY, delta);

    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
