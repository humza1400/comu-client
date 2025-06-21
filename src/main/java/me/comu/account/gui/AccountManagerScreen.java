package me.comu.account.gui;

import me.comu.Comu;
import me.comu.account.Account;
import me.comu.account.auth.MicrosoftLogin;
import me.comu.account.auth.openauth.microsoft.MicrosoftAuthResult;
import me.comu.account.auth.openauth.microsoft.MicrosoftAuthenticationException;
import me.comu.account.auth.openauth.microsoft.MicrosoftAuthenticator;
import me.comu.config.configs.AccountsConfig;
import me.comu.logging.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AccountManagerScreen extends Screen {
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("M/dd/yy h:mm a");

    private TextFieldWidget searchBox;
    private AccountListWidget listWidget;

    private ButtonWidget addButton, loginButton, removeButton, lastButton;
    private ButtonWidget directLoginButton, backButton, serversButton;
    private ButtonWidget upButton, downButton, starButton, lockButton;
    private boolean censorEmails = true;

    private String ipAddress = "Connecting...";
    private long lastIpFetchTime = 0L;

    public AccountManagerScreen() {
        super(Text.literal("Account Manager"));
    }

    @Override
    protected void init() {
        super.init();
        fetchIPAddress();

        int midX = width / 2;
        int headerY = 10;
        int searchW = 200, searchH = 20;
        int footerMargin = 10;
        int btnW = 120, btnH = 24, gap = 6;

        int backY = height - btnH - footerMargin;
        int smallRowY = backY - gap - btnH;

        searchBox = new TextFieldWidget(textRenderer, midX - searchW / 2, headerY, searchW, searchH, Text.translatable("gui.search"));
        searchBox.setChangedListener(s -> filterList());
        addSelectableChild(searchBox);

        int listTop = headerY + searchH + gap;
        int listBottom = smallRowY - gap - 40;
        int listHeight = listBottom - listTop;
        int listWidgetWidth = 400;
        int listX = (width - listWidgetWidth) / 2;
        int itemHeight = 50;

        listWidget = addDrawableChild(new AccountListWidget(client, listWidgetWidth, listBottom, listTop, itemHeight, 0));
        listWidget.setX(listX);

        var accounts = Comu.getInstance().getAccountManager().getAccounts();
        listWidget.setAccounts(getSortedAccounts(accounts));

        int totalFirstWidth = btnW * 4 + gap * 3;
        int startX = midX - totalFirstWidth / 2;

        addButton = addDrawableChild(ButtonWidget.builder(Text.literal("Add"), b -> onAdd()).dimensions(startX, smallRowY, btnW, btnH).build());
        loginButton = addDrawableChild(ButtonWidget.builder(Text.literal("Login"), b -> login()).dimensions(startX + (btnW + gap), smallRowY, btnW, btnH).build());
        removeButton = addDrawableChild(ButtonWidget.builder(Text.literal("Remove"), b -> onRemove()).dimensions(startX + 2 * (btnW + gap), smallRowY, btnW, btnH).build());
        lastButton = addDrawableChild(ButtonWidget.builder(Text.literal("Last"), b -> loginLast()).dimensions(startX + 3 * (btnW + gap), smallRowY, btnW, btnH).build());

        int secondY = backY;
        int serversX = startX + 3 * (btnW + gap);
        directLoginButton = addDrawableChild(ButtonWidget.builder(Text.literal("Direct Login"), b -> onDirectLogin()).dimensions(startX, secondY, btnW, btnH).build());
        backButton = addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> client.setScreen(new TitleScreen())).dimensions(startX + (btnW + gap), secondY, btnW * 2 + gap, btnH).build());
        serversButton = addDrawableChild(ButtonWidget.builder(Text.literal("Servers"), b -> client.setScreen(new MultiplayerScreen(new AccountManagerScreen()))).dimensions(serversX, secondY, btnW, btnH).build());

        int iconBtnSize = 20;
        int iconGap = 4;
        int iconStartX = listWidget.getX() + listWidget.getWidth() + 6;
        int iconY = listWidget.getY();

        upButton = addDrawableChild(ButtonWidget.builder(Text.literal("\u25B2"), unfocusAfter(b -> moveSelected(-1))).dimensions(iconStartX, iconY, iconBtnSize, iconBtnSize).build());

        downButton = addDrawableChild(ButtonWidget.builder(Text.literal("\u25BC"), unfocusAfter(b -> moveSelected(1))).dimensions(iconStartX, iconY + (iconBtnSize + iconGap), iconBtnSize, iconBtnSize).build());

        starButton = addDrawableChild(ButtonWidget.builder(Text.literal("\u2605"), unfocusAfter(b -> starSelected())).dimensions(iconStartX, iconY + 2 * (iconBtnSize + iconGap), iconBtnSize, iconBtnSize).build());

        lockButton = addDrawableChild(ButtonWidget.builder(Text.literal(censorEmails ? "\uD83D\uDD12" : "\uD83D\uDD13"), unfocusAfter(b -> toggleCensor())).dimensions(iconStartX, iconY + 3 * (iconBtnSize + iconGap), iconBtnSize, iconBtnSize).build());


    }

    private List<Account> getSortedAccounts(List<Account> accounts) {
        return accounts.stream().sorted((a, b) -> {
            if (a.isStarred() != b.isStarred()) {
                return a.isStarred() ? -1 : 1;
            }
            return Long.compare(b.getDateAdded(), a.getDateAdded());
        }).collect(Collectors.toList());
    }


    private ButtonWidget.PressAction unfocusAfter(ButtonWidget.PressAction action) {
        return button -> {
            action.onPress(button);
            setFocused(null);
        };
    }


    private void moveSelected(int direction) {
        var sel = listWidget.getSelected();
        if (sel == null) return;
        List<Account> list = listWidget.children().stream().map(e -> e.acct).collect(Collectors.toList());
        int idx = list.indexOf(sel.acct);
        int target = idx + direction;
        if (target < 0 || target >= list.size()) return;
        Collections.swap(list, idx, target);
        listWidget.setAccounts(list);
        listWidget.setSelected(listWidget.children().get(target));
    }

    private void starSelected() {
        var sel = listWidget.getSelected();
        if (sel == null) return;

        Account acct = sel.acct;
        boolean wasStarred = acct.isStarred();
        acct.setStarred(!wasStarred);

        List<Account> list = listWidget.children().stream().map(e -> e.acct).collect(Collectors.toList());

        list.remove(acct);

        if (!acct.isStarred()) {
            list.add(acct);
            listWidget.setAccounts(list);
            listWidget.setSelected(listWidget.children().get(list.size() - 1));
            return;
        }

        int insertIdx = 0;
        while (insertIdx < list.size() && list.get(insertIdx).isStarred()) insertIdx++;
        list.add(insertIdx, acct);

        listWidget.setAccounts(getSortedAccounts(list));
        listWidget.setSelected(listWidget.children().get(insertIdx));
    }


    private void filterList() {
        String q = searchBox.getText().toLowerCase();
        List<Account> filtered = Comu.getInstance().getAccountManager().getAccounts().stream().filter(a -> a.getUsername().toLowerCase().contains(q) || a.getEmail().toLowerCase().contains(q)).collect(Collectors.toList());
        listWidget.setAccounts(getSortedAccounts(filtered));
    }

    private void toggleCensor() {
        censorEmails = !censorEmails;
        lockButton.setMessage(Text.literal(censorEmails ? "\uD83D\uDD12" : "\uD83D\uDD13"));
    }

    private void fetchIPAddress() {
        new Thread(() -> {
            try (java.util.Scanner s = new java.util.Scanner(new java.net.URL("http://checkip.amazonaws.com").openStream(), "UTF-8")) {
                this.ipAddress = s.nextLine();
            } catch (Exception e) {
                this.ipAddress = "Connecting...";
            }
        }).start();
    }

    private void onAdd() {
        client.setScreen(new AccountInputScreen(false, this));
    }

    private void onDirectLogin() {
        client.setScreen(new AccountInputScreen(true, this));
    }

    private void login() {
        var sel = listWidget.getSelected();
        if (sel == null) return;

        loginWithAccount(sel.acct);
    }

    private void loginLast() {
        List<Account> accounts = Comu.getInstance().getAccountManager().getAccounts();
        Account lastUsed = accounts.stream().filter(a -> a.getRefreshToken() != null && !a.getRefreshToken().isBlank()).max(Comparator.comparingLong(Account::getLastUsed)).orElse(null);

        loginWithAccount(lastUsed);
    }


    private void loginWithAccount(Account acct) {
        if (acct == null) {
            return;
        }

        String refreshToken = acct.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            Logger.getLogger().print("Missing refresh token.", Logger.LogType.ERROR);
            return;
        }

        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult result = authenticator.loginWithRefreshToken(refreshToken);
            Session session = new Session(result.getProfile().getName(), MicrosoftLogin.formatUUID(result.getProfile().getId()), result.getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.MSA);

            MicrosoftLogin.setSession(session);

            acct.setUsername(result.getProfile().getName());
            acct.setRefreshToken(result.getRefreshToken());
            acct.setLastUsed(System.currentTimeMillis());

            Comu.getInstance().getConfigManager().getConfig(AccountsConfig.class).save();

            Logger.getLogger().print("Logged in as " + acct.getUsername());

        } catch (MicrosoftAuthenticationException e) {
            Logger.getLogger().print(("Failed to log in: " + e.getMessage()), Logger.LogType.ERROR);
            e.printStackTrace();
        }
    }


    private void onRemove() {
        var sel = listWidget.getSelected();
        if (sel == null) {
            return;
        }

        Account acct = sel.acct;

        Comu.getInstance().getAccountManager().removeAccount(acct);

        listWidget.setAccounts(Comu.getInstance().getAccountManager().getAccounts());
        listWidget.setSelected(null);
    }


    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);

        long now = System.currentTimeMillis();
        if (now - lastIpFetchTime > 4000) {
            lastIpFetchTime = now;
            fetchIPAddress();
        }

        String total = Comu.getInstance().getAccountManager().getAccounts().size() + " Accounts";
        ctx.drawTextWithShadow(textRenderer, total, 10, 10, 0xFFFFFF);

        String me = MinecraftClient.getInstance().getSession().getUsername();

        int padding = 10;
        int spacing = 2;

        int textHeight = textRenderer.fontHeight;
        int totalHeight = textHeight * 2 + spacing;

        int maxWidth = 100;
        int nameWidth = textRenderer.getWidth(me);
        int ipWidth = textRenderer.getWidth(ipAddress);
        int blockWidth = Math.max(nameWidth, ipWidth);

        float scale = 1.0f;
        if (blockWidth > maxWidth) {
            scale = (float) maxWidth / blockWidth;
        }

        int x = width - padding;
        int y = padding;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);

        ctx.drawTextWithShadow(textRenderer, me, (int) (-nameWidth * scale), 0, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, ipAddress, (int) (-ipWidth * scale), textHeight + spacing, 0xAAAAAA);

        ctx.getMatrices().pop();


        searchBox.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private class AccountListWidget extends ElementListWidget<AccountListWidget.Entry> {
        private Entry selected;

        public AccountListWidget(MinecraftClient client, int width, int height, int top, int itemHeight, int headerHeight) {
            super(client, width, height, top, itemHeight, headerHeight);
        }

        public void setAccounts(List<Account> accts) {
            clearEntries();
            for (Account a : accts) addEntry(new Entry(a));
        }

        public void setSelected(Entry entry) {
            this.selected = entry;
            super.setSelected(entry);
        }

        public Entry getSelected() {
            return selected;
        }

        @Override
        public int getRowWidth() {
            return width - 35;
        }

        public class Entry extends ElementListWidget.Entry<Entry> {
            private final Account acct;

            public Entry(Account a) {
                this.acct = a;
            }

            @Override
            public void render(DrawContext ctx, int idx, int y, int x, int rowW, int rowH, int mX, int mY, boolean hovered, float d) {
                boolean selected = listWidget.getSelected() == this;
                if (hovered && !selected) ctx.fill(x, y, x + rowW, y + rowH, 0x40FFFFFF);
                if (selected) {
                    ctx.fill(x, y, x + rowW, y + rowH, 0x40FFFFFF);
                    ctx.drawBorder(x, y, rowW, rowH, 0xFFFFFFFF);
                }

                String name = acct.getUsername();
                int nameX = x + 5;
                ctx.drawText(textRenderer, name, nameX, y + 2, 0xFFFFFF, true);

                if (acct.isStarred()) {
                    int nameWidth = textRenderer.getWidth(name);
                    ctx.drawText(textRenderer, "★", nameX + nameWidth + 2, y + 2, 0xFFD700, true); // Gold star
                }

                String email = acct.getEmail();
                if (censorEmails) {
                    int at = email.indexOf("@");
                    if (at != -1) {
                        email = "*".repeat(at) + email.substring(at);
                    } else {
                        email = "*".repeat(email.length());
                    }
                }
                ctx.drawText(textRenderer, email, x + 5, y + 12, 0xAAAAAA, true);


                int labelX = x + rowW - 150;
                int labelColor = 0x888888;

                int addedLabelW = textRenderer.getWidth("Added:");
                int usedLabelW = textRenderer.getWidth("Last used:");
                int maxLabelW = Math.max(addedLabelW, usedLabelW);

                String addedStr = DATE_FMT.format(new Date(acct.getDateAdded()));
                String usedStr = DATE_FMT.format(new Date(acct.getLastUsed()));

                ctx.drawText(textRenderer, "Added:", labelX, y + 2, labelColor, true);
                ctx.drawText(textRenderer, addedStr, labelX + maxLabelW + 5, y + 2, labelColor, true);
                ctx.drawText(textRenderer, "Last used:", labelX, y + 12, labelColor, true);
                ctx.drawText(textRenderer, usedStr, labelX + maxLabelW + 5, y + 12, labelColor, true);

            }

            @Override
            public boolean mouseClicked(double mx, double my, int btn) {
                if (btn == 0) {
                    listWidget.setSelected(this);
                    return true;
                }
                return false;
            }

            @Override
            public List<? extends Element> children() {
                return Collections.emptyList();
            }

            @Override
            public List<? extends Selectable> selectableChildren() {
                return Collections.emptyList();
            }
        }
    }
}