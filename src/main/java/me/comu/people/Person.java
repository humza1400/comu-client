package me.comu.people;

public class Person {
    private final String name;
    private final String alias;

    public Person(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias == null ? name : alias;
    }
}