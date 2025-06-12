package me.comu.module.impl.render.tabgui.comu;

import me.comu.Comu;
import me.comu.module.ToggleableModule;
import me.comu.module.impl.render.tabgui.TabGuiRenderer;
import me.comu.module.impl.render.tabgui.TabGuiState;
import me.comu.property.Property;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.InputProperty;
import me.comu.property.properties.ListProperty;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class ComuTabGui implements TabGuiRenderer {

    @Override
    public void render(DrawContext context, TabGuiState state, int x, int y) {
        if (!state.visible) return;

        float animationSpeed = 6.0f;
        state.categoryTransition += (state.targetCategoryTransition - state.categoryTransition) / animationSpeed;
        state.moduleTransition += (state.targetModuleTransition - state.moduleTransition) / animationSpeed;
        state.propertyTransition += (state.targetPropertyTransition - state.propertyTransition) / animationSpeed;
        state.listPropertyTransition += (state.targetListPropertyTransition - state.listPropertyTransition) / animationSpeed;

        MinecraftClient mc = MinecraftClient.getInstance();
        float scale = 1.0f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1f);

        float scaledX = x / scale;
        float scaledY = y / scale;

        int topPadding = 1;
        int gapPadding = 3;
        int itemHeight = 12;
        int bgColor = 0x50000000;
        int outlineColor = 0x99000000;
        int selectedBg = 0xFFFF6B6B;
        int propertyFocusColor = 0xFFFF6B6B;

        int maxCategoryWidth = state.categories.stream().mapToInt(cat -> mc.textRenderer.getWidth(cat.getName())).max().orElse(60);
        int categoryBoxWidth = maxCategoryWidth + 6;
        int categoryBoxHeight = state.categories.size() * itemHeight + topPadding;

        context.fill((int) scaledX, (int) scaledY, (int) (scaledX + categoryBoxWidth), (int) (scaledY + categoryBoxHeight), bgColor);
        context.drawBorder((int) scaledX, (int) scaledY, categoryBoxWidth, categoryBoxHeight, outlineColor);

        for (int i = 0; i < state.categories.size(); i++) {
            boolean selected = i == state.selectedTab;
            int itemY = (int) (scaledY + topPadding + i * itemHeight);
            int drawY = itemY + 1;

            if (selected) {
                float selectorY = scaledY + topPadding + state.categoryTransition;
                int selectorTop = Math.round(selectorY);
                context.fill((int) scaledX + 1, selectorTop, (int) (scaledX + categoryBoxWidth) - 1, selectorTop + itemHeight - 1, selectedBg);
            }

            context.drawText(mc.textRenderer, state.categories.get(i).getName(), (int) (scaledX + 2), drawY, 0xFFFFFFFF, true);
        }

        if (!state.mainMenu) {
            List<ToggleableModule> modules = Comu.getInstance().getModuleManager().getModulesByCategory(state.getCurrentCategory());

            int maxModuleWidth = modules.stream().mapToInt(mod -> mc.textRenderer.getWidth(mod.getDisplayName())).max().orElse(60);
            int moduleBoxWidth = maxModuleWidth + 6;
            int moduleBoxHeight = modules.size() * itemHeight + topPadding;

            int moduleX = (int) (scaledX + categoryBoxWidth + gapPadding);

            context.fill(moduleX, (int) scaledY, moduleX + moduleBoxWidth, (int) (scaledY + moduleBoxHeight), bgColor);
            context.drawBorder(moduleX, (int) scaledY, moduleBoxWidth, moduleBoxHeight, outlineColor);

            for (int i = 0; i < modules.size(); i++) {
                boolean selected = i == state.selectedModule;
                int itemY = (int) (scaledY + topPadding + i * itemHeight);
                int drawY = itemY + 1;

                if (selected) {
                    float selectorY = scaledY + topPadding + state.moduleTransition;
                    int selectorTop = Math.round(selectorY);
                    context.fill(moduleX + 1, selectorTop, moduleX + moduleBoxWidth - 1, selectorTop + itemHeight - 1, selectedBg);
                }

                ToggleableModule mod = modules.get(i);
                context.drawText(mc.textRenderer, mod.getDisplayName(), moduleX + 2, drawY, mod.isEnabled() ? propertyFocusColor : 0xFFFFFFFF, true);
            }

            if (state.showProperties) {
                ToggleableModule mod = modules.get(state.selectedModule);
                List<Property<?>> properties = mod.getProperties().stream().filter(p -> !(p instanceof InputProperty)).toList();

                int maxPropWidth = properties.stream().mapToInt(prop -> {
                    if (prop instanceof ListProperty) return mc.textRenderer.getWidth(prop.getName());
                    return mc.textRenderer.getWidth(prop.getName() + ": " + prop.getValue());
                }).max().orElse(60);

                int propBoxWidth = maxPropWidth + 6;
                int propBoxHeight = properties.size() * itemHeight + topPadding;
                int propX = moduleX + moduleBoxWidth + gapPadding;

                context.fill(propX, (int) scaledY, propX + propBoxWidth, (int) (scaledY + propBoxHeight), bgColor);
                context.drawBorder(propX, (int) scaledY, propBoxWidth, propBoxHeight, outlineColor);

                for (int i = 0; i < properties.size(); i++) {
                    boolean selected = i == state.selectedProperty;
                    int itemY = (int) (scaledY + topPadding + i * itemHeight);
                    int drawY = itemY + 1;

                    if (selected) {
                        float selectorY = scaledY + topPadding + state.propertyTransition;
                        int selectorTop = Math.round(selectorY);
                        context.fill(propX + 1, selectorTop, propX + propBoxWidth - 1, selectorTop + itemHeight - 1, selectedBg);
                    }

                    Property<?> prop = properties.get(i);
                    String propName = prop.getName();

                    int nameWidth = mc.textRenderer.getWidth(propName);
                    int nameColor = selected && (state.inPropertyFocus || state.insideListProperty) ? propertyFocusColor : 0xFFFFFFFF;
                    context.drawText(mc.textRenderer, propName, propX + 2, drawY, nameColor, true);

                    if (!(prop instanceof ListProperty)) {
                        String rawValue;
                        if (prop instanceof EnumProperty<?> enumProp) {
                            rawValue = enumProp.getFormattedValue();
                        } else {
                            rawValue = String.valueOf(prop.getValue());
                        }

                        context.drawText(mc.textRenderer, ": ", propX + 2 + nameWidth, drawY, 0xFFFFFFFF, true);
                        int colonWidth = mc.textRenderer.getWidth(": ");
                        int valueX = propX + 2 + nameWidth + colonWidth;

                        int valueColor = (prop.getValue() instanceof Boolean b && !b) ? 0xFFAAAAAA : 0xFFFFFFFF;
                        context.drawText(mc.textRenderer, rawValue, valueX, drawY, valueColor, true);
                    }

                }

                if (state.insideListProperty && state.currentList != null) {
                    List<Property<?>> listProps = state.currentListProperties;
                    int maxListPropWidth = listProps.stream().mapToInt(p -> {
                        if (p instanceof ListProperty) return mc.textRenderer.getWidth(p.getName());
                        return mc.textRenderer.getWidth(p.getName() + ": " + p.getValue());
                    }).max().orElse(60);
                    int listPropBoxWidth = maxListPropWidth + 6;
                    int listPropBoxHeight = listProps.size() * itemHeight + topPadding;
                    int listPropX = propX + propBoxWidth + gapPadding;

                    context.fill(listPropX, (int) scaledY, listPropX + listPropBoxWidth, (int) (scaledY + listPropBoxHeight), bgColor);
                    context.drawBorder(listPropX, (int) scaledY, listPropBoxWidth, listPropBoxHeight, outlineColor);

                    for (int i = 0; i < listProps.size(); i++) {
                        boolean selected = i == state.selectedListProperty;
                        int itemY = (int) (scaledY + topPadding + i * itemHeight);
                        int drawY = itemY + 1;

                        if (selected) {
                            float selectorY = scaledY + topPadding + state.listPropertyTransition;
                            int selectorTop = Math.round(selectorY);
                            context.fill(listPropX + 1, selectorTop, listPropX + listPropBoxWidth - 1, selectorTop + itemHeight - 1, selectedBg);
                        }

                        Property<?> prop = listProps.get(i);
                        String propName = prop.getName();
                        int nameWidth = mc.textRenderer.getWidth(propName);
                        int nameColor = selected && state.inPropertyFocus ? propertyFocusColor : 0xFFFFFFFF;

                        context.drawText(mc.textRenderer, propName, listPropX + 2, drawY, nameColor, true);

                        if (!(prop instanceof ListProperty)) {
                            String rawValue;
                            if (prop instanceof EnumProperty<?> enumProp) {
                                rawValue = enumProp.getFormattedValue();
                            } else {
                                rawValue = String.valueOf(prop.getValue());
                            }

                            context.drawText(mc.textRenderer, ": ", listPropX + 2 + nameWidth, drawY, 0xFFFFFFFF, true);
                            int colonWidth = mc.textRenderer.getWidth(": ");
                            int valueX = listPropX + 2 + nameWidth + colonWidth;

                            int valueColor = (prop.getValue() instanceof Boolean b && !b) ? 0xFFAAAAAA : 0xFFFFFFFF;
                            context.drawText(mc.textRenderer, rawValue, valueX, drawY, valueColor, true);
                        }
                    }
                }
            }
        }

        context.getMatrices().pop();
    }
}
