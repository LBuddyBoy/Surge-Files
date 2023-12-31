package dev.aurapvp.samurai.api.impl.dynmap;

import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;

public class IconStorage {
    private final MarkerAPI markerApi;
    private final Set<MarkerIcon> iconSet;
    private final JavaPlugin plugin;
    private MarkerIcon defaultIcon;

    /**
     * Creates an unmodified storage for {@link MarkerIcon}.
     * <pre>
     *  Firstly, it checks on working path validity and throws the error.
     *
     *  After it creates a default icon.
     *  It took the default icon from plugin resources
     *  if can't find it in the icons set by any reason.
     *
     *  Finally, it adds all icons from the working directory to icon set.
     * </pre>
     *
     * @param plugin          The plugin instance
     * @param workingPath     The path of working directory, where icons stored.
     * @param defaultIconName The icon name, located inside working directory.
     *                        Used if working directory doesn't contain any icons.
     * @param markerAPI       {@link MarkerAPI}
     * @throws IllegalArgumentException if workingPath is invalid.
     */
    public IconStorage(JavaPlugin plugin, String workingPath,
                       String defaultIconName, MarkerAPI markerAPI) throws IllegalArgumentException {
        this.plugin = plugin;
        if (!isValidPath(workingPath)) {
            throw new IllegalArgumentException(String.format("Provided workingPath ( %s ) is invalid!", workingPath));
        }
        markerApi = markerAPI;
        defaultIcon = markerAPI.getMarkerIcon(defaultIconName);
        if (defaultIcon == null) {
            defaultIcon = markerAPI.createMarkerIcon(defaultIconName, defaultIconName,
                    plugin.getClass().getResourceAsStream(workingPath + "/" + defaultIconName + ".png"));
        }
        File workingDir = new File(plugin.getDataFolder(), workingPath);
        iconSet = getIconsIn(workingDir);
    }

    /**
     * Checks if a string is a valid path.
     * Null safe.
     *
     * <pre>
     * Examples:
     *    isValidPath("c:/test");      // returns true
     *    isValidPath("c:/te:t");      // returns false
     *    isValidPath("c:/te?t");      // returns false
     *    isValidPath("c/te*t");       // returns false
     *    isValidPath("good.txt");     // returns true
     *    isValidPath("not|good.txt"); // returns false
     *    isValidPath("not:good.txt"); // returns false
     * </pre>
     */
    private static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException ex) {
            return false;
        }
        return true;
    }

    /**
     * @return {@link MarkerIcon} from the working directory or the default one
     */
    public MarkerIcon getIcon(String iconName) {
        Optional<MarkerIcon> icon = iconSet.stream().
                filter(markerIcon -> Objects.equals(markerIcon.getMarkerIconLabel(), iconName)).
                findAny();

        return icon.orElse(defaultIcon);
    }

    public String getDefaultIconName() {
        return defaultIcon.getMarkerIconID();
    }

    /**
     * Checks if the icon is contained in IconStorage
     *
     * @param iconName the name of icon
     * @return true if contains, otherwise false
     */
    public boolean has(String iconName) {
        return iconSet.stream().map(MarkerIcon::getMarkerIconLabel).anyMatch(iconName::equals);
    }

    /**
     * @return Retrieves all icons from IconStorage
     */
    public Set<MarkerIcon> getIcons() {
        return iconSet;
    }

    /**
     * Retrieves all {@link MarkerIcon} in defined folder
     *
     * <p>
     * Uses {@link File#listFiles(FileFilter)} method to retrieve
     * all .png images from the folder.
     * </p>
     */
    private Set<MarkerIcon> getIconsIn(File iconsFolder) {
        File[] files = iconsFolder.listFiles(file -> file.getName().contains(".png"));

        if (files == null) {
            return new HashSet<>();
        }

        HashSet<MarkerIcon> icons = new HashSet<>();
        for (File icon : files) {
            String name = icon.getName();
            String nameWithoutExt = name.substring(0, name.lastIndexOf(".")).toLowerCase();
            try (FileInputStream stream = new FileInputStream(icon)) {
                MarkerIcon markerIcon = markerApi.createMarkerIcon(nameWithoutExt, nameWithoutExt, stream);
                if (markerIcon == null) {
                    icons.add(markerApi.getMarkerIcon(nameWithoutExt));
                } else {
                    icons.add(markerIcon);
                }
            } catch (IOException ex) {
                plugin.getLogger().severe(
                        String.format("Error occurred while trying to create %s icon: %s", name, ex.getMessage()));
            }
        }

        return Collections.unmodifiableSet(icons);
    }

    public enum DefaultIcons {
        FACTIONHOME("factionhome", "images/factionhome/factionhome.png"),
        BLOOD("blood", "images/blood.png");

        private final String name;
        private final String path;

        DefaultIcons(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }
    }
}