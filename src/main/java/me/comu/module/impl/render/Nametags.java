package me.comu.module.impl.render;

import me.comu.Comu;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.Render2DEvent;
import me.comu.mixin.render.accessor.GameRendererAccessor;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;
import me.comu.utils.ItemUtils;
import me.comu.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
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
    BooleanProperty enchants = new BooleanProperty("Enchantments", List.of("enchants", "e", "ench"), true);
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
        offerProperties(health, equipment, enchants, self, potions, distance, lores, invisibles, ping, healthLook, heart, hunger, durability);
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
            boolean isOmegaOrSimilar = ItemUtils.isOmega(stack) || ItemUtils.isPurpleGodSword(stack) || ItemUtils.isPunch3OrBetter(stack);
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

            boolean isMainHand = stack == player.getMainHandStack();
            if (!enchants.isEmpty() && (this.enchants.getValue() || isMainHand)) {
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
        String nameStr = player.getDisplayName() == null ? player.getName().getString() : player.getDisplayName().getString();
        MutableText name;

        TextColor color = TextColor.parse("#AAAAAA").result().orElse(TextColor.fromRgb(0xAAAAAA));
        String displayText = nameStr;

        if (nameStr.equals(mc.getSession().getUsername())) {
            displayText = "You";
        }

        if (Comu.getInstance().getFriendManager().isFriend(nameStr)) {
            displayText = Comu.getInstance().getFriendManager().getByNameOrAlias(nameStr).getAlias();
            color = TextColor.parse("#55C0ED").result().orElse(TextColor.fromRgb(0x55C0ED));
        } else if (player.isInvisible()) {
            color = TextColor.parse("#ef0147").result().orElse(TextColor.fromRgb(0xef0147));
        } else if (player.isSneaking()) {
            color = TextColor.parse("#9d1995").result().orElse(TextColor.fromRgb(0x9d1995));
        } else if (Comu.getInstance().getEnemyManager().isEnemy(nameStr)) {
            displayText = Comu.getInstance().getEnemyManager().getEnemyByNameOrAlias(nameStr).getAlias();
            color = TextColor.parse("#f442e8").result().orElse(TextColor.fromRgb(0xf442e8));
        } else if (Comu.getInstance().getStaffManager().isStaff(nameStr)) {
            displayText = Comu.getInstance().getStaffManager().getStaffByNameOrAlias(nameStr).getAlias();
            color = TextColor.parse("#FFDD2E").result().orElse(TextColor.fromRgb(0xFFDD2E));
        }

        name = Text.literal(displayText).setStyle(Style.EMPTY.withColor(color));

        if (ping.getValue()) {
            int pingInt = mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) != null ? mc.getNetworkHandler().getPlayerListEntry(player.getUuid()).getLatency() : -1;
            name.append(Text.literal(" [" + pingInt + "ms]").formatted(Formatting.GRAY));
        }


        float health = player.getHealth();
        Formatting healthColor;
        if (health > 18) {
            healthColor = Formatting.GREEN;
        } else if (health > 16) {
            healthColor = Formatting.DARK_GREEN;
        } else if (health > 12) {
            healthColor = Formatting.YELLOW;
        } else if (health > 8) {
            healthColor = Formatting.GOLD;
        } else if (health > 5) {
            healthColor = Formatting.RED;
        } else {
            healthColor = Formatting.DARK_RED;
        }

        if (this.health.getValue()) {
            float displayHealth = switch (healthLook.getValue()) {
                case TEN -> health / 2;
                case PERCENT -> health * 5;
                default -> health;
            };

            String healthText = (health > 0 ? (int) displayHealth + (healthLook.getValue() == HealthLook.PERCENT ? "%" : "") : "Dead");
            name.append(Text.literal(" " + healthText).formatted(healthColor));

            if (heart.getValue()) {
                name.append(Text.literal(" ❤").formatted(healthColor));
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
