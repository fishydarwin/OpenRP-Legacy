package me.darwj.openrp.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class YAMLDataWriter implements DataWriter {

    private final File yamlFile;
    private YamlConfiguration yamlConfig;

    public YAMLDataWriter(@NotNull File yamlFile) throws IOException {
        this.yamlFile = yamlFile;
        if (!this.yamlFile.exists()) {
            if (!this.yamlFile.getParentFile().exists()) {
                if (!this.yamlFile.mkdirs())
                    throw new RuntimeException("Could not make " +
                            this.yamlFile.getParentFile().getPath() + " directory");
                if (!this.yamlFile.createNewFile())
                    throw new RuntimeException("This file already exists! " + this.yamlFile.getPath());
            }
        }
        yamlConfig = YamlConfiguration.loadConfiguration(this.yamlFile);
    }

    @Override
    public void set(Object key, Object value) {
        if (key instanceof String) {
            String keyString = (String) key;
            yamlConfig.set(keyString, value);
        } else {
            throw new RuntimeException("Cannot use non-string keys in YAML!");
        }
    }

    @Override
    public void unset(Object key) {
        if (key instanceof String) {
            String keyString = (String) key;
            yamlConfig.set(keyString, null);
        } else {
            throw new RuntimeException("Cannot use non-string keys in YAML!");
        }
    }

    @Override
    public Object get(Object key) {
        if (key instanceof String) {
            String keyString = (String) key;
            return yamlConfig.get(keyString);
        } else {
            throw new RuntimeException("Cannot use non-string keys in YAML!");
        }
    }

    @Override
    public Object get(Object key, Object resultIfNull) {
        if (key instanceof String) {
            String keyString = (String) key;
            return yamlConfig.get(keyString, resultIfNull);
        } else {
            throw new RuntimeException("Cannot use non-string keys in YAML!");
        }
    }

    @Override
    public void save() {
        try {
            yamlConfig.save(yamlFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load() {
        yamlConfig = YamlConfiguration.loadConfiguration(yamlFile);
    }
}
