package me.comu.module.impl.render;

import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.module.impl.render.clickgui.comu.ComuGui;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;

import java.util.List;

public class ClickGui extends ToggleableModule {

    private final BooleanProperty blur = new BooleanProperty("Blur", List.of("b"), false);
    private final BooleanProperty gengar = new BooleanProperty("Gengar", List.of("g"), true);
    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.COMU);

    public enum Mode {
        COMU
    }

    public ClickGui() {
        super("Click Gui", List.of("clickgui", "gui"), Category.RENDER, "A gui screen to modify all your modules and client settings in one place");
        this.offerProperties(mode, blur, gengar);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        switch (mode.getValue()) {
            case COMU:
                mc.setScreen(ComuGui.getInstance());
                break;
        }
        setEnabled(false);
    }
}
