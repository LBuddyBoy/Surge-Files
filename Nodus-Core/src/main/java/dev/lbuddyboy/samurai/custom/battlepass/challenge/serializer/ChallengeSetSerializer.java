package dev.lbuddyboy.samurai.custom.battlepass.challenge.serializer;

import com.google.gson.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class ChallengeSetSerializer implements JsonSerializer<Set<Challenge>>, JsonDeserializer<Set<Challenge>> {

    @Override
    public JsonElement serialize(Set<Challenge> challenges, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray array = new JsonArray();

        for (Challenge challenge : challenges) {
            JsonObject object = new JsonObject();
            object.addProperty("id", challenge.getId());
            object.addProperty("daily", challenge.isDaily());
            array.add(object);
        }

        return array;
    }

    @Override
    public Set<Challenge> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Set<Challenge> challenges = new HashSet<>();

        for (JsonElement element : jsonElement.getAsJsonArray()) {
            final JsonObject jsonObject = element.getAsJsonObject();
            final String challengeId = jsonObject.get("id").getAsString();

            final Challenge challenge;
            if (jsonObject.get("daily").getAsBoolean()) {
                challenge = Samurai.getInstance().getBattlePassHandler().getDailyChallenges().getChallenge(challengeId);
            } else {
                challenge = Samurai.getInstance().getBattlePassHandler().getChallenge(challengeId);
            }

            if (challenge != null) {
                challenges.add(challenge);
            }
        }

        return challenges;
    }

}