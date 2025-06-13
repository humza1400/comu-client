package me.comu.mixin.render;

import me.comu.account.gui.AccountManagerScreen;
import me.comu.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    protected MixinTitleScreen() {
        super(Text.empty());
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addNormalWidgets(II)I"))
    private void addAltManagerButton(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        int buttonWidth = 80;
        int buttonHeight = 18;

        int x = this.width / 2 - buttonWidth / 2;

        int y = this.height / 4 + 27;

        this.addDrawableChild(new AnimatedAltManagerButton(x, y, buttonWidth, buttonHeight, Text.of("Accounts"), button -> client.setScreen(new AccountManagerScreen())));
    }


    private static class AnimatedAltManagerButton extends ButtonWidget {

        private float animationTime = 0.0f;
        private float hoverProgress = 0.0f;
        private float clickProgress = 0.0f;
        private float shimmerOffset = 0.0f;
        private float pulseAnimation = 0.0f;
        private boolean wasClicked = false;
        private long lastClickTime = 0;

        public AnimatedAltManagerButton(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            MinecraftClient mc = MinecraftClient.getInstance();
            boolean hovered = isHovered();

            animationTime += delta * 0.05f;
            if (hovered || hoverProgress > 0.0f) {
                shimmerOffset += delta;
            }
            pulseAnimation += delta * 0.04f;

            float targetHover = hovered ? 1.0f : 0.0f;
            hoverProgress = MathHelper.lerp(delta * 0.15f, hoverProgress, targetHover);

            long currentTime = System.currentTimeMillis();
            if (wasClicked && currentTime - lastClickTime < 300) {
                clickProgress = Math.max(0, clickProgress - delta * 0.2f);
            } else {
                clickProgress = 0;
                wasClicked = false;
            }

            float scale = 1.0f + (hoverProgress * 0.05f) - (clickProgress * 0.03f);
            int scaledWidth = (int) (getWidth() * scale);
            int scaledHeight = (int) (getHeight() * scale);
            int offsetX = (getWidth() - scaledWidth) / 2;
            int offsetY = (getHeight() - scaledHeight) / 2;

            int buttonX = getX() + offsetX;
            int buttonY = getY() + offsetY;

            float wave1 = (float) Math.sin(animationTime) * 0.5f + 0.5f;
            float wave2 = (float) Math.sin(animationTime + Math.PI / 3) * 0.5f + 0.5f;
            float wave3 = (float) Math.sin(animationTime + Math.PI * 2 / 3) * 0.5f + 0.5f;

            int baseR = MathHelper.lerp(wave1, 45, 85);
            int baseG = MathHelper.lerp(wave2, 25, 65);
            int baseB = MathHelper.lerp(wave3, 85, 145);

            int hoverR = MathHelper.lerp(hoverProgress, baseR, Math.min(255, baseR + 40));
            int hoverG = MathHelper.lerp(hoverProgress, baseG, Math.min(255, baseG + 60));
            int hoverB = MathHelper.lerp(hoverProgress, baseB, Math.min(255, baseB + 40));

            RenderUtils.drawGradientRect(context, buttonX, buttonY, scaledWidth, scaledHeight, (255 << 24) | (hoverR << 16) | (hoverG << 8) | hoverB, (255 << 24) | ((hoverR - 20) << 16) | ((hoverG - 15) << 8) | (hoverB - 25));

            float pulseIntensity = (float) Math.sin(pulseAnimation) * 0.3f + 0.7f;
            int borderAlpha = (int) (255 * pulseIntensity);
            int borderR = Math.min(255, hoverR + 60);
            int borderG = Math.min(255, hoverG + 80);
            int borderB = Math.min(255, hoverB + 60);
            int borderColor = (borderAlpha << 24) | (borderR << 16) | (borderG << 8) | borderB;

            int borderThickness = hovered ? 2 : 1;
            for (int i = 0; i < borderThickness; i++) {
                RenderUtils.drawHollowRect(context, buttonX - i, buttonY - i, scaledWidth + i * 2, scaledHeight + i * 2, borderColor);
            }

            if (hoverProgress > 0.2f) {
                drawShimmerEffect(context, buttonX, buttonY, scaledWidth, scaledHeight, shimmerOffset, hoverProgress);
            }


            if (hoverProgress > 0.3f) {
                float t = (hoverProgress - 0.3f) / 0.7f;
                int alpha = (int) (t * 80);
                if (alpha > 0) {
                    int highlightColor = (alpha << 24) | 0xFFFFFF;
                    context.fill(buttonX + 2, buttonY + 2, buttonX + scaledWidth - 2, buttonY + 4, highlightColor);
                }
            }

            RenderUtils.drawEnhancedText(context, mc.textRenderer, getMessage(), buttonX + scaledWidth / 2, buttonY + (scaledHeight - 8) / 2, hoverProgress, animationTime);
        }


        private void drawShimmerEffect(DrawContext context, int x, int y, int width, int height, float offsetSeconds, float intensity) {
            int shimmerWidth = (int) (intensity * 30);
            float shimmerSpeed = 4f;
            float travelRange = width + shimmerWidth;

            float shimmerX = (offsetSeconds * shimmerSpeed) % travelRange;

            for (int i = 0; i < shimmerWidth; i++) {
                int drawX = x + (int) shimmerX + i - shimmerWidth;

                if (drawX < x || drawX >= x + width) continue;

                float distanceFromCenter = Math.abs(i - shimmerWidth / 2f) / (shimmerWidth / 2f);
                float gradient = 1.0f - distanceFromCenter;
                int shimmerAlpha = (int) (intensity * gradient * 120);

                int shimmerColor = (shimmerAlpha << 24) | 0xFFFFFF;
                context.fill(drawX, y + 2, drawX + 1, y + height - 2, shimmerColor);
            }
        }


        @Override
        public void onClick(double mouseX, double mouseY) {
            super.onClick(mouseX, mouseY);
            wasClicked = true;
            lastClickTime = System.currentTimeMillis();
            clickProgress = 1.0f;
        }
    }

}

