package me.comu.module.impl.render;

import me.comu.api.registry.event.listener.Listener;
import me.comu.events.Render3DEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.property.properties.BooleanProperty;
import me.comu.property.properties.EnumProperty;
import me.comu.utils.ItemUtils;
import me.comu.utils.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.List;

public class ItemESP extends ToggleableModule {

    private final BooleanProperty nametags = new BooleanProperty("Nametags", List.of("tags"), true);
    private final BooleanProperty lines = new BooleanProperty("Lines", List.of("line"), true);
    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.EVERYTHING);

    private enum Mode {
        EVERYTHING, PURPLE, OMEGAS
    }

    public ItemESP() {
        super("ItemESP", List.of("iesp", "espitem", "itemsesp", "espitems"), Category.RENDER, "Highlights items on the ground");
        offerProperties(nametags, lines, mode);
        listeners.add(new Listener<>(Render3DEvent.class) {
            @Override
            public void call(Render3DEvent event) {
                if (isPlayerOrWorldNull()) return;
                Vec3d cam = new Vec3d(event.getCameraX(), event.getCameraY(), event.getCameraZ());
                Vec3d center = cam.add(0, 0, 3);
                Box testBox = new Box(center.x - 0.5, center.y - 0.5, center.z - 0.5, center.x + 0.5, center.y + 0.5, center.z + 0.5);
                RenderUtils.drawBox(testBox.offset(-cam.x, -cam.y, -cam.z), 0xAA00FFAA);

                MatrixStack matrices = event.getMatrixStack();
                VertexConsumerProvider.Immediate consumers = event.getConsumers();
                VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
                Matrix4f mat = matrices.peek().getPositionMatrix();

                for (Entity entity : mc.world.getEntities()) {
                    if (!(entity instanceof ItemEntity itemEntity)) continue;

                    ItemStack stack = itemEntity.getStack();
                    if (stack == null || stack.isEmpty()) continue;

                    boolean shouldRender = switch (mode.getValue()) {
                        case EVERYTHING -> true;
                        case PURPLE ->
                                ItemUtils.isPurpleGodSword(stack) || ItemUtils.isPunch3OrBetter(stack) || ItemUtils.isOmega(stack);
                        case OMEGAS -> ItemUtils.isOmega(stack);
                    };

                    if (!shouldRender) continue;


                    // interpolate position
                    double ix = MathHelper.lerp(event.getTickDelta(), entity.lastX, entity.getX());
                    double iy = MathHelper.lerp(event.getTickDelta(), entity.lastY, entity.getY());
                    double iz = MathHelper.lerp(event.getTickDelta(), entity.lastZ, entity.getZ());

                    Box box = entity.getBoundingBox().offset(ix - entity.getX(), iy - entity.getY(), iz - entity.getZ());

                    // green
                    drawBoxOutline(buffer, mat, box, 1f, 1f, 0f, 0.67f);
                }

                // flush all draws
                consumers.draw();
            }
        });
    }

    private static void drawBoxOutline(VertexConsumer buf, Matrix4f mat, Box b, float r, float g, float b_, float a) {
        // bottom
        drawLine(buf, mat, b.minX, b.minY, b.minZ, b.maxX, b.minY, b.minZ, r, g, b_, a);
        drawLine(buf, mat, b.maxX, b.minY, b.minZ, b.maxX, b.minY, b.maxZ, r, g, b_, a);
        drawLine(buf, mat, b.maxX, b.minY, b.maxZ, b.minX, b.minY, b.maxZ, r, g, b_, a);
        drawLine(buf, mat, b.minX, b.minY, b.maxZ, b.minX, b.minY, b.minZ, r, g, b_, a);
        // top
        drawLine(buf, mat, b.minX, b.maxY, b.minZ, b.maxX, b.maxY, b.minZ, r, g, b_, a);
        drawLine(buf, mat, b.maxX, b.maxY, b.minZ, b.maxX, b.maxY, b.maxZ, r, g, b_, a);
        drawLine(buf, mat, b.maxX, b.maxY, b.maxZ, b.minX, b.maxY, b.maxZ, r, g, b_, a);
        drawLine(buf, mat, b.minX, b.maxY, b.maxZ, b.minX, b.maxY, b.minZ, r, g, b_, a);
        // verticals
        drawLine(buf, mat, b.minX, b.minY, b.minZ, b.minX, b.maxY, b.minZ, r, g, b_, a);
        drawLine(buf, mat, b.maxX, b.minY, b.minZ, b.maxX, b.maxY, b.minZ, r, g, b_, a);
        drawLine(buf, mat, b.maxX, b.minY, b.maxZ, b.maxX, b.maxY, b.maxZ, r, g, b_, a);
        drawLine(buf, mat, b.minX, b.minY, b.maxZ, b.minX, b.maxY, b.maxZ, r, g, b_, a);
    }

    private static void drawLine(VertexConsumer buf, Matrix4f mat, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
        buf.vertex(mat, (float) x1, (float) y1, (float) z1).color(r, g, b, a).normal(0, 1, 0);
        buf.vertex(mat, (float) x2, (float) y2, (float) z2).color(r, g, b, a).normal(0, 1, 0);
    }
}