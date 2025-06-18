package me.comu.module.impl.combat;

import me.comu.api.registry.event.listener.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.events.MotionEvent;
import me.comu.events.TickEvent;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;
import me.comu.property.properties.NumberProperty;
import me.comu.utils.RotationUtils;
import net.minecraft.entity.Entity;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.StreamSupport;

public class KillAura extends ToggleableModule {

    BooleanProperty mobs = new BooleanProperty("Monsters", List.of("mobs", "mob", "zombie", "zombies", "skeleton", "skeletons", "monster", "mon", "p"), true);
    BooleanProperty passives = new BooleanProperty("Passives", List.of("passive", "villager", "villagers", "neutral", "neutrals", "animal", "cow", "cows", "sheep", "anim", "a"), true);
    BooleanProperty players = new BooleanProperty("Players", List.of("player", "people"), true);
    BooleanProperty rayTrace = new BooleanProperty("Ray-Trace", List.of("raytrace", "rayt", "ray", "rt"), true);
    BooleanProperty cooldownAttack = new BooleanProperty("Cooldown", List.of("1.9pvp", "19pvp", "1.9", "19", "cd"), false);
    BooleanProperty rangeVisualizer = new BooleanProperty("Visualize Range", List.of("vrange", "vreach", "rangev", "reachv", "visualizerange", "rangevisualize", "reachvisualize", "visualizereach", "reachvisualizer","rangevisualizer"), false);

    NumberProperty<Float> range = new NumberProperty<>("Reach", List.of("range", "r"), 4f, 3f, 6f, 0.1f);
    NumberProperty<Integer> aps = new NumberProperty<>("APS", List.of("speed", "cps"), 10, 1, 20, 1);
    NumberProperty<Integer> fov = new NumberProperty<>("FOV", List.of(), 180, 1, 180, 30);

    EnumProperty<Mode> mode = new EnumProperty<>("Targeting", List.of("m", "t", "mode"), Mode.SINGLE);
    NumberProperty<Float> delta = new NumberProperty<>("Delta", List.of(), 1.2F, 0F, 2F, 0.1F);
    NumberProperty<Integer> smooth = new NumberProperty<>("Yaw-Delta", List.of(), 70, 0, 100, 10);

    public enum Mode {SINGLE, SWITCH}

    private final Stopwatch stopwatch = new Stopwatch();
    private Entity target = null;
    private Entity lastTarget = null;
    public static float clientRotationYaw, clientRotationPitch;
    private boolean shouldReturnToClientRotation = false;
    private float currentYaw, currentPitch;

    public KillAura() {
        super("Kill Aura", List.of("killaura","aura", "ka"), Category.COMBAT, "Attacks any valid targets within reach");
        offerProperties(range, aps, fov, mode, mobs, passives, players, rayTrace, cooldownAttack, delta, smooth, rangeVisualizer);
        listeners.add(new Listener<>(MotionEvent.class) {
            @Override
            public void call(MotionEvent event) {
                if (event.isPre()) {
                    updateTarget();
                    if (target == null) {
                        float baseYaw = mc.player.getYaw();
                        float basePitch = mc.player.getPitch();

                        float[] normalized = normalizeRotation(baseYaw, basePitch, currentYaw, currentPitch, delta.getValue());

                        currentYaw = normalized[0];
                        currentPitch = normalized[1];

                        event.setYaw(currentYaw);
                        event.setPitch(currentPitch);
                        return;
                    }

                    float baseYaw = currentYaw;
                    float basePitch = currentPitch;

                    float[] rotations = RotationUtils.getRotations(target);

                    float rawTargetYaw = rotations[0];
                    float targetYaw = wrapYawToBase(rawTargetYaw, baseYaw);
                    float targetPitch = clamp(rotations[1], -87f, 87f);

                    float[] normalized = normalizeRotation(targetYaw, (float) (targetPitch + getRandomInRange(-5, 5)), baseYaw, basePitch, delta.getValue());

                    currentYaw = normalized[0];
                    currentPitch = normalized[1];

                    event.setYaw(currentYaw);
                    event.setPitch(currentPitch);
                    Logger.getLogger().print("Yaw " + currentYaw + " Pitch " + currentPitch);
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
                        int baseDelay = 1000 / aps.getValue();
                        int jitter = ThreadLocalRandom.current().nextInt(-75, 75);
                        int targetDelay = Math.max(60, baseDelay + jitter);
                        if (stopwatch.hasCompleted(targetDelay, true)) {
                            mc.player.swingHand(mc.player.getActiveHand());
                            mc.player.attack(target);
                            mc.interactionManager.attackEntity(mc.player, target);
                        }
                    }
                }
            }
        });

