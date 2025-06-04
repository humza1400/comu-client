package me.comu;

import me.comu.api.registry.event.EventManager;
import me.comu.api.registry.event.IEventManager;
import me.comu.module.Module;
import me.comu.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Comu implements ClientModInitializer {

	public static final String CLIENT_NAME = "comu";
	public static final Logger LOGGER = LoggerFactory.getLogger(CLIENT_NAME);

	private static Comu instance;
	private long startTime;

	private IEventManager eventManager;
	private ModuleManager moduleManager;

	@Override
	public void onInitializeClient() {
		instance = this;
		startTime = System.nanoTime() / 1000000L;
		this.eventManager = new EventManager();
		this.moduleManager = new ModuleManager();
		LOGGER.info(CLIENT_NAME + " initialized in {} ms", (System.nanoTime() / 1_000_000L) - startTime);
	}

	public static Comu getInstance() {
		return instance;
	}

	public IEventManager getEventManager() {
		return eventManager;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public Logger getLogger() {
		return LOGGER;
	}
}