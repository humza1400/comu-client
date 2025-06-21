package me.comu.account;

import me.comu.Comu;
import me.comu.api.registry.Registry;
import me.comu.config.configs.AccountsConfig;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountManager extends Registry<Account> {

    private final AccountsConfig config;

    public AccountManager() {
        // backing list
        registry = new CopyOnWriteArrayList<>();
        // load from disk
        this.config = Comu.getInstance().getConfigManager().getConfig(AccountsConfig.class);
    }

    public void addAccount(Account a) {
        register(a);
        config.save();
    }

    public void removeAccount(Account a) {
        unregister(a);
        config.save();
    }

    public void markLastUsed(Account a) {
        a.setLastUsed(System.currentTimeMillis());
        config.save();
    }

    public List<Account> getAccounts() {
        return List.copyOf(registry);
    }

    public void reload() {
        config.load();
    }
}
