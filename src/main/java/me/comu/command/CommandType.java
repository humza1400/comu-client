package me.comu.command;

public enum CommandType {
    CLIENT("Client"), NETWORK("Network"), PLAYER("Player"), SERVER("Server");

    private final String name;

    CommandType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
