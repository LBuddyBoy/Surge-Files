package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class TeamBundleMap extends PersistMap<Boolean> {

    public TeamBundleMap() {
        super("BundleClaimed", "BundleClaimed");
    }

    @Override
    public String getRedisValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Object getMongoValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Boolean getJavaObject(String str) {
        return Boolean.valueOf(str);
    }

    public void setActive(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isActive(UUID check) {
        return (contains(check) ? getValue(check) : false);
    }

}
