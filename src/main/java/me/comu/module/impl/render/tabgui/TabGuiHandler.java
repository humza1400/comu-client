package me.comu.module.impl.render.tabgui;

import me.comu.Comu;
import me.comu.module.ToggleableModule;
import me.comu.property.Property;
import me.comu.property.properties.*;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class TabGuiHandler {

    public static void handleKey(int key, TabGuiState state) {
        if (!state.visible) return;

        List<ToggleableModule> modules = Comu.getInstance().getModuleManager().getModulesByCategory(state.getCurrentCategory());
        ToggleableModule currentModule = modules.isEmpty() ? null : modules.get(Math.min(state.selectedModule, modules.size() - 1));
        List<Property<?>> props = currentModule == null ? List.of() : currentModule.getProperties().stream().filter(p -> !(p instanceof InputProperty)).toList();

        List<Property<?>> currentProps = state.insideListProperty ? state.currentListProperties : props;
        int selectedIndex = state.insideListProperty ? state.selectedListProperty : state.selectedProperty;

        switch (key) {
            case GLFW.GLFW_KEY_UP -> {
                if (state.mainMenu) {
                    state.selectedTab = (state.selectedTab - 1 + state.categories.size()) % state.categories.size();
                    state.targetCategoryTransition = state.selectedTab * 12;
                } else if (!state.showProperties && !modules.isEmpty()) {
                    state.selectedModule = (state.selectedModule - 1 + modules.size()) % modules.size();
                    state.targetModuleTransition = state.selectedModule * 12;
                } else if (state.insideListProperty && !state.currentListProperties.isEmpty()) {
                    if (state.inPropertyFocus) {
                        Property<?> prop = state.currentListProperties.get(state.selectedListProperty);
                        if (prop instanceof NumberProperty<?> numberProp) {
                            numberProp.increment();
                        }
                    } else {
                        state.selectedListProperty = (state.selectedListProperty - 1 + state.currentListProperties.size()) % state.currentListProperties.size();
                        state.targetListPropertyTransition = state.selectedListProperty * 12;
                    }
                } else if (state.showProperties && !props.isEmpty()) {
                    if (state.inPropertyFocus) {
                        Property<?> prop = props.get(state.selectedProperty);
                        if (prop instanceof NumberProperty<?> numberProp) {
                            numberProp.increment();
                        }
                    } else {
                        state.selectedProperty = (state.selectedProperty - 1 + props.size()) % props.size();
                        state.targetPropertyTransition = state.selectedProperty * 12;
                    }
                }
            }

            case GLFW.GLFW_KEY_DOWN -> {
                if (state.mainMenu) {
                    state.selectedTab = (state.selectedTab + 1) % state.categories.size();
                    state.targetCategoryTransition = state.selectedTab * 12;
                } else if (!state.showProperties && !modules.isEmpty()) {
                    state.selectedModule = (state.selectedModule + 1) % modules.size();
                    state.targetModuleTransition = state.selectedModule * 12;
                } else if (state.insideListProperty && !state.currentListProperties.isEmpty()) {
                    if (state.inPropertyFocus) {
                        Property<?> prop = state.currentListProperties.get(state.selectedListProperty);
                        if (prop instanceof NumberProperty<?> numberProp) {
                            numberProp.decrement();
                        }
                    } else {
                        state.selectedListProperty = (state.selectedListProperty + 1) % state.currentListProperties.size();
                        state.targetListPropertyTransition = state.selectedListProperty * 12;
                    }
                } else if (state.showProperties && !props.isEmpty()) {
                    if (state.inPropertyFocus) {
                        Property<?> prop = props.get(state.selectedProperty);
                        if (prop instanceof NumberProperty<?> numberProp) {
                            numberProp.decrement();
                        }
                    } else {
                        state.selectedProperty = (state.selectedProperty + 1) % props.size();
                        state.targetPropertyTransition = state.selectedProperty * 12;
                    }
                }
            }

            case GLFW.GLFW_KEY_LEFT -> {
                if (state.inPropertyFocus) {
                    state.inPropertyFocus = false;
                } else if (state.insideListProperty) {
                    if (!state.parentListStack.isEmpty()) {
                        state.currentList = state.parentListStack.remove(state.parentListStack.size() - 1);
                        state.currentListProperties = state.currentList.getProperties().stream().filter(p -> !(p instanceof InputProperty)).toList();
                        state.selectedListProperty = 0;
                        state.listPropertyTransition = 0;
                        state.targetListPropertyTransition = 0;
                        state.inPropertyFocus = false;
                    } else {
                        state.insideListProperty = false;
                        state.currentList = null;
                        state.currentListProperties = List.of();
                        state.selectedListProperty = 0;
                        state.listPropertyTransition = 0;
                        state.targetListPropertyTransition = 0;
                        state.inPropertyFocus = false;
                    }
                } else if (state.showProperties) {
                    state.showProperties = false;
                    state.selectedProperty = 0;
                    state.propertyTransition = 0;
                    state.targetPropertyTransition = 0;
                } else {
                    state.mainMenu = true;
                    state.selectedModule = 0;
                    state.selectedProperty = 0;
                    state.inPropertyFocus = false;
                    state.showProperties = false;
                    state.moduleTransition = 0;
                    state.targetModuleTransition = 0;
                }
            }

            case GLFW.GLFW_KEY_RIGHT -> {
                if (!state.mainMenu && state.showProperties && !currentProps.isEmpty()) {
                    Property<?> prop = currentProps.get(selectedIndex);

                    if (state.inPropertyFocus && prop instanceof NumberProperty) {
                        state.inPropertyFocus = false;
                        return;
                    }

                    if (!state.inPropertyFocus) {
                        if (prop instanceof BooleanProperty || prop instanceof EnumProperty) {
                            prop.toggle();
                        } else if (prop instanceof NumberProperty) {
                            state.inPropertyFocus = true;
                        } else if (prop instanceof ListProperty listProp) {
                            if (state.insideListProperty) {
                                state.parentListStack.add(state.currentList);
                            }
                            state.insideListProperty = true;
                            state.currentList = listProp;
                            state.currentListProperties = listProp.getProperties().stream().filter(p -> !(p instanceof InputProperty)).toList();
                            state.selectedListProperty = 0;
                            state.listPropertyTransition = 0;
                            state.targetListPropertyTransition = 0;
                            state.inPropertyFocus = false;
                        }
                    }
                } else if (!state.mainMenu && !state.showProperties && !modules.isEmpty()) {
                    currentModule.toggle();
                } else if (state.mainMenu && !modules.isEmpty()) {
                    state.mainMenu = false;
                    state.selectedModule = 0;
                    state.moduleTransition = 0;
                    state.targetModuleTransition = 0;
                    state.parentListStack.clear();
                }
            }

            case GLFW.GLFW_KEY_ENTER -> {
                if (state.inPropertyFocus && !currentProps.isEmpty()) {
                    Property<?> prop = currentProps.get(selectedIndex);
                    if (!(prop instanceof NumberProperty)) {
                        prop.toggle();
                    }
                } else if (!state.mainMenu && !state.showProperties && !modules.isEmpty()) {
                    if (!props.isEmpty()) {
                        state.showProperties = true;
                        state.propertyTransition = 0;
                        state.targetPropertyTransition = 0;
                    } else {
                        currentModule.toggle();
                    }
                } else if (!state.mainMenu && state.showProperties && !currentProps.isEmpty()) {
                    Property<?> prop = currentProps.get(selectedIndex);
                    if (prop instanceof BooleanProperty || prop instanceof EnumProperty) {
                        prop.toggle();
                    } else if (prop instanceof NumberProperty) {
                        state.inPropertyFocus = true;
                    } else if (prop instanceof ListProperty listProp) {
                        if (state.insideListProperty) {
                            state.parentListStack.add(state.currentList);
                        }
                        state.insideListProperty = true;
                        state.currentList = listProp;
                        state.currentListProperties = listProp.getProperties().stream().filter(p -> !(p instanceof InputProperty)).toList();
                        state.selectedListProperty = 0;
                        state.listPropertyTransition = 0;
                        state.targetListPropertyTransition = 0;
                        state.inPropertyFocus = false;
                    }
                }
            }
        }
    }
}
