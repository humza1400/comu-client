package me.comu.module.impl.movement;

import me.comu.api.registry.event.Event;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.TickEvent;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.EnumProperty;
import net.minecraft.world.tick.Tick;

import java.util.List;

public class NoFall extends ToggleableModule {

    private EnumProperty<Mode> mode = new EnumProperty<Mode>("Mode", List.of("m"), Mode.SpoofGround);

    public NoFall() {
        super("NoFall", List.of("nf"), Category.MOVEMENT, "Attempts To Stop Fall Damage");

        listeners.add(new Listener<>(TickEvent.class) {
            @Override
            public void call(TickEvent event) {
                if(isPlayerOrWorldNull())return;
                switch(mode.getValue()){
                    case SpoofGround:
                        if(mc.player.fallDistance >= 3){
                            mc.player.setOnGround(true);
                        }
                    break;
                }
            }
        });
    }

    public enum Mode {SpoofGround}
}
