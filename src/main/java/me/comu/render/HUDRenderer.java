package me.comu.render;

import me.comu.Comu;
import me.comu.logging.Logger;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.module.impl.render.HUD;
import me.comu.module.impl.render.TabGui;
import me.comu.module.impl.render.tabgui.comu.ComuTabGui;
import me.comu.utils.ClientUtils;
import me.comu.utils.ItemUtils;
import me.comu.utils.RenderUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;


public final class HUDRenderer {

    private static final Map<ToggleableModule, Float> moduleAnimationProgress = new HashMap<>();

    private static int indigoFadeState = 0;
    private static boolean indigoGoingUp = false;

    public static void init() {
        Logger.getLogger().print("Initializing HUD Renderer");
        HudRenderCallback.EVENT.register(HUDRenderer::onRender);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            HUD hud = (HUD) Comu.getInstance().getModuleManager().getModuleByName("HUD");
            if (hud != null && hud.isEnabled() && hud.getArrayListTheme().getValue() == HUD.ArrayListTheme.INDIGO) {
                updateIndigoFadeState();
            }
        });

    }

    private static final ComuTabGui renderer = new ComuTabGui();

    private static void onRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getDebugHud().shouldShowDebugHud()) return;

        HUD hud = (HUD) Comu.getInstance().getModuleManager().getModuleByName("HUD");
        TabGui tabGui = (TabGui) Comu.getInstance().getModuleManager().getModuleByName("TabGui");

        if (hud == null || !hud.isEnabled()) return;

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        if (hud.getWatermark().getValue()) {
            Renderer2D.drawText(context, Formatting.RED + Comu.getClientName() + Formatting.GRAY + " b" + Comu.getClientVersion(), 4, 4, 0xFFFFFFFF, true);
        }
        if (tabGui != null && tabGui.isEnabled()) {
            renderer.render(context, tabGui.getGuiState(), 3, hud.getWatermark().getValue() ? 15 : 4);
        }

        if (hud.getArrayList().getValue()) {
            List<ToggleableModule> modules = new ArrayList<>(Comu.getInstance().getModuleManager().getToggleableModules());

            HUD.ArrayListSort sortValue = hud.getArrayListSort().getValue();
            HUD.ArrayListCase caseValue = hud.getArrayListCase().getValue();
            HUD.ArrayListPosition positionValue = hud.getArrayListPosition().getValue();
            HUD.ArrayListAnimation animationValue = hud.getArrayListAnimation().getValue();
            HUD.ArrayListTheme theme = hud.getArrayListTheme().getValue();
            boolean shouldRenderSuffix = hud.shouldRenderSuffix().getValue();

            int baseX;
            int y = 4;
            switch (positionValue) {
                case TOPLEFT -> {
                    int padding = 4;
                    int topOffset = padding;
                    if (hud.getWatermark().getValue()) topOffset += 11;
                    if (tabGui != null && tabGui.isEnabled()) topOffset += 76;
                    y = topOffset;
                    baseX = padding;
                }
                case CROSSHAIR -> {
                    baseX = screenWidth / 2 + 30;
                    y = screenHeight / 2 - (modules.size() * (Renderer2D.getFontHeight() + 2)) / 2;
                }
                default -> baseX = screenWidth - 4;
            }

            if (animationValue != HUD.ArrayListAnimation.DEFAULT) {
                float fadeInSpeed = 0.05f;
                float fadeOutSpeed = 0.15f;

                for (ToggleableModule module : new ArrayList<>(moduleAnimationProgress.keySet())) {
                    updateModuleAnimation(module, fadeOutSpeed);
                }

                for (ToggleableModule module : Comu.getInstance().getModuleManager().getToggleableModules()) {
                    if (module.isEnabled()) {
                        moduleAnimationProgress.putIfAbsent(module, 0f);
                        updateModuleAnimation(module, fadeInSpeed);
                    }
                }
            } else {
                moduleAnimationProgress.clear();
            }

            Stream<ToggleableModule> stream = (animationValue == HUD.ArrayListAnimation.DEFAULT ? modules.stream().filter(ToggleableModule::isEnabled) : Comu.getInstance().getModuleManager().getToggleableModules().stream().filter(module -> {
                Float p = moduleAnimationProgress.get(module);
                return p != null && p > 0.01f;
            }));

            List<ToggleableModule> renderList = stream.sorted((a, b) -> switch (sortValue) {
                case LONGEST ->
                        Integer.compare(getEffectiveWidth(b, shouldRenderSuffix, caseValue, theme), getEffectiveWidth(a, shouldRenderSuffix, caseValue, theme));
                case SHORTEST ->
                        Integer.compare(getEffectiveWidth(a, shouldRenderSuffix, caseValue, theme), getEffectiveWidth(b, shouldRenderSuffix, caseValue, theme));
                case ABC -> {
                    String nameA = getStyledName(a, shouldRenderSuffix, caseValue, theme);
                    String nameB = getStyledName(b, shouldRenderSuffix, caseValue, theme);
                    yield nameA.compareToIgnoreCase(nameB);
                }
                case REVERSE_ABC -> {
                    String nameA = getStyledName(a, shouldRenderSuffix, caseValue, theme);
                    String nameB = getStyledName(b, shouldRenderSuffix, caseValue, theme);
                    yield nameB.compareToIgnoreCase(nameA);
                }
                case CATEGORY -> a.getCategory().getName().compareToIgnoreCase(b.getCategory().getName());
            }).toList();


            for (var module : renderList) {
                if (!module.isDrawn()) continue;
                float rawProgress = animationValue == HUD.ArrayListAnimation.DEFAULT ? 1f : moduleAnimationProgress.get(module);
                if (rawProgress <= 0.05f && !module.isEnabled()) continue;

                float progress = switch (animationValue) {
                    case SLIDE, FADE -> rawProgress;
                    case BOUNCE -> (float) Math.sin(rawProgress * Math.PI);
                    default -> 1f;
                };

                if (progress <= 0f) continue;

                String name = getStyledName(module, shouldRenderSuffix, caseValue, theme);

                int textWidth = Renderer2D.getStringWidth(name);
                int drawX = switch (positionValue) {
                    case TOPLEFT, CROSSHAIR -> baseX;
                    case TOPRIGHT -> baseX - textWidth;
                };

                int animatedX = drawX;
                int alpha = 0xFFFFFFFF;
                int drawY = y;
                switch (animationValue) {
                    case SLIDE -> {
                        int offscreenX = (positionValue == HUD.ArrayListPosition.TOPLEFT || positionValue == HUD.ArrayListPosition.CROSSHAIR) ? baseX - textWidth - 40 : screenWidth + 40;
                        animatedX = (int) (offscreenX + (drawX - offscreenX) * progress);
                    }
                    case FADE -> {
                        int alphaValue = (int) (255 * progress);
                        alpha = (alphaValue << 24) | 0xFFFFFF;
                    }
                    case BOUNCE -> {
                        drawY -= 20;
                        float bounceProgress = moduleAnimationProgress.get(module);
                        boolean isDisabling = rawProgress < 1f && !module.isEnabled();

                        float bounce = (float) Math.sin(bounceProgress * Math.PI);
                        if (isDisabling) bounce = 1f - bounce;

                        int fadeAlpha = getFadeAlpha(isDisabling, rawProgress);

                        alpha = (fadeAlpha << 24) | 0xFFFFFF;

                        float bounceHeight = 20;
                        drawY += Math.round((1f - bounce) * bounceHeight);
                    }
                }

                int color = switch (theme) {
                    case RAINBOW -> getRainbowColor(y, 0.002f, 0.8f, 1.0f, (alpha >> 24));
                    case COMU -> getComuColor(drawY, rawProgress);
                    case INDIGO -> getIndigoColor(progress);
                    case VIRTUE -> getVirtueColor(module.getCategory(), progress);
                    case GRAYSCALE -> (alpha & 0xFF000000) | 0x666666;
                    case WHITE -> (alpha & 0xFF000000) | 0xFFFFFF;
                    default -> (alpha & 0xFF000000) | 0xFFFFFF;
                };

                boolean withBackground = switch (theme) {
                    case MINECRAFT, COMU -> true;
                    default -> false;
                };

                if (withBackground) {
                    Renderer2D.drawTextWithBackground(context, name, animatedX, drawY, color, 0x66000000, true);
                } else {
                    Renderer2D.drawText(context, name, animatedX, drawY, color, true);
                }

                y += Renderer2D.getFontHeight() + 2;
            }


        }

        int yOffset = screenHeight - 2;
        if (hud.getPotions().getValue()) yOffset = drawPotions(context, screenWidth, yOffset);
        if (hud.getCoords().getValue()) yOffset = drawCoords(context, screenWidth, yOffset);
        if (hud.getClock().getValue()) yOffset = drawClock(context, screenWidth, yOffset);
        if (hud.getFps().getValue()) yOffset = drawFPS(context, screenWidth, yOffset);
        if (hud.getPing().getValue()) yOffset = drawPing(context, screenWidth, yOffset);
        if (hud.getDirection().getValue()) drawDirection(context, screenWidth, yOffset);

        if (hud.getGapple().getValue()) drawGappled(context, screenWidth, screenHeight);
        if (hud.getArmor().getValue()) drawArmor(context, screenWidth, screenHeight);
    }

    private static int drawPotions(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        for (var effect : mc.player.getStatusEffects()) {
            var type = effect.getEffectType();
            var color = type.value().getColor();
            String name = type.value().getName().getString();
            int level = effect.getAmplifier() + 1;
            int durationTicks = effect.getDuration();
            int durationSeconds = durationTicks / 20;
            String durationStr = String.format("%d:%02d", durationSeconds / 60, durationSeconds % 60);
            String display = name + " " + level + " " + Formatting.GRAY + durationStr;

            int textWidth = Renderer2D.getStringWidth(display);
            yOffset -= Renderer2D.getFontHeight();
            Renderer2D.drawText(context, display, screenWidth - textWidth - 4, yOffset, 0xFF000000 | color, true);
            yOffset -= 1;
        }
        return yOffset;
    }

    private static int drawCoords(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String coords = String.format(Formatting.WHITE + "%d, %d, %d" + Formatting.GRAY + " XYZ", (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
        int textWidth = Renderer2D.getStringWidth(coords);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, coords, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
        return yOffset - 1;
    }

    private static int drawClock(DrawContext context, int screenWidth, int yOffset) {
        String clock = Formatting.GRAY + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"));
        int textWidth = Renderer2D.getStringWidth(clock);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, clock, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
        return yOffset - 1;
    }

    private static int drawFPS(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String fpsStr = Formatting.GRAY + "" + mc.getCurrentFps() + " FPS";
        int textWidth = Renderer2D.getStringWidth(fpsStr);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, fpsStr, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
        return yOffset - 1;
    }

    private static int drawPing(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null ? mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency() : -1;
        String pingStr = Formatting.GRAY + "" + ping + "ms";
        int textWidth = Renderer2D.getStringWidth(pingStr);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, pingStr, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
        return yOffset - 1;
    }

    private static void drawDirection(DrawContext context, int screenWidth, int yOffset) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String[] directions = {"South", "West", "North", "East"};
        int facing = Math.round(mc.player.getYaw() / 90f) & 3;
        String direction = Formatting.GRAY + directions[facing];
        int textWidth = Renderer2D.getStringWidth(direction);
        yOffset -= Renderer2D.getFontHeight();
        Renderer2D.drawText(context, direction, screenWidth - textWidth - 4, yOffset, 0xFFFFFFFF, true);
    }

    private static void drawGappled(DrawContext context, int screenWidth, int screenHeight) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        boolean isGappled = false;
        int durationSeconds = 0;

        for (var effect : mc.player.getStatusEffects()) {
            if (effect.getEffectType() == StatusEffects.REGENERATION) {
                isGappled = true;
                durationSeconds = effect.getDuration() / 20;
                break;
            }
        }

        String text = isGappled ? Formatting.GREEN + "Gappled " + Formatting.GRAY + "(" + durationSeconds + ")" : Formatting.RED.toString() + Formatting.UNDERLINE + "NOT Gappled";

        int textWidth = Renderer2D.getStringWidth(text);

        int x = screenWidth / 2 + textWidth - 60;
        int y = screenHeight / 2 + 12;

        Renderer2D.drawText(context, text, x, y, 0xFFFFFFFF, true);
    }

    private static void drawArmor(DrawContext context, int screenWidth, int screenHeight) {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<ItemStack> items = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            ItemStack stack = ItemUtils.getEquipmentItem(mc.player, i);
            if (!stack.isEmpty()) items.add(stack);
        }

        if (items.isEmpty()) return;

        final int iconSize = 16;
        final int iconSpacing = 18;

        int startX = screenWidth / 2 + 93;
        int yOffset = screenHeight - 18;

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            int x = startX + i * iconSpacing;

            RenderUtils.drawItem(context, stack, x, yOffset, 1f, true);

            if (stack.isDamageable()) {
                context.getMatrices().push();

                float scaleFactor = 0.7f;

                int durability = stack.getMaxDamage() - stack.getDamage();
                String durabilityText = String.valueOf(durability);
                int textWidth = mc.textRenderer.getWidth(durabilityText);
                int textHeight = mc.textRenderer.fontHeight;

                float unscaledX = x + iconSize - textWidth * scaleFactor - 1;
                float unscaledY = yOffset + iconSize - textHeight * scaleFactor - 8.5f;

                context.getMatrices().translate(unscaledX, unscaledY, 9999);
                context.getMatrices().scale(scaleFactor, scaleFactor, 1f);
                context.drawText(mc.textRenderer, durabilityText, 0, 0, 0xFFAA00, true);
                context.getMatrices().pop();
            }
        }
    }

    private static void updateModuleAnimation(ToggleableModule module, float speed) {
        float current = moduleAnimationProgress.getOrDefault(module, module.isEnabled() ? 0f : 1f);
        float target = module.isEnabled() ? 1f : 0f;
        float updated = current + (target - current) * speed;

        if (!module.isEnabled() && updated < 0.01f) {
            moduleAnimationProgress.remove(module);
            return;
        }

        moduleAnimationProgress.put(module, updated);
    }

    private static int getFadeAlpha(boolean isDisabling, Float rawProgress) {
        int fadeAlpha;
        if (isDisabling) {
            float smoothFade = (float) Math.pow(rawProgress, 0.5);
            fadeAlpha = Math.max(0, Math.min(255, (int) (255 * smoothFade)));
        } else {
            fadeAlpha = Math.max(0, Math.min(255, (int) (255 * rawProgress)));
        }

        if (fadeAlpha < 10 && rawProgress > 0.01f) {
            fadeAlpha = 10;
        }
        return fadeAlpha;
    }

    private static int getEffectiveWidth(ToggleableModule module, boolean shouldRenderSuffix, HUD.ArrayListCase caseValue, HUD.ArrayListTheme theme) {
        String styledName = getStyledName(module, shouldRenderSuffix, caseValue, theme);
        return Renderer2D.getStringWidth(styledName);
    }


    private static String getStyledName(ToggleableModule module, boolean shouldRenderSuffix, HUD.ArrayListCase caseValue, HUD.ArrayListTheme theme) {
        String displayName = module.getDisplayName();
        String suffix = shouldRenderSuffix && module.getSuffix() != null ? module.getSuffix() : null;

        String name;
        switch (caseValue) {
            case CUB -> name = "[" + displayName + "]";
            case PAREN -> name = "(" + displayName + ")";
            case DASH -> name = "- " + displayName + " -";
            case STAR -> name = "*" + displayName + "*";
            default -> name = displayName;
        }

        if (suffix != null) {
            if (theme == HUD.ArrayListTheme.VIRTUE) {
                name += Formatting.GRAY + " [" + suffix + "]";
            } else name += Formatting.GRAY + " " + suffix;
        }

        return switch (caseValue) {
            case LOWER -> name.toLowerCase();
            case UPPER -> name.toUpperCase();
            default -> name;
        };
    }

    private static int getRainbowColor(int yOffset, float speed, float saturation, float brightness, int alpha) {
        float hue = (System.currentTimeMillis() % 6000L) / 6000f + yOffset * speed;
        hue %= 1.0f;
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    private static int getComuColor(int drawY, float progress) {
        int alpha = Math.max(10, Math.min(255, (int) (255 * progress)));

        float time = (System.currentTimeMillis() % 3000L) / 3000f;
        float waveOffset = (float) Math.sin(drawY * 0.04 + time * Math.PI * 2);
        float pulseOffset = (float) Math.sin(time * Math.PI * 2 * 2);

        float hue = ((drawY * 0.002f + time + waveOffset * 0.1f) % 1.0f);
        float saturation = 0.85f + 0.15f * pulseOffset;
        float brightness = 0.85f + 0.15f * pulseOffset;

        int rgb = Color.HSBtoRGB(hue, saturation, brightness) & 0x00FFFFFF;
        return (alpha << 24) | rgb;
    }

    public static int getIndigoColor(float progress) {
        double ratio = indigoFadeState / 25.0;
        int fadeHex = getFadeHex(-23614, -3394561, ratio);
        int alpha = Math.max(10, Math.min(255, (int) (255 * progress)));

        return (alpha << 24) | (fadeHex & 0x00FFFFFF);
    }

    public static void updateIndigoFadeState() {
        if (indigoFadeState >= 25 || indigoFadeState <= 0) indigoGoingUp = !indigoGoingUp;

        indigoFadeState += indigoGoingUp ? 1 : -1;
    }

    private static int getFadeHex(int hex1, int hex2, double ratio) {
        int r1 = (hex1 >> 16) & 0xFF, g1 = (hex1 >> 8) & 0xFF, b1 = hex1 & 0xFF;
        int r2 = (hex2 >> 16) & 0xFF, g2 = (hex2 >> 8) & 0xFF, b2 = hex2 & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }

    public static int getVirtueColor(Category category, float progress) {
        int alpha = Math.max(10, Math.min(255, (int)(255 * progress)));
        int baseColor;

        switch (category) {
            case COMBAT -> baseColor = 0xFFFA5551;
            case EXPLOITS -> baseColor = 0x5CCEFF;
            case MOVEMENT -> baseColor = 0x77A7F7;
            case MISCELLANEOUS -> baseColor = 0xB4FFAC;
            case RENDER -> baseColor = 0xFFFFFFFF;
            case WORLD -> baseColor = 0xF7A72E;
            default -> baseColor = 0xCCCCCC;
        }

        return (alpha << 24) | (baseColor & 0x00FFFFFF);
    }

}
