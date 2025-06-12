package me.comu.module.impl.render;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.Render2DEvent;
import me.comu.mixin.render.accessor.GameRendererAccessor;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;
import me.comu.utils.ClientUtils;
import me.comu.utils.ItemUtils;
import me.comu.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;


public class Nametags extends ToggleableModule {

    BooleanProperty health = new BooleanProperty("Health", List.of("h"), true);
    BooleanProperty hunger = new BooleanProperty("Hunger", List.of("food"), false);
    BooleanProperty heart = new BooleanProperty("Heart", List.of("hearts"), true);
    BooleanProperty equipment = new BooleanProperty("Equipment", List.of("armor", "a"), true);
    BooleanProperty self = new BooleanProperty("Self", List.of("s"), false);
    BooleanProperty potions = new BooleanProperty("Potions", List.of("potion", "pot", "p"), false);
    BooleanProperty distance = new BooleanProperty("Distance", List.of("blocks", "d", "dist"), false);
    BooleanProperty ping = new BooleanProperty("Ping", List.of("ping", "ms"), true);
    BooleanProperty lores = new BooleanProperty("Lore", List.of("lores", "l"), false);
    BooleanProperty invisibles = new BooleanProperty("Invisibles", List.of("invisible", "i", "visible", "visibles"), true);
    BooleanProperty durability = new BooleanProperty("Durability", List.of("dur", "dura"), true);

    EnumProperty<HealthLook> healthLook = new EnumProperty<>("Health-Look", List.of("healthlook", "healthmode", "hlook", "mode", "m"), HealthLook.TEN);

    public enum HealthLook {
        TEN, TWENTY, PERCENT
    }

    public Nametags() {
        super("Nametags", List.of("tags", "nt", "nameplates"), Category.RENDER, "Renders nametags on players");
        offerProperties(health, equipment, self, potions, distance, lores, invisibles, ping, healthLook, heart, hunger, durability);
        listeners.add(new Listener<>(Render2DEvent.class) {
            @Override
            public void call(Render2DEvent event) {
                if (mc.player == null || mc.world == null || mc.cameraEntity == null) return;

                for (PlayerEntity player : mc.world.getPlayers()) {
                    if ((!self.getValue() && player == mc.player) || (!invisibles.getValue() && player.isInvisible()))
                        continue;

                    Vec3d interpolatedPos = new Vec3d(player.lastRenderX + (player.getX() - player.lastRenderX) * event.getTickDelta(), player.lastRenderY + (player.getY() - player.lastRenderY) * event.getTickDelta(), player.lastRenderZ + (player.getZ() - player.lastRenderZ) * event.getTickDelta());

                    Vec3d playerPos = interpolatedPos.add(0, player.getHeight() + 0.1, 0);

                    Vec3d screenPos = worldToScreen(playerPos, event.getTickDelta());

                    if (screenPos == null) continue;

                    Text name = getDisplayName(player);
                    int textW = mc.textRenderer.getWidth(name);
                    int textH = mc.textRenderer.fontHeight;

                    double distance = mc.gameRenderer.getCamera().getPos().distanceTo(playerPos);
                    float scale = Math.clamp(40f / (float) distance, 1f, 4f);

                    var mats = event.getContext().getMatrices();
                    mats.push();

                    mats.translate((float) screenPos.x, (float) screenPos.y, 0);
                    mats.scale(scale, scale, 1);
                    mats.translate(-textW / 2f, -textH - 2f, 0);

                    event.getContext().fill(-2, -2, textW + 2, textH + 2, 0x77000000);

                    event.getContext().drawText(mc.textRenderer, name, 0, 0, 0xFFAAAAAA, true);
                    if (equipment.getValue()) {
                        renderEquipmentRow(player, event.getContext(), textW, textH, scale);
                    }

                    mats.pop();
                }
            }
        });
    }

    private Vec3d worldToScreen(Vec3d worldPos, float tickDelta) {
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();

        Vec3d forward = Vec3d.fromPolar(mc.gameRenderer.getCamera().getPitch(), mc.gameRenderer.getCamera().getYaw());
        Vec3d up = forward.y == 1.0 || forward.y == -1.0 ? new Vec3d(0, 0, 1) : new Vec3d(0, 1, 0);

        Matrix4f viewMatrix = new Matrix4f().lookAt(new org.joml.Vector3f((float) camPos.x, (float) camPos.y, (float) camPos.z), new org.joml.Vector3f((float) (camPos.x + forward.x), (float) (camPos.y + forward.y), (float) (camPos.z + forward.z)), new org.joml.Vector3f((float) up.x, (float) up.y, (float) up.z));

        float fov = (float) Math.toRadians(((GameRendererAccessor) mc.gameRenderer).callGetFov(mc.gameRenderer.getCamera(), tickDelta, true));
        float aspect = (float) mc.getWindow().getFramebufferWidth() / mc.getWindow().getFramebufferHeight();
        Matrix4f projectionMatrix = new Matrix4f().perspective(fov, aspect, 0.05f, 1000.0f);

        Vector4f pos = new Vector4f((float) worldPos.x, (float) worldPos.y, (float) worldPos.z, 1.0f);
        pos.mul(viewMatrix);
        pos.mul(projectionMatrix);

        if (pos.w <= 0.0f) return null;

        pos.x /= pos.w;
        pos.y /= pos.w;
        pos.z /= pos.w;

        if (pos.x < -1 || pos.x > 1 || pos.y < -1 || pos.y > 1 || pos.z < -1 || pos.z > 1) return null;

        double screenX = (pos.x + 1.0) / 2.0 * mc.getWindow().getFramebufferWidth();
        double screenY = (1.0 - pos.y) / 2.0 * mc.getWindow().getFramebufferHeight();

        return new Vec3d(screenX, screenY, pos.z);
    }

