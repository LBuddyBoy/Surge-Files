package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class IronMinedMap extends PersistMap<Integer> {

    public IronMinedMap() {
        super("IronMined", "MiningStats.Iron");
    }

    @Override
    public String getRedisValue(Integer mined) {
        return (String.valueOf(mined));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer mined) {
        return (mined);
    }

    public int getMined(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setMined(UUID update, int mined) {
        updateValueAsync(update, mined);
    }

}