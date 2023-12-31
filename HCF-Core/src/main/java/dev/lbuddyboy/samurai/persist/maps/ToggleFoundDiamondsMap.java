package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class ToggleFoundDiamondsMap extends PersistMap<Boolean> {

    public ToggleFoundDiamondsMap() {
        super("FoundDiamondToggles", "FoundDiamondEnabled");
    }

    @Override
    public String getRedisValue(Boolean toggled){
        return (String.valueOf(toggled));
    }

    @Override
    public Boolean getJavaObject(String str){
        return (Boolean.valueOf(str));
    }

    @Override
    public Object getMongoValue(Boolean toggled) {
        return (toggled);
    }

    public void setFoundDiamondToggled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isFoundDiamondToggled(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}