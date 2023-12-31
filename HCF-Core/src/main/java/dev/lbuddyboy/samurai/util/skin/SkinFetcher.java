package dev.lbuddyboy.samurai.util.skin;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class SkinFetcher extends Reflection {

    private final Player player;

    /**
     * Gets the players skin
     * @return Skin Values
     */
    public Map.Entry<String, String> getSkin() {
        Object craftPlayerHandle = callMethod(player, "getHandle");
        Object gameProfile = callMethod(craftPlayerHandle, "getProfile");
        Object propertyMap = callMethod(gameProfile, "getProperties");

        Collection<?> textures = (Collection<?>) callMethod(propertyMap, "get", "textures");
        Object property = textures.iterator().next();

        return new AbstractMap.SimpleEntry<>((String) callMethod(property, "getValue"), (String) callMethod(property, "getSignature"));
    }

}