    private void renderEquipmentRow(PlayerEntity player, DrawContext context, int textW, int textH, float scale) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i <= 5; i++) {
            ItemStack stack = ItemUtils.getEquipmentItem(player, i);
            if (!stack.isEmpty()) items.add(stack);
        }

        if (items.isEmpty()) return;

        float iconSpacing = 18f;
        float itemSize = 16f;
        float totalWidth = items.size() * iconSpacing;

        float startX = (textW - totalWidth) / 2f;

        float yOffset = -itemSize - 4f;

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            int omegaColor = ItemUtils.hasCurseOfVanishing(stack) ? 0x1d1f1d : 0xFF1706;
            boolean isOmegaOrSimilar = ItemUtils.isOmega(stack) || ItemUtils.isPurpleGodSword(stack) || ItemUtils.isPunch2OrBetter(stack);
            float x = startX + i * iconSpacing;

            RenderUtils.drawItem(context, stack, (int) x, (int) yOffset, 1f, true);

            if (durability.getValue() && stack.isDamageable()) {
                context.getMatrices().push();

                float scaleFactor = 0.40f;

                int durability = stack.getMaxDamage() - stack.getDamage();
                String durabilityText = String.valueOf(durability);
                int textWidth = mc.textRenderer.getWidth(durabilityText);
                int textHeight = mc.textRenderer.fontHeight;

                float unscaledX = x + 16 - textWidth * scaleFactor - 1;
                float unscaledY = yOffset + 16 - textHeight * scaleFactor - 2.8f;

                context.getMatrices().translate(unscaledX, unscaledY, 9999);
                context.getMatrices().scale(scaleFactor, scaleFactor, 1f);

                context.drawText(mc.textRenderer, durabilityText, 0, 0, 0xFFFF9999, true);

                context.getMatrices().pop();
            }
            List<String> enchants = ItemUtils.getShortenedEnchantments(stack);
            if (!enchants.isEmpty()) {
                context.getMatrices().push();

                float scaleFactor = 0.45f;
                context.getMatrices().translate(x, yOffset, 9999);
                context.getMatrices().scale(scaleFactor, scaleFactor, 1f);

                for (int j = 0; j < enchants.size(); j++) {
                    String txt = enchants.get(j);
                    context.drawText(mc.textRenderer, txt, 0, j * 8, isOmegaOrSimilar ? omegaColor : 0xFFFFFFFF, true);
                }

                context.getMatrices().pop();
            }

        }
    }

    private Text getDisplayName(PlayerEntity player) {
        MutableText name = player.getDisplayName().copy();

        if (player.getName().getString().equals(mc.getSession().getUsername())) {
            name = Text.literal("You");
        }

//        if (Comu.getInstance().getFriendManager().isFriend(player.getName())) {
//            name = Comu.getInstance().getFriendManager().getFriendByAliasOrLabel(player.getName()).getAlias();
//        }
//
//        if (Comu.getInstance().getEnemyManager().isEnemy(player.getName())) {
//            name = Comu.getInstance().getEnemyManager().getEnemyByAliasOrLabel(player.getName()).getAlias();
//        }
//        if (Comu.getInstance().getStaffManager().isStaff((player.getName()))) {
//            name = Comu.getInstance().getStaffManager().getStaffByAliasOrLabel(player.getName()).getAlias();
//        }

        if (ping.getValue()) {
            int pingInt = mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) != null ? mc.getNetworkHandler().getPlayerListEntry(player.getUuid()).getLatency() : -1;
            name.append(Text.literal(" [" + pingInt + "ms]").formatted(Formatting.GRAY));
        }


        float health = player.getHealth();
        Formatting color;
        if (health > 18) {
            color = Formatting.GREEN;
        } else if (health > 16) {
            color = Formatting.DARK_GREEN;
        } else if (health > 12) {
            color = Formatting.YELLOW;
        } else if (health > 8) {
            color = Formatting.GOLD;
        } else if (health > 5) {
            color = Formatting.RED;
        } else {
            color = Formatting.DARK_RED;
        }

        if (this.health.getValue()) {
            float displayHealth = switch (healthLook.getValue()) {
                case TEN -> health / 2;
                case PERCENT -> health * 5;
                default -> health;
            };

            String healthText = (health > 0 ? (int) displayHealth + (healthLook.getValue() == HealthLook.PERCENT ? "%" : "") : "Dead");
            name.append(Text.literal(" " + healthText).formatted(color));

            if (heart.getValue()) {
                name.append(Text.literal(" ❤").formatted(color));
            }
        }

        if (hunger.getValue()) {
            float hungerLevel = player.getHungerManager().getFoodLevel();
            Formatting hungerColor = hungerLevel > 6 ? Formatting.GREEN : hungerLevel < 6 ? Formatting.RED : Formatting.YELLOW;
            name.append(Text.literal(" " + (hungerLevel / 2)).formatted(hungerColor));
        }

        return name;
    }
}
