package me.comu.module.impl.active;

import me.comu.Comu;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.MotionEvent;
import me.comu.events.TickEvent;
import me.comu.hooks.Hook;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.Module;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.ListProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.MovementUtils;
import net.minecraft.client.option.Perspective;

import java.util.List;

public class Overlay extends Module {

    BooleanProperty customHitAnimation = new BooleanProperty("Hit Animation", List.of("swinganimation", "hitanimation"), false);
    BooleanProperty blockModes = new BooleanProperty("Blocking Modes", List.of("swinganimation", "hitanimation"), false);
    BooleanProperty blockOverlay = new BooleanProperty("Block Overlay", List.of("blockoverlay", "blockpos"), true);
    BooleanProperty noFire = new BooleanProperty("Fire Animation", List.of("fire", "fireeffect", "fireanimation"), false);
    BooleanProperty noVanillPotionHud = new BooleanProperty("Potion Icons", List.of("vanillapotionicon", "vanillapotionicons", "vanillastatusicon", "vanillastatusicons", "effecticon", "effecticons", "vanillapotions", "vanillapotion", "vanillapots", "vanillapot", "poticons", "poticon", "potionicon", "potionicons"), true);

    private final NumberProperty<Float> zoomFactor = new NumberProperty<>("Zoom Factor", List.of("magnification", "zoomfactor", "zoom", "magnify"), 0.2f, 0.1f, 1.0f, 0.1f);
    private final NumberProperty<Float> smoothFactor = new NumberProperty<>("Smooth Factor", List.of("lerp", "smooth", "easing", "smoothfactor"), 0.15f, 0.01f, 1.0f, 0.01f);

    public enum HIT_ANIMATIONS {
        COMU
    }

    public enum BLOCKING_MODES {
        VANILLA, ONEDOTSEVEN, TAP, SWEEP
    }

    ListProperty noRender = new ListProperty("No Render", List.of("norender", "nr"), List.of(noFire, noVanillPotionHud));
    ListProperty zoom = new ListProperty("Zoom", List.of(), List.of(zoomFactor, smoothFactor));

    private float savedYaw, savedPitch;
    private boolean wasFreelookActive = false;

    public Overlay() {
        super("Overlay", List.of(), Category.PERSISTENT, "Modify vanilla minecraft screen behavior");
        offerProperties(blockOverlay, zoom, noRender);
        Comu.getInstance().getEventManager().register(new Listener<>(TickEvent.class) {
            @Override
            public void call(TickEvent event) {
                if (mc.player == null || mc.options == null) return;
                boolean freelookHeld = Hook.getFreelookKeybind().isPressed();

                if (freelookHeld && !wasFreelookActive) {
                    savedYaw = mc.player.getYaw();
                    savedPitch = mc.player.getPitch();
                    wasFreelookActive = true;
                }

                if (!freelookHeld && wasFreelookActive) {
                    savedYaw = mc.player.getYaw();
                    savedPitch = mc.player.getPitch();
                    wasFreelookActive = false;
                }

                if (freelookHeld) {
                    if (mc.options.getPerspective() == Perspective.THIRD_PERSON_BACK || mc.options.getPerspective() == Perspective.THIRD_PERSON_FRONT) {
                        mc.player.setYaw(savedYaw);
                        mc.player.setPitch(savedPitch);
                    }

                }
            }
        });
    }
}
