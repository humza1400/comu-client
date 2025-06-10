package me.comu.module.impl.render.clickgui.comu;

import me.comu.Comu;
import me.comu.module.Category;
import me.comu.module.impl.render.ClickGui;
import me.comu.module.impl.render.clickgui.comu.properties.StringInput;
import me.comu.property.properties.BooleanProperty;
import me.comu.render.Renderer2D;
import me.comu.utils.ClientUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ComuGui extends Screen {
    private static final ComuGui instance = new ComuGui();

    private int x = 500, y = 220;
    private final int width = 400, height = 300;
    private boolean isDragging = false;
    private int dragOffsetX, dragOffsetY;

    private final int backgroundColor = 0xCC1A1A1A;
    private final int outlineColor = 0xFFB01670;
    private final int barHeight = 1;
    private final int headerHeight = 18;

    private final ComuSidebar sidebar = new ComuSidebar();
    private final ComuModuleList moduleList = new ComuModuleList();

    private final int sidebarWidth = 60;
    private final int categoryHeight = 37;
    private final int spacing = 5;
    private Category lastCategory = null;

    private String searchText = "";
    private boolean searching = false;
    private boolean wasSearching = false;
    private boolean suppressNextCharTyped = false;


    private long lastCursorBlink = System.currentTimeMillis();
    private boolean cursorVisible = true;

    private int searchCursorIndex = 0;
    private boolean searchSelectedAll = false;
    private boolean isBackspaceHeld = false;
    private long backspaceHoldStart = 0;
    private long lastBackspaceRepeat = 0;
    private int searchScrollOffset = 0;
    private boolean shadersEnabled = false;

    private int frameIndex = 0;
    private long lastFrameTime = 0;
    private float gengarOffsetX = 2;
    private boolean gengarMovingRight = true;

    private static final Identifier[] GENGAR_FRAMES = new Identifier[] {
            ClientUtils.identifier("textures/gengar/0.png"),
            ClientUtils.identifier( "textures/gengar/1.png"),
            ClientUtils.identifier( "textures/gengar/2.png"),
            ClientUtils.identifier( "textures/gengar/3.png"),
            ClientUtils.identifier( "textures/gengar/4.png"),
            ClientUtils.identifier( "textures/gengar/5.png"),
            ClientUtils.identifier( "textures/gengar/6.png"),
            ClientUtils.identifier( "textures/gengar/7.png"),
            ClientUtils.identifier( "textures/gengar/8.png"),
            ClientUtils.identifier( "textures/gengar/9.png"),
            ClientUtils.identifier( "textures/gengar/10.png"),
            ClientUtils.identifier( "textures/gengar/11.png"),
            ClientUtils.identifier( "textures/gengar/12.png"),
            ClientUtils.identifier( "textures/gengar/13.png"),
            ClientUtils.identifier( "textures/gengar/14.png"),
            ClientUtils.identifier( "textures/gengar/15.png"),
            ClientUtils.identifier( "textures/gengar/16.png"),
            ClientUtils.identifier( "textures/gengar/17.png"),
            ClientUtils.identifier( "textures/gengar/18.png"),
            ClientUtils.identifier( "textures/gengar/19.png"),
    };

    public ComuGui() {
        super(Text.literal("comu"));
    }

    public static ComuGui getInstance() {
        return instance;
    }

    @Override
    protected void init() {
        super.init();
        sidebar.init(x + 3, y + barHeight + 5, sidebarWidth, categoryHeight, spacing);
        sidebar.setPosition(x + 3, y + barHeight + 5);
        moduleList.setBounds(x + sidebarWidth + 12, y + barHeight + 5, 320);
        moduleList.setCategory(sidebar.getSelectedCategory());
    }

    @Override
    public void tick() {
        if (isBackspaceHeld && searching && ClientUtils.isKeyPressed(GLFW.GLFW_KEY_BACKSPACE)) {
            long now = System.currentTimeMillis();
            if (now - backspaceHoldStart > 300 && now - lastBackspaceRepeat > 50) {
                if (searchSelectedAll) {
                    searchText = "";
                    searchCursorIndex = 0;
                    searchSelectedAll = false;
                } else if (searchCursorIndex > 0 && !searchText.isEmpty()) {
                    searchText = searchText.substring(0, searchCursorIndex - 1) + searchText.substring(searchCursorIndex);
                    searchCursorIndex--;
                }
                lastBackspaceRepeat = now;
            }
        } else {
            isBackspaceHeld = false;
        }

        moduleList.getModules().forEach(m -> m.getAllStringInputs().forEach(StringInput::tick));
        super.tick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);


        if (isDragging) {
            x = mouseX - dragOffsetX;
            y = mouseY - dragOffsetY;
        }

        if (System.currentTimeMillis() - lastCursorBlink > 500) {
            cursorVisible = !cursorVisible;
            lastCursorBlink = System.currentTimeMillis();
        }

        context.fill(x, y, x + width, y + height, backgroundColor);
        context.fill(x, y - barHeight - headerHeight, x + width, y - headerHeight, 0xFF90105A);
        context.fill(x, y - headerHeight, x + width, y, 0xFF111111);

        ClickGui clickGui = Comu.getInstance().getModuleManager().getModule(ClickGui.class);
        BooleanProperty gengar = (BooleanProperty) clickGui.getPropertyByName("gengar");
        if (gengar.getValue()) {
            int imageSize = 50;
            int texSize = 24;
            int imageX = x + 5 - (imageSize - texSize) / 2;
            int imageY = y - headerHeight + 1 - (imageSize - texSize) / 2;
            if (gengarMovingRight) {
                gengarOffsetX += 0.3f;
                if (gengarOffsetX >= 150) gengarMovingRight = false;
            } else {
                gengarOffsetX -= 0.3f;
                if (gengarOffsetX <= 4) gengarMovingRight = true;
            }
            drawGengar(context, imageX + (int) gengarOffsetX, imageY + 12);
        }

        String title = Comu.getClientName();
        int titleWidth = textRenderer.getWidth(title);
        context.drawText(textRenderer, title, (int) (x + (width - titleWidth) / 2f), y - headerHeight + 5, 0xFFFA74B4, true);

        drawSearchBox(context, mouseX, mouseY);

        sidebar.setPosition(x + 3, y + 5);
        moduleList.setBounds(x + sidebarWidth + 12, y + 5, 320);

        Category currentCategory = sidebar.getSelectedCategory();
        if (searchText.trim().isEmpty()) {
            if (wasSearching) {
                sidebar.setInSearchMode(false);
                moduleList.setCategory(currentCategory);
                moduleList.rebuild();
                wasSearching = false;
                lastCategory = currentCategory;
            } else if (currentCategory != lastCategory) {
                moduleList.setCategory(currentCategory);
                moduleList.rebuild();
                lastCategory = currentCategory;
            }
        } else {
            moduleList.setFilteredModulesBySearch(searchText.trim().toLowerCase());
            sidebar.setInSearchMode(true);
            wasSearching = true;
        }

        sidebar.draw(context, mouseX, mouseY);
//        context.fill(x + 5 + sidebarWidth + 1, y + 8, x + 5 + sidebarWidth + 2, y + height - 8, 0x4DCCCCCC);
        moduleList.draw(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int dragHeight = barHeight + headerHeight;
            if (mouseX >= x && mouseX <= x + width && mouseY >= y - dragHeight && mouseY <= y) {
                isDragging = true;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
                return true;
            }
        }

        searchSelectedAll = false;

        int searchBoxWidth = 90;
        int searchBoxHeight = 10;
        int searchX = x + width - searchBoxWidth - 10;
        int searchY = y - headerHeight + 4;

        searching = mouseX >= searchX && mouseX <= searchX + searchBoxWidth && mouseY >= searchY && mouseY <= searchY + searchBoxHeight;

        boolean sidebarClicked = sidebar.mouseClicked(mouseX, mouseY, button);
        boolean moduleClicked = moduleList.mouseClicked(mouseX, mouseY, button);

        return sidebarClicked || moduleClicked || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount == 0) return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        int x = sidebar.getX();
        int y = sidebar.getY();
        int w = sidebar.getWidth();
        int h = sidebar.getHeight();

        if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            sidebar.handleScroll(-Integer.signum((int) verticalAmount));
        } else {
            moduleList.handleScroll(-Integer.signum((int) verticalAmount));
        }

        return true;
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean inputFocused = moduleList.getModules().stream().flatMap(m -> m.getAllStringInputs().stream()).anyMatch(StringInput::isFocused);
        if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            if (keyCode == GLFW.GLFW_KEY_C) {
                if (inputFocused) {
                    moduleList.getModules().stream().flatMap(m -> m.getAllStringInputs().stream()).filter(StringInput::isFocused).findFirst().ifPresent(input -> MinecraftClient.getInstance().keyboard.setClipboard(input.getValue()));
                } else if (searching) {
                    MinecraftClient.getInstance().keyboard.setClipboard(searchText);
                }
                return true;
            }

            if (keyCode == GLFW.GLFW_KEY_V) {
                String clipboard = MinecraftClient.getInstance().keyboard.getClipboard();
                if (inputFocused) {
                    moduleList.getModules().stream().flatMap(m -> m.getAllStringInputs().stream()).filter(StringInput::isFocused).findFirst().ifPresent(input -> input.paste(clipboard));
                } else if (searching) {
                    pasteToSearch(clipboard);
                }
                return true;
            }
        }

        if (inputFocused && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER)) {
            moduleList.getModules().forEach(module -> module.getAllStringInputs().forEach(input -> {
                if (input.isFocused()) {
                    input.keyTyped('\0', keyCode);
                }
            }));
            return true;
        }


        if (keyCode == GLFW.GLFW_KEY_SLASH && !searching && !inputFocused) {
            searching = true;
            searchCursorIndex = searchText.length();
            searchSelectedAll = false;
            suppressNextCharTyped = true;
            return false;
        }

        if (searching) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_ENTER:
                    searching = false;
                    return true;
                case GLFW.GLFW_KEY_ESCAPE:
                    searching = false;
                    searchText = "";
                    searchCursorIndex = 0;
                    moduleList.setCategory(sidebar.getSelectedCategory());
                    return true;
                case GLFW.GLFW_KEY_BACKSPACE:
                    if (searchSelectedAll) {
                        searchText = "";
                        searchCursorIndex = 0;
                        searchSelectedAll = false;
                    } else if (searchCursorIndex > 0) {
                        searchText = searchText.substring(0, searchCursorIndex - 1) + searchText.substring(searchCursorIndex);
                        searchCursorIndex--;
                    }
                    return true;
                case GLFW.GLFW_KEY_LEFT:
                    if (searchCursorIndex > 0) {
                        searchCursorIndex--;
                        searchSelectedAll = false;
                    }
                    return true;
                case GLFW.GLFW_KEY_RIGHT:
                    if (searchCursorIndex < searchText.length()) {
                        searchCursorIndex++;
                        searchSelectedAll = false;
                    }
                    return true;
                case GLFW.GLFW_KEY_A:
                    if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                        searchCursorIndex = searchText.length();
                        searchSelectedAll = true;
                    }
                    return true;
                default:
                    return true;
            }
        }

        for (ComuModule module : moduleList.getModules()) {
            module.handleKeyTyped(keyCode);
        }

        boolean consumedByBind = false;
        for (ComuModule module : moduleList.getModules()) {
            if (module.isListeningForKey()) {
                module.handleKeyTyped(keyCode);
                consumedByBind = true;
                break;
            }
        }

        if (!consumedByBind && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            MinecraftClient.getInstance().setScreen(null);
            return true;
        }

        return true;
    }


    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (suppressNextCharTyped) {
            suppressNextCharTyped = false;
            return true;
        }

        boolean inputFocused = moduleList.getModules().stream().flatMap(m -> m.getAllStringInputs().stream()).anyMatch(StringInput::isFocused);
        if (inputFocused) {
            moduleList.getModules().forEach(module -> {
                for (StringInput input : module.getAllStringInputs()) {
                    if (input.isFocused()) {
                        input.keyTyped(chr, GLFW.GLFW_KEY_UNKNOWN);
                    }
                }
            });
            return true;
        }

        if (chr == '/' && !searching) {
            searching = true;
            searchCursorIndex = searchText.length();
            searchSelectedAll = false;
            return true;
        }

        if (searching && chr >= 32 && chr <= 126) {
            if (searchSelectedAll) {
                searchText = "" + chr;
                searchCursorIndex = 1;
                searchSelectedAll = false;
            } else {
                searchText = searchText.substring(0, searchCursorIndex) + chr + searchText.substring(searchCursorIndex);
                searchCursorIndex++;
            }
            return true;
        }

        return super.charTyped(chr, modifiers);
    }


    @Override
    protected void applyBlur() {
        ClickGui clickGui = Comu.getInstance().getModuleManager().getModule(ClickGui.class);
        BooleanProperty blur = (BooleanProperty) clickGui.getPropertyByName("blur");
        if (blur.getValue()) super.applyBlur();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void drawSearchBox(DrawContext context, int mouseX, int mouseY) {
        int searchBoxWidth = 90;
        int searchBoxHeight = 10;
        int searchX = x + width - searchBoxWidth - 10;
        int searchY = y - headerHeight + 4;
        context.fill(searchX, searchY, searchX + searchBoxWidth, searchY + searchBoxHeight, 0xFF292929);

        int boxInnerWidth = searchBoxWidth - 8;
        int cursorPixelX = Renderer2D.getStringWidth(searchText.substring(0, searchCursorIndex));

        if (cursorPixelX - searchScrollOffset > boxInnerWidth) searchScrollOffset = cursorPixelX - boxInnerWidth;
        else if (cursorPixelX - searchScrollOffset < 0) searchScrollOffset = cursorPixelX;

        int skipChars = 0, widthSoFar = 0;
        while (skipChars < searchCursorIndex) {
            char c = searchText.charAt(skipChars);
            int charWidth = Renderer2D.getCharWidth(c);
            if (widthSoFar + charWidth > searchScrollOffset) break;
            widthSoFar += charWidth;
            skipChars++;
        }

        String remaining = searchText.substring(skipChars);
        String visibleText = ClientUtils.trimTextToFit(remaining, boxInnerWidth);

        if (searching && searchSelectedAll && !searchText.isEmpty()) {
            int textWidth = Renderer2D.getStringWidth(visibleText);
            context.fill(searchX + 4, searchY + 1, searchX + 4 + textWidth, searchY + searchBoxHeight - 1, 0x80FFAACC);
        }

        if (!searching && searchText.isEmpty()) {
            Renderer2D.drawText(context, "Search...", searchX + 4, searchY + 1, 0xFF777777, true);
        } else {
            Renderer2D.drawText(context, visibleText, searchX + 4, searchY + 1, 0xFFFFFFFF, true);
        }

        if (searching && cursorVisible) {
            int visibleCursorIndex = Math.max(0, Math.min(visibleText.length(), searchCursorIndex - skipChars));
            int cursorOffsetX = Renderer2D.getStringWidth(visibleText.substring(0, visibleCursorIndex));
            int cursorX = searchX + 4 + cursorOffsetX;
            context.fill(cursorX, searchY + 1, cursorX + 1, searchY + searchBoxHeight - 2, 0xFFFFFFFF);
        }

        context.fill(x, y + height, x + width, y + height + 2, 0xFF90105A);
    }

    private void pasteToSearch(String clipboard) {
        if (clipboard == null) return;

        if (searchSelectedAll) {
            searchText = clipboard;
            searchCursorIndex = clipboard.length();
        } else {
            searchText = searchText.substring(0, searchCursorIndex) + clipboard + searchText.substring(searchCursorIndex);
            searchCursorIndex += clipboard.length();
        }
        searchSelectedAll = false;
    }

    private void drawGengar(DrawContext context, int x, int y) {
        long now = System.currentTimeMillis();
        if (now - lastFrameTime > 100) {
            frameIndex = (frameIndex + 1) % GENGAR_FRAMES.length;
            lastFrameTime = now;
        }

        Identifier frame = GENGAR_FRAMES[frameIndex];

        int texWidth = 500;
        int texHeight = 493;

        float scaleX = 50f / texWidth;
        float scaleY = 18f / texHeight;

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scaleX, scaleY, 1.0f);

        context.drawTexture(
                RenderLayer::getGuiTextured,
                frame,
                0, 0,
                0, 0,
                texWidth, texHeight,
                texWidth, texHeight
        );

        context.getMatrices().pop();
    }
}
