//package dev.lbuddyboy.samurai.team.configuration;
//
//import lombok.Getter;
//import dev.lbuddyboy.samurai.Foxtrot;
//import dev.lbuddyboy.samurai.util.qlib.configuration.Configuration;
//import dev.lbuddyboy.samurai.util.qlib.configuration.annotations.ConfigData;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TeamGeneralConfiguration extends Configuration {
//
//    @ConfigData(path = "teams.disallowed_names")
//    @Getter
//    private static List<String> disallowedNames = new ArrayList<>();
//
//    public TeamGeneralConfiguration() {
//        super(Foxtrot.getInstance(), "config.yml", "./plugins/Teams/");
//        disallowedNames.add("glowstone");
//        disallowedNames.add("miniend");
//        disallowedNames.add("warzone");
//        disallowedNames.add("spawn");
//        disallowedNames.add("end");
//        disallowedNames.add("theend");
//        disallowedNames.add("citadel");
//        load();
//        save();
//    }
//}
