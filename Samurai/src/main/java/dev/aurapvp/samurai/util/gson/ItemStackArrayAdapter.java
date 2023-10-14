package dev.aurapvp.samurai.util.gson;

import com.google.gson.*;
import dev.aurapvp.samurai.util.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Objects;

public class ItemStackArrayAdapter implements JsonDeserializer<ItemStack[]>, JsonSerializer<ItemStack[]> {

    @Override
    public ItemStack[] deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }

    @Override
    public JsonElement serialize(ItemStack[] src, Type type, JsonSerializationContext context) {
        return toJson(src);
    }

    public static JsonObject toJson(ItemStack[] src) {
        if (src == null) {
            return null;
        }

        JsonObject object = new JsonObject();


        object.addProperty("item", ItemUtils.itemStackArrayToBase64(src));

        return object;
    }

    public static ItemStack[] fromJson(JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        JsonObject json = src.getAsJsonObject();

        return Objects.requireNonNull(ItemUtils.itemStackArrayFromBase64(json.get("item").getAsString()));
    }

}

