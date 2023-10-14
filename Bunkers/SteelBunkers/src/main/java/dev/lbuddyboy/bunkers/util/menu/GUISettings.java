package dev.lbuddyboy.bunkers.util.menu;

import dev.lbuddyboy.bunkers.util.bukkit.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Data
public class GUISettings {

    private boolean autoFill;
    private ItemStack autoFillItem;
    private String title;
    private int size;

    public GUISettings(FileConfiguration config) {
        this.title = config.getString("menu-settings.title");
        this.size = config.getInt("menu-settings.size");
        this.autoFill = config.getBoolean("menu-settings.auto-fill.enabled");
        this.autoFillItem = ItemUtils.itemStackFromConfigSect("menu-settings.auto-fill.display-item", config);
    }

    public void save(FileConfiguration config) {
        config.set("menu-settings.title", this.title);
        config.set("menu-settings.size", this.size);
        config.set("menu-settings.auto-fill.enabled", this.autoFill);
        ItemUtils.itemStackToConfigSect(this.autoFillItem, -1, "menu-settings.auto-fill.display-item", config);
    }

}
