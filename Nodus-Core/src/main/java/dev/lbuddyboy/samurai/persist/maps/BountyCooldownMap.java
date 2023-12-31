package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.Date;
import java.util.UUID;

public class BountyCooldownMap extends PersistMap<Long> {

    public BountyCooldownMap() {
        super("BountyCooldown", "BountyCooldown");
    }

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    @Override
    public Object getMongoValue(Long time) {
        return (new Date(time));
    }

    public boolean isOnCooldown(UUID check) {
        return (getValue(check) != null && getValue(check) > System.currentTimeMillis());
    }

    public void applyCooldown(UUID update, long seconds) {
        updateValueAsync(update, System.currentTimeMillis() + (seconds * 1000));
    }

    public void resetCooldown(UUID update) {
        updateValueAsync(update, 0L);
    }

    public long getCooldown(UUID check) {
        return (contains(check) ? getValue(check) : -1L);
    }

}