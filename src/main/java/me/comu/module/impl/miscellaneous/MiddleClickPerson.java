package me.comu.module.impl.miscellaneous;

import me.comu.Comu;
import me.comu.api.registry.event.listener.Listener;
import me.comu.events.InputEvent;
import me.comu.events.TickEvent;
import me.comu.module.Category;
import me.comu.module.ToggleableModule;
import me.comu.notification.Notification;
import me.comu.notification.NotificationType;
import me.comu.property.properties.EnumProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MiddleClickPerson extends ToggleableModule {

    EnumProperty<Mode> mode = new EnumProperty<>("Mode", List.of("m"), Mode.FRIEND);

    public enum Mode {
        FRIEND, ENEMY, STAFF
    }

    public MiddleClickPerson() {
        super("Middle Click", List.of("mcf", "midclick", "middleclick", "middleclickfriends", "middleclickfriend", "midclickfriend", "middleclickfriends", "mcp"), Category.MISCELLANEOUS, "Middle click your mouse on an entity to add them as a person");
        offerProperties(mode);
        listeners.add(new Listener<>(InputEvent.class) {
            @Override
            public void call(InputEvent event) {
                if (event.getType() == InputEvent.Type.MOUSE_MIDDLE_CLICK) {
                    if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                        Entity entity = ((EntityHitResult) mc.crosshairTarget).getEntity();
                        if (entity instanceof PlayerEntity player) {
                            String name = player.getName().getString();
                            Mode selectedMode = mode.getValue();

                            switch (selectedMode) {
                                case FRIEND -> {
                                    var manager = Comu.getInstance().getFriendManager();
                                    if (manager.isFriend(name)) {
                                        manager.remove(name);
                                        Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.NEGATIVE, "Friend Removed", "Removed Friend \247e" + name, 3));
                                    } else {
                                        manager.add(name, name);
                                        Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.POSITIVE, "Friend Added", "Added Friend \247e" + name, 3));
                                    }
                                }
                                case ENEMY -> {
                                    var manager = Comu.getInstance().getEnemyManager();
                                    if (manager.isEnemy(name)) {
                                        manager.remove(name);
                                        Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.NEGATIVE, "Enemy Removed", "Removed Enemy \247e" + name, 3));
                                    } else {
                                        manager.add(name, name);
                                        Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.POSITIVE, "Enemy Added", "Added Enemy \247e" + name, 3));
                                    }
                                }
                                case STAFF -> {
                                    var manager = Comu.getInstance().getStaffManager();
                                    if (manager.isStaff(name)) {
                                        manager.remove(name);
                                        Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.NEGATIVE, "Staff Removed", "Removed Staff \247e" + name, 3));
                                    } else {
                                        manager.add(name, name);
                                        Comu.getInstance().getNotificationManager().notify(new Notification(NotificationType.POSITIVE, "Staff Added", "Added Staff \247e" + name, 3));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public String getSuffix() {
        return mode.getFormattedValue();
    }
}
