package dev.lbuddyboy.samurai.team.track;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public final class TeamActionTracker {

    @Getter @Setter private static boolean databaseLogEnabled = true;
    private static final File logFileRoot = new File(new File("foxlogs"), "teamactiontracker");

    public static void logActionAsync(Team team, TeamActionType actionType, Map<String, Object> params) {
        // do nothing bc we dontt use this shiz anymore

    }

    private static void logActionToFile(Team team, TeamActionType actionType, Map<String, Object> params) {
        File teamLogFolder = new File(logFileRoot, team.getName());
        File teamLogFile = new File(teamLogFolder, (actionType.isLoggedToDatabase() ? "general" : "misc") + ".log");

        try {
            StringBuilder logLine = new StringBuilder();

            logLine.append('[');
            logLine.append(DateTimeFormatter.ISO_INSTANT.format(Instant.now())); // ISO 8601
            logLine.append(", ");
            logLine.append(actionType.getInternalName());
            logLine.append("] ");

            params.forEach((key, value) -> {
                logLine.append(key);
                logLine.append(": ");
                logLine.append(value);
                logLine.append(' ');
            });

            logLine.append('\n');

            teamLogFile.getParentFile().mkdirs();
            teamLogFile.createNewFile();

            Files.append(
                logLine.toString(),
                teamLogFile,
                Charsets.UTF_8
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void logActionToDatabase(Team team, TeamActionType actionType, Map<String, Object> params) {
        BasicDBObject entry = new BasicDBObject();

        entry.put("teamId", team.getUniqueId().toString());
        entry.put("teamName", team.getName());
        entry.put("time", new Date());
        entry.put("type", actionType.getInternalName());

        BasicDBObject paramsJson = new BasicDBObject();

        // we manually serialize this so we use .toString
        // instead of Mongo's serialization (ex UUID -> binary)
        params.forEach((key, value) -> {
            paramsJson.put(key, value.toString());
        });

        entry.put("params", paramsJson);

        // we embed the entire team json here :(
        // this is bad and uses a lot of data, but
        // the web dev team (Ariel) will have to do
        // less work if we embed it.
        entry.put("teamAfterAction", team.toJSON());

        DB db = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME);
        db.getCollection("TeamActions").insert(entry);
    }

}