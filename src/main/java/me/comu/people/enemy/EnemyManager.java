package me.comu.people.enemy;

import me.comu.people.PeopleManager;

import java.util.Collection;

public class EnemyManager extends PeopleManager<Enemy> {
    @Override
    protected Enemy createPerson(String name, String alias) {
        return new Enemy(name, alias);
    }

    public boolean isEnemy(String nameOrAlias) {
        return getEnemyByNameOrAlias(nameOrAlias) != null;
    }

    public Enemy getEnemyByNameOrAlias(String nameOrAlias) {
        for (Enemy enemy : getEnemies()) {
            if (enemy.getName().equalsIgnoreCase(nameOrAlias) || enemy.getAlias().equalsIgnoreCase(nameOrAlias)) {
                return enemy;
            }
        }
        return null;
    }

    public Collection<Enemy> getEnemies() {
        return getAll();
    }
}
