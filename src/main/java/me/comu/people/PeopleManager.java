package me.comu.people;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PeopleManager<T extends Person> {
    protected final Map<String, T> people = new ConcurrentHashMap<>();

    public void add(String name, String alias) {
        people.put(name.toLowerCase(), createPerson(name, alias));
    }

    public boolean contains(String nameOrAlias) {
        for (Person person : people.values()) {
            if (person.getName().equalsIgnoreCase(nameOrAlias) || person.getAlias().equalsIgnoreCase(nameOrAlias)) {
                return true;
            }
        }
        return false;
    }

    public void remove(String nameOrAlias) {
        Person person = getByNameOrAlias(nameOrAlias);
        if (person != null) {
            people.remove(person.getName().toLowerCase());
        }
    }

    public Person getByNameOrAlias(String nameOrAlias) {
        for (Person person : getAll()) {
            if (person.getName().equalsIgnoreCase(nameOrAlias) || person.getAlias().equalsIgnoreCase(nameOrAlias)) {
                return person;
            }
        }
        return null;
    }


    public Collection<T> getAll() {
        return people.values();
    }

    protected abstract T createPerson(String name, String alias);
}
