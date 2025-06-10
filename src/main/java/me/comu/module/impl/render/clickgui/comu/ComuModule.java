package me.comu.module.impl.render.clickgui.comu;

import me.comu.Comu;
import me.comu.keybind.Keybind;
import me.comu.module.Module;
import me.comu.module.ToggleableModule;
import me.comu.module.impl.render.clickgui.comu.properties.*;
import me.comu.property.Property;
import me.comu.property.properties.*;
import me.comu.render.Renderer2D;
import me.comu.utils.ClientUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ComuModule {
    private ComuPanel panel;
    private me.comu.module.Module module;
    private int x, y, width, height;

    private boolean listeningForKey = false;
    private int bindTextX, bindTextY, bindTextWidth, bindTextHeight;
    private int previousKey = -1;
    private boolean hoverDisabled = false;

    private List<BooleanButton> booleanButtons = new ArrayList<>();
    private List<ValueSlider> valueSliders = new ArrayList<>();
    private List<EnumDropdown> enumDropdowns = new ArrayList<>();
    private final List<StringInput> stringInputs = new ArrayList<>();
    private final List<PropertyContainer> propertyContainers = new ArrayList<>();


    public ComuModule(ComuPanel panel, Module module) {
        this.panel = panel;
        this.module = module;
        this.width = 300;
        this.height = 40;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void drawAt(DrawContext context, int drawX, int drawY, int scrollOffset, int visibleTop, int visibleBottom, int guiMouseX, int guiMouseY, List<EnumDropdown> expandedDropdowns) {
        int boxColor = 0x66181818;
        int borderColor = 0xFFB0B0B0;

        String name = module.getName();
        int nameWidth = Renderer2D.getStringWidth(name);
        int nameX = drawX + 8;
        int nameY = drawY - 4;
        int textPadding = 4;

        context.fill(drawX, drawY, drawX + width, drawY + height, boxColor);
        context.fill(drawX, drawY, nameX - textPadding, drawY + 1, borderColor);
        context.fill(nameX + nameWidth + textPadding, drawY, drawX + width, drawY + 1, borderColor);
        context.fill(drawX, drawY, drawX + 1, drawY + height, borderColor);
        context.fill(drawX + width - 1, drawY, drawX + width, drawY + height, borderColor);
        context.fill(drawX, drawY + height - 1, drawX + width, drawY + height, borderColor);

        Renderer2D.drawText(context, name, nameX, nameY, 0xFFFFFFFF, true);

        int nameH = Renderer2D.getFontHeight();
        boolean hoveringName = guiMouseX >= nameX && guiMouseX <= nameX + nameWidth && guiMouseY >= nameY && guiMouseY <= nameY + nameH;

        if (hoveringName) {
            String description = module.getDescription();
            if (description != null && !description.isEmpty()) {
                float tooltipScale = 0.6f;

                int textW = Renderer2D.getStringWidth(description);
                int textH = Renderer2D.getFontHeight();

                int padX = 4;
                int padY = 2;

                int tooltipX = guiMouseX + 6;
                int tooltipY = guiMouseY + 4;
                int tooltipW = (int) (textW * tooltipScale) + padX * 2;
                int tooltipH = (int) (textH * tooltipScale) + padY * 2;
                if (!isTooltipObstructed(tooltipX, tooltipY, tooltipW, tooltipH, expandedDropdowns)) {
                    MatrixStack matrices = context.getMatrices();
                    matrices.push();
                    matrices.translate(0, 0, 1000);
                    context.fill(tooltipX, tooltipY, tooltipX + tooltipW, tooltipY + tooltipH, 0xFF000000);
                    matrices.translate(tooltipX + padX, tooltipY + padY, 0);
                    matrices.scale(tooltipScale, tooltipScale, 1.0f);
                    Renderer2D.drawText(context, description, 0, 0, 0xFFFFFF, true);
                    matrices.pop();
                }
            }
        }


        for (BooleanButton button : booleanButtons) {
            button.draw(context, drawX, drawY, guiMouseX, guiMouseY, visibleTop, visibleBottom, hoverDisabled);
        }

        for (ValueSlider slider : valueSliders) {
            slider.draw(context, drawX, drawY, guiMouseX, guiMouseY, hoverDisabled);
        }

        for (EnumDropdown dropdown : enumDropdowns) {
            dropdown.draw(context, drawX, drawY, guiMouseX, guiMouseY, hoverDisabled);
        }

        for (StringInput input : stringInputs) {
            input.draw(context, drawX, drawY, guiMouseX, guiMouseY, visibleTop, visibleBottom);
        }

        for (PropertyContainer container : propertyContainers) {
            container.draw(context, drawX, drawY, guiMouseX, guiMouseY);
        }

        if (module instanceof ToggleableModule) {
            int enabledX = drawX + 5;
            int enabledY = drawY + 7;
            float scale = 0.85f;

            Keybind key = Comu.getInstance().getKeybindManager().getKeybindByLabel(module.getName());

            String fullKey = key == null ? "-" : listeningForKey ? "..." : (ClientUtils.getKeyName(key.getKey()).equalsIgnoreCase("UNKNOWN") ? "-" : ClientUtils.getKeyName(key.getKey()));

            String shortKey;
            boolean shouldShowTooltip = false;

            if (listeningForKey) {
                shortKey = "...";
            } else if (fullKey.length() == 1) {
                shortKey = "[" + fullKey + "]";
            } else {
                shortKey = "[" + fullKey.charAt(0) + "..]";
                shouldShowTooltip = true;
            }

            int rawWidth = Renderer2D.getStringWidth(shortKey);
            int rawHeight = Renderer2D.getFontHeight();

            int txtW = (int) (rawWidth * scale);
            int txtH = (int) (rawHeight * scale);

            int textX = enabledX + 75;
            int textY = enabledY;

            bindTextX = textX;
            bindTextY = textY;
            bindTextWidth = txtW;
            bindTextHeight = txtH;

            boolean hovering = guiMouseX >= textX && guiMouseX < textX + txtW && guiMouseY >= textY && guiMouseY < textY + txtH;


            if (hovering) {
                int tooltipX = guiMouseX + 6;
                int tooltipY = guiMouseY + 4;
                int tooltipW = (int) (Renderer2D.getStringWidth("[" + fullKey + "]") * 0.6f) + 8;
                int tooltipH = (int) (Renderer2D.getFontHeight() * 0.6f) + 4;

                int pad = 2;
                context.fill(textX - pad, textY - pad, textX + txtW + pad, textY + txtH + pad, 0x30FFFFFF);

                if (shouldShowTooltip && !isTooltipObstructed(tooltipX, tooltipY, tooltipW, tooltipH, expandedDropdowns)) {
                    float tooltipScale = 0.6f;
                    String tooltipText = "[" + fullKey + "]";

                    MatrixStack matrices = context.getMatrices();
                    matrices.push();
                    matrices.translate(0, 0, 1000);
                    context.fill(tooltipX, tooltipY, tooltipX + tooltipW, tooltipY + tooltipH, 0xFF000000);
                    matrices.translate(tooltipX + 4, tooltipY + 2, 0);
                    matrices.scale(tooltipScale, tooltipScale, 1.0f);
                    Renderer2D.drawText(context, tooltipText, 0, 0, 0xFFFFFFFF, true);
                    matrices.pop();
                }
            }

            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.translate(textX, textY, 0);
            matrices.scale(scale, scale, 1.0f);
            Renderer2D.drawText(context, shortKey, 0, 0, 0x808080, true);
            matrices.pop();
        }
    }

    public boolean isTooltipObstructed(int tooltipX, int tooltipY, int tooltipW, int tooltipH, List<EnumDropdown> obstructingDropdowns) {
        for (EnumDropdown dropdown : obstructingDropdowns) {
            if (dropdown.isObstructing(tooltipX, tooltipY, tooltipW, tooltipH)) {
                return true;
            }
        }
        return false;
    }

    public void rebuildButtons() {
        booleanButtons.clear();
        valueSliders.clear();
        enumDropdowns.clear();
        stringInputs.clear();
        propertyContainers.clear();

        int paddingX = 5;
        int spacingX = 100;
        int spacingY = 12;
        int buttonWidth = 80;
        int buttonHeight = 8;
        int bottomPad = 3;

        int currentX = paddingX;
        int currentY = 7;

        boolean isAlwaysActive = !(module instanceof ToggleableModule);
        boolean skipFirstRow = false;

        if (!isAlwaysActive) {
            BooleanProperty enabledProp = getBooleanProperty();
            BooleanButton enabledButton = new BooleanButton(module, enabledProp, currentX, currentY, buttonWidth, buttonHeight);
            booleanButtons.add(enabledButton);
            currentX += spacingX;
            skipFirstRow = true;
        }

        if (skipFirstRow) {
            currentY += spacingY;
            currentX = paddingX;
        }

        boolean firstRow = true;
        for (Property<?> prop : module.getProperties()) {
            if (prop != null && prop.getValue() instanceof Boolean) {
                BooleanProperty boolProp = (BooleanProperty) prop;

                if (firstRow || (currentX + spacingX > width - paddingX)) {
                    if (!firstRow) {
                        currentY += spacingY;
                    }
                    currentX = paddingX;
                    firstRow = false;
                }

                BooleanButton button = new BooleanButton(module, boolProp, currentX, currentY, buttonWidth, buttonHeight);
                booleanButtons.add(button);
                currentX += spacingX;
            }
        }


        boolean hasBooleans = module.getProperties().stream().anyMatch(p -> p.getValue() instanceof Boolean);
        boolean hasSliders = module.getProperties().stream().anyMatch(p -> p instanceof NumberProperty);
        boolean hasEnums = module.getProperties().stream().anyMatch(p -> p instanceof EnumProperty);
        boolean hasInputs = module.getProperties().stream().anyMatch(p -> p instanceof InputProperty);
        boolean hasContainers = module.getProperties().stream().anyMatch(p -> p instanceof ListProperty);

        if (!firstRow && !hasSliders && !hasEnums) {
            currentY += spacingY;
        }

        if (!hasBooleans && (hasSliders || hasEnums)) {
            currentY += spacingY - 2;
        }

        if (!firstRow && (hasSliders || hasEnums)) {
            currentY += spacingY + 8;
        }

        boolean renderedSlider = false;
        for (Property<?> prop : module.getProperties()) {
            if (prop instanceof NumberProperty) {
                ValueSlider slider = new ValueSlider((NumberProperty) prop, paddingX, currentY, width - paddingX * 2, 8);
                valueSliders.add(slider);
                currentY += 14;
                renderedSlider = true;
            }
        }

        if (renderedSlider && hasEnums) {
            currentY += 4;
        }

        for (Property<?> prop : module.getProperties()) {
            if (prop instanceof EnumProperty) {
                EnumDropdown dropdown = new EnumDropdown((EnumProperty<?>) prop, paddingX, currentY, width - paddingX * 2, 12);
                enumDropdowns.add(dropdown);
                currentY += 22;
            }
        }

        boolean anyAbove = !booleanButtons.isEmpty() || !valueSliders.isEmpty() || !enumDropdowns.isEmpty();

        if (anyAbove && hasInputs) {
            currentY += 4;
        }

        List<InputProperty> inputs = module.getProperties().stream().filter(p -> p instanceof InputProperty).map(p -> (InputProperty) p).toList();

        for (int i = 0; i < inputs.size(); i++) {
            StringInput input = new StringInput(inputs.get(i), paddingX, currentY, width - paddingX * 2, 12);
            stringInputs.add(input);
            currentY += input.getHeight();
            if (i < inputs.size() - 1) {
                currentY += 4;
            }
        }

        anyAbove = !booleanButtons.isEmpty() || !valueSliders.isEmpty() || !enumDropdowns.isEmpty() || !stringInputs.isEmpty();

        if (anyAbove && hasContainers) {
            currentY += 4;
        }

        List<ListProperty> listProps = module.getProperties().stream().filter(p -> p instanceof ListProperty).map(p -> (ListProperty) p).toList();

        for (int i = 0; i < listProps.size(); i++) {
            ListProperty listProp = listProps.get(i);
            PropertyContainer container = new PropertyContainer(listProp, paddingX, currentY, width - paddingX * 2);
            propertyContainers.add(container);
            currentY += container.getHeight();

            if (i < listProps.size() - 1) {
                currentY += 6;
            }
        }

        currentY += 8;

        this.height = Math.max(currentY + bottomPad, 25) - 5;
    }


    private BooleanProperty getBooleanProperty() {
        boolean isAlwaysActive = !(module instanceof ToggleableModule);
        return new BooleanProperty("Enabled", List.of(), isAlwaysActive || ((ToggleableModule) module).isEnabled()) {
            @Override
            public Boolean getValue() {
                return isAlwaysActive || ((ToggleableModule) module).isEnabled();
            }

            @Override
            public void setValue(Boolean value) {
                if (isAlwaysActive) return;
                ToggleableModule toggleable = (ToggleableModule) module;
                if (toggleable.isEnabled() != value) {
                    toggleable.toggle();
                }
            }
        };
    }

    public boolean mouseClicked(double guiMouseX, double guiMouseY, int drawX, int drawY, int button) {
        if (module instanceof ToggleableModule) {
            boolean hovered = guiMouseX >= bindTextX && guiMouseX < bindTextX + bindTextWidth && guiMouseY >= bindTextY && guiMouseY < bindTextY + bindTextHeight;
            if (hovered && button == 0) {
                listeningForKey = true;
                previousKey = Comu.getInstance().getKeybindManager().getKeybindByLabel(module.getName()).getKey();
                return true;
            }
        }

        for (BooleanButton boolButton : booleanButtons) {
            if (boolButton.mouseClicked(guiMouseX, guiMouseY, drawX, drawY, button)) {
                return true;
            }
        }

        for (ValueSlider slider : valueSliders) {
            if (slider.mouseClicked(drawX, drawY, guiMouseX, guiMouseY, button)) {
                return true;
            }
        }

        for (EnumDropdown dropdown : enumDropdowns) {
            if (dropdown.mouseClicked(guiMouseX, guiMouseY, button)) {
                return true;
            }
        }

        for (StringInput input : stringInputs) {
            if (input.mouseClicked(guiMouseX, guiMouseY, drawX, drawY, button)) return true;
        }

        for (PropertyContainer container : propertyContainers) {
            if (container.mouseClicked(guiMouseX, guiMouseY, drawX, drawY, button)) {
                return true;
            }
        }

        return false;
    }


    public void handleKeyTyped(int keyCode) {
        if (listeningForKey) {
            if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                Comu.getInstance().getKeybindManager().getKeybindByLabel(module.getName()).setKey(keyCode);
            } else {
                Comu.getInstance().getKeybindManager().getKeybindByLabel(module.getName()).setKey(GLFW.GLFW_KEY_UNKNOWN);
            }
            listeningForKey = false;
        }

        for (StringInput input : stringInputs) {
            input.keyTyped('\0', keyCode);
        }

        for (PropertyContainer container : propertyContainers) {
            container.handleKeyTyped('\0', keyCode);
        }
    }

    public void handleCharTyped(char c) {
        for (StringInput input : stringInputs) {
            input.keyTyped(c, GLFW.GLFW_KEY_UNKNOWN);
        }

        for (PropertyContainer container : propertyContainers) {
            container.handleKeyTyped(c, GLFW.GLFW_KEY_UNKNOWN);
        }
    }

    public boolean isListeningForKey() {
        return listeningForKey;
    }

    public void handleMouseDrag(int guiMouseX, int guiMouseY, int drawX, int drawY) {
        for (ValueSlider slider : valueSliders) {
            slider.handleMouse(drawX, drawY, guiMouseX, guiMouseY);
        }

        for (PropertyContainer container : propertyContainers) {
            container.handleMouseDrag(drawX, drawY, guiMouseX, guiMouseY);
        }
    }

    public List<EnumDropdown> getEnumDropdowns() {
        return this.enumDropdowns;
    }

    public List<StringInput> getStringInputs() {
        return this.stringInputs;
    }

    public List<StringInput> getAllStringInputs() {
        List<StringInput> all = new ArrayList<>(stringInputs);
        for (PropertyContainer container : propertyContainers) {
            container.collectStringInputs(all);
        }
        return all;
    }


    public void setHoverDisabled(boolean disabled) {
        this.hoverDisabled = disabled;
    }

    public void collectExpandedDropdowns(List<EnumDropdown> list) {
        for (EnumDropdown dropdown : enumDropdowns) {
            if (dropdown.isExpanded()) {
                list.add(dropdown);
            }
        }
        for (PropertyContainer container : propertyContainers) {
            container.collectExpandedDropdowns(list);
        }
    }

}
