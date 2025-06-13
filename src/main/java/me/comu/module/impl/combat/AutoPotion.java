package me.comu.module.impl.combat;

import me.comu.api.registry.event.Event;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.ItemUtils;

import java.util.List;

public class AutoPotion extends ToggleableModule {

    private final NumberProperty<Float> health = new NumberProperty<>("Health", List.of("h", "<3", "hearts", "heart"), 8F, 1F, 20F, 1F);
    private final NumberProperty<Long> delay = new NumberProperty<>("Delay", List.of("d", "wait"), 200L, 0L, 1000L, 50L);
    private final BooleanProperty eatCheck = new BooleanProperty("Eat Check", List.of("eatcheck", "checkeat", "eat"), true);
    private final BooleanProperty ladderCheck = new BooleanProperty("Ladder Check", List.of("laddercheck", "ladder", "ladders", "ladderscheck"), true);
    private final BooleanProperty defensive = new BooleanProperty("Defensive", List.of("defensive"), false);

    public AutoPotion() {
        super("Auto Potion", List.of("autopot", "autopotion"), Category.COMBAT, "Pots for you when at low health");
        offerProperties(health, delay, defensive, eatCheck, ladderCheck);
        this.listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (isPlayerOrWorldNull()) return;
                setSuffix(Integer.toString(ItemUtils.getPotCount()));


            }
        });
    }
}
