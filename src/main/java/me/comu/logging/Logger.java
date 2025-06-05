package me.comu.logging;

import me.comu.Comu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class Logger {

    private static Logger instance;

    public static Logger getLogger() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void print(String message) {
        Comu.LOGGER.info(message);
    }

    public void print(String message, LogType type) {
        switch (type) {
            case INFO -> Comu.LOGGER.info(message);
            case WARN -> Comu.LOGGER.warn(message);
            case ERROR -> Comu.LOGGER.error(message);
        }
    }

    public void printToChat(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                    Text.literal("[")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal(Comu.CLIENT_NAME).formatted(Formatting.RED))
                            .append(Text.literal("] ").formatted(Formatting.GRAY))
                            .append(Text.literal(message.replace("&", "§")).formatted(Formatting.GRAY)),
                    false
            );
        }
    }

    public enum LogType {
        INFO,
        WARN,
        ERROR
    }

}
