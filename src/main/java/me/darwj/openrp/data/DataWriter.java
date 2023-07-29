package me.darwj.openrp.data;

import java.util.UUID;

public interface DataWriter {

    void set(Object key, Object value);
    void unset(Object key);

    Object get(Object key);

    Object get(Object key, Object resultIfNull);

    void save();
    void load();

}
