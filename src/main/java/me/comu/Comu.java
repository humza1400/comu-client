package me.comu;

import me.comu.account.MicrosoftLogin;
import me.comu.api.registry.event.EventManager;
import me.comu.api.registry.event.IEventManager;
import me.comu.command.CommandManager;
import me.comu.config.ConfigManager;
import me.comu.hooks.Hook;
import me.comu.keybind.KeybindManager;
import me.comu.module.ModuleManager;
import me.comu.notification.NotificationManager;
import me.comu.people.enemy.EnemyManager;
import me.comu.people.friend.FriendManager;
import me.comu.people.staff.StaffManager;
import me.comu.render.HUDRenderer;
import me.comu.rotation.RotationManager;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Comu implements ClientModInitializer {

    private static final String CLIENT_NAME = "comu";
    private static final int CLIENT_VERSION = 5;
    public static final Logger LOGGER = LoggerFactory.getLogger(CLIENT_NAME);

    private static Comu instance;

    private IEventManager eventManager;
    private KeybindManager keybindManager;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private RotationManager rotationManager;
    private NotificationManager notificationManager;
    private FriendManager friendManager;
    private EnemyManager enemyManager;
    private StaffManager staffManager;

    @Override
    public void onInitializeClient() {
        instance = this;
        long startTime = System.nanoTime() / 1000000L;

        File configRoot = new File(FabricLoader.getInstance().getGameDir().toFile(), "comu");
        this.configManager = new ConfigManager(configRoot);
        this.eventManager = new EventManager();
        this.keybindManager = new KeybindManager();
        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.rotationManager = new RotationManager();
        this.notificationManager = new NotificationManager();
        this.friendManager = new FriendManager();
        this.enemyManager = new EnemyManager();
        this.staffManager = new StaffManager();
        this.configManager.loadAll();

        // This will be removed soon for our own login api since it's a shitty library.
//        MicrosoftLogin.setUserMicrosoft("", "");
        Hook.init();
        HUDRenderer.init();
        LOGGER.info(CLIENT_NAME + " initialized in {} ms", (System.nanoTime() / 1_000_000L) - startTime);

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Comu.getInstance().getConfigManager().saveAll());
    }

    public static String getClientName() {
        return CLIENT_NAME;
    }

    public static int getClientVersion() {
        return CLIENT_VERSION;
    }

    public static Comu getInstance() {
        return instance;
    }

    public IEventManager getEventManager() {
        return eventManager;
    }

    public KeybindManager getKeybindManager() {
        return keybindManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RotationManager getRotationManager() {
        return rotationManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public StaffManager getStaffManager() {
        return staffManager;
    }
}