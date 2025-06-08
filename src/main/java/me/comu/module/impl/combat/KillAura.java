package me.comu.module.impl.combat;

import me.comu.api.registry.event.listener.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.events.MotionEvent;
import me.comu.events.TickEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.RotationUtils;
import net.minecraft.entity.Entity;

import java.util.List;
import java.util.stream.StreamSupport;

public class KillAura extends ToggleableModule {

    BooleanProperty mobs = new BooleanProperty("Monsters", List.of("mobs", "mob", "zombie", "zombies", "skeleton", "skeletons"), true);
    BooleanProperty passives = new BooleanProperty("Passives", List.of("passive", "villager", "villagers", "neutral","neutrals", "animal", "cow", "cows", "sheep"), true);
    BooleanProperty players = new BooleanProperty("Players", List.of("player", "people"), true);
    BooleanProperty rayTrace = new BooleanProperty("Ray-Trace", List.of("raytrace", "rayt", "ray", "rt"), true);
    BooleanProperty cooldownAttack = new BooleanProperty("Cooldown", List.of("1.9pvp", "19pvp", "1.9", "19", "cd"), false);

    NumberProperty<Float> range = new NumberProperty<>("Reach", List.of("range", "r"), 4f, 3f, 6f, 0.1f);
    NumberProperty<Integer> aps = new NumberProperty<>("APS", List.of("speed", "cps"), 10, 1, 20, 1);
    NumberProperty<Integer> fov = new NumberProperty<>("FOV", List.of(), 180, 1, 180, 30);

    EnumProperty<Mode> mode = new EnumProperty<>("Targeting", List.of("m", "t", "mode"), Mode.SINGLE);

    public enum Mode {SINGLE, SWITCH}

    private final Stopwatch stopwatch = new Stopwatch();
    private Entity target = null;
    private Entity lastTarget = null;

    public KillAura() {
        super("Kill Aura", List.of("aura", "ka"), Category.MOVEMENT, "Automatically starts attacking any valid targets around you");
        offerProperties(range, aps, fov, mode, mobs, passives, players, rayTrace, cooldownAttack);
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (event.isPre()) {
                    updateTarget();
                    if (target != null) {
                        float[] rotations = RotationUtils.getRotations(target);
                        event.setYaw(rotations[0]);
                        event.setPitch(rotations[1]);
                    }
                }
            }
        });

        listeners.add(new Listener<>(TickEvent.class) {
            @Override
            public void call(TickEvent event) {
                if (mc.player == null || target == null || mc.interactionManager == null) {
                    return;
                }
                if (event.getPhase() == TickEvent.Phase.PRE) {
                    if (cooldownAttack.getValue()) {
                        float cooldown = mc.player.getAttackCooldownProgress(0);
                        if (cooldown >= 1.0f) {
                            mc.player.swingHand(mc.player.getActiveHand());
                            mc.player.attack(target);
                            mc.interactionManager.attackEntity(mc.player, target);
                        }
                    } else {
                        if (stopwatch.hasCompleted(1000 / aps.getValue(), true)) {
                            mc.player.swingHand(mc.player.getActiveHand());
                            mc.player.attack(target);
                            mc.interactionManager.attackEntity(mc.player, target);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        target = null;
    }

    private void updateTarget() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        List<Entity> validTargets = StreamSupport.stream(mc.world.getEntities().spliterator(), false).filter(this::isValid).sorted((a, b) -> Float.compare(mc.player.distanceTo(a), mc.player.distanceTo(b))).toList();

        if (validTargets.isEmpty()) {
            target = null;
            return;
        }

        switch (mode.getValue()) {
            case SINGLE:
                if (isValid(lastTarget)) {
                    target = lastTarget;
                } else {
                    target = validTargets.getFirst();
                    lastTarget = target;
                }
                break;

            case SWITCH:
                int currentIndex = lastTarget != null ? validTargets.indexOf(lastTarget) : -1;
                int nextIndex = (currentIndex + 1) % validTargets.size();
                target = validTargets.get(nextIndex);
                lastTarget = target;
                break;
        }
    }

    private boolean isValid(Entity entity) {
        if (mc.player == null || entity == null || entity == mc.player || !entity.isAlive() || mc.player.isUsingItem()) {
            return false;
        }

        if (rayTrace.getValue() && !mc.player.canSee(entity)) {
            return false;
        }

        boolean isTargetType = (players.getValue() && entity instanceof net.minecraft.entity.player.PlayerEntity) || (mobs.getValue() && entity instanceof net.minecraft.entity.mob.Monster) || (passives.getValue() && entity instanceof net.minecraft.entity.passive.PassiveEntity);

        if (!isTargetType) {
            return false;
        }

        float yawToTarget = RotationUtils.getRotations(entity)[0];
        float yawDifference = Math.abs(RotationUtils.getDistanceBetweenAngles(mc.player.getYaw(), yawToTarget));
        if (yawDifference > fov.getValue()) {
            return false;
        }

        return mc.player.distanceTo(entity) <= range.getValue();
    }

}
