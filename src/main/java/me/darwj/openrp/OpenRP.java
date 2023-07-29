package me.darwj.openrp;

import org.bukkit.plugin.java.JavaPlugin;

public final class OpenRP extends JavaPlugin {

    private static OpenRP instance;
    public static OpenRP getInstance() { return instance; }

    @Override
    public void onEnable() {
        instance = this;

        // File timeConfigFile = new File(OpenRP.getInstance.getDataFolder(), "time_config.yml");
        // timeConfigDataWriter = new YAMLDataWriter(timeConfigFile);
        // [...]
        // int secondsInMinute = (int) timeConfigDataWriter.get("date-time.seconds-in-minute");

    }

    @Override
    public void onDisable() {

    }
}
