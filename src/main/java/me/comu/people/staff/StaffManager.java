package me.comu.people.staff;

import me.comu.people.PeopleManager;

import java.util.Collection;

public class StaffManager extends PeopleManager<Staff> {
    @Override
    protected Staff createPerson(String name, String alias) {
        return new Staff(name, alias);
    }

    public boolean isStaff(String nameOrAlias) {
        return getStaffByNameOrAlias(nameOrAlias) != null;
    }

    public Staff getStaffByNameOrAlias(String nameOrAlias) {
        for (Staff staff : getStaff()) {
            if (staff.getName().equalsIgnoreCase(nameOrAlias) || staff.getAlias().equalsIgnoreCase(nameOrAlias)) {
                return staff;
            }
        }
        return null;
    }

    public Collection<Staff> getStaff() {
        return getAll();
    }
}
