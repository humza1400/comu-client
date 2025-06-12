package me.comu.people.friend;

import me.comu.people.PeopleManager;

import java.util.Collection;

public class FriendManager extends PeopleManager<Friend> {
    @Override
    protected Friend createPerson(String name, String alias) {
        return new Friend(name, alias);
    }

    public boolean isFriend(String nameOrAlias) {
        return getByNameOrAlias(nameOrAlias) != null;
    }

    public Friend getByNameOrAlias(String nameOrAlias) {
        for (Friend friend : getFriends()) {
            if (friend.getName().equalsIgnoreCase(nameOrAlias) || friend.getAlias().equalsIgnoreCase(nameOrAlias)) {
                return friend;
            }
        }
        return null;
    }

    public Collection<Friend> getFriends() {
        return getAll();
    }
}
