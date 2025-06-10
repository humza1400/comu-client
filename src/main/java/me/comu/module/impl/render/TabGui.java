package me.comu.module.impl.render;

import me.comu.Comu;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.InputEvent;
import me.comu.events.Render2DEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.module.impl.render.tabgui.TabGuiHandler;
import me.comu.module.impl.render.tabgui.TabGuiState;
import me.comu.module.impl.render.tabgui.comu.ComuTabGui;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;

import java.util.List;

public class TabGui extends ToggleableModule {

    private final BooleanProperty icons = new BooleanProperty("Icons", List.of("icon", "icon"), true);
    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.COMU);

    public enum Mode {
        COMU
    }

    private final TabGuiState guiState = new TabGuiState();

    public TabGui() {
        super("TabGui", List.of("tg"), Category.RENDER, "Interactable HUD overlay to toggle and modify modules");
        this.listeners.add(new Listener<>(InputEvent.class) {
            @Override
            public void call(InputEvent event) {
                HUD hud = (HUD) Comu.getInstance().getModuleManager().getModuleByName("HUD");
                if (hud == null || !hud.isEnabled()) return;
                if (event.getType() != InputEvent.Type.KEYBOARD_KEY_PRESS) return;
                TabGuiHandler.handleKey(event.getKey(), guiState);
            }
        });
    }

    public TabGuiState getGuiState() {
        return guiState;
    }
}