        listeners.add(new Listener<>(MotionEvent.class, true) {
            @Override
            public void call(MotionEvent event) {
                clientRotationYaw = mc.player.getYaw();
                clientRotationPitch = mc.player.getPitch();

                if (shouldReturnToClientRotation) {
                    float wrappedYaw = wrapYawToBase(mc.player.getYaw(), currentYaw);
                    float wrappedPitch = clamp(mc.player.getPitch(), -90f, 90f);
                    float[] normalized = normalizeRotation(wrappedYaw, wrappedPitch, currentYaw, currentPitch, delta.getValue());


                    currentYaw = normalized[0];
                    currentPitch = normalized[1];

                    event.setYaw(currentYaw);
                    event.setPitch(currentPitch);

                    float yawDiff = Math.abs(currentYaw - mc.player.getYaw());
                    float pitchDiff = Math.abs(currentPitch - mc.player.getPitch());

                    if (yawDiff < 0.1f && pitchDiff < 0.1f) {
                        shouldReturnToClientRotation = false;
                    }

                }
            }
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(isPlayerOrWorldNull()) return;
        target = null;
        shouldReturnToClientRotation = true;
    }

    public void onEnable() {
        super.onEnable();
        if(isPlayerOrWorldNull()) return;
        currentYaw = mc.player.getYaw();
        currentPitch = mc.player.getPitch();
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

    public static double getRandomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    private double getGCD() {
        double sensitivity = mc.options.getMouseSensitivity().getValue();
        double f = sensitivity * 0.6 + 0.2;
        return f * f * f * 8.0 * 0.15;
    }

    private float[] normalizeRotation(float targetYaw, float targetPitch, float baseYaw, float basePitch, float smoothFactor) {
        double gcd = getGCD();

        float yawDelta = RotationUtils.getAngleDelta(baseYaw, targetYaw);
        float pitchDelta = targetPitch - basePitch;

        float baseYawChange = smooth.getValue();
        float basePitchChange = 22.0f;

        float maxYawChange = addRandomization(baseYawChange, 2.0f);
        float maxPitchChange = addRandomization(basePitchChange, 1.5f);

        float smoothedYawDelta = Math.max(-maxYawChange, Math.min(yawDelta * smoothFactor, maxYawChange));
        float smoothedPitchDelta = Math.max(-maxPitchChange, Math.min(pitchDelta * smoothFactor, maxPitchChange));

        float normalizedYaw = baseYaw + Math.round(smoothedYawDelta / (float)gcd) * (float)gcd;
        float normalizedPitch = basePitch + Math.round(smoothedPitchDelta / (float)gcd) * (float)gcd;

        normalizedPitch = clamp(normalizedPitch, -90f, 90f);

        return new float[]{normalizedYaw, normalizedPitch};
    }
    private static float clamp(float v, float mn, float mx) {
        return v < mn ? mn : Math.min(v, mx);
    }

    private static float addRandomization(float value, float maxVariation) {
        return value + (ThreadLocalRandom.current().nextFloat() - 0.5f) * maxVariation;
    }

    public static float wrapYawToBase(float targetYaw, float baseYaw) {
        float wrapped = targetYaw;
        while (wrapped < baseYaw - 180.0f) {
            wrapped += 360.0f;
        }
        while (wrapped >= baseYaw + 180.0f) {
            wrapped -= 360.0f;
        }
        return wrapped;
    }

    @Override
    public String getSuffix() {
        return mode.getFormattedValue();
    }
}