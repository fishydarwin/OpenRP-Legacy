package me.darwj.openrp.domain;

import me.darwj.openrp.OpenRP;
import me.darwj.openrp.data.DataWriter;
import me.darwj.openrp.data.YAMLDataWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OpenRPPlayer {

    private final UUID uniqueId;
    private String selectedChatChannel;
    private final Map<String, String> characterFields;

    /**
     * Initializes a new OpenRP Player with the attached unique ID.
     * @param uniqueId the UUID of the Player for whom this player is made
     */
    public OpenRPPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        selectedChatChannel = null;
        characterFields = new HashMap<>();

        load();
    }

    private DataWriter playerDataWriter;

    /**
     * Loads this OpenRP Player
     */
    public void load() {
        //TODO: change to MySQL also
        try {
            File dataWriterFile = new File(OpenRP.getInstance().getDataFolder().getPath()
                    + File.separator + "player_data" + File.separator + uniqueId + ".yml");
            playerDataWriter = new YAMLDataWriter(dataWriterFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        selectedChatChannel = (String) playerDataWriter.get("selected-chat-channel");
        // TODO: character info
    }

    /**
     * Saves this OpenRP Player.
     */
    public void save() {
        playerDataWriter.set("selected-chat-channel", selectedChatChannel);
        for (String field : characterFields.keySet())
            playerDataWriter.set("character." + field, characterFields.get(field));
        playerDataWriter.save();
    }

    /**
     * Returns the unique ID of the player whom this object belongs to
     * @return the unique ID of the owning player
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * Returns the selected chat channel as a string
     * @return the selected chat channel
     */
    public String getSelectedChatChannel() {
        return selectedChatChannel;
    }

    /**
     * Sets the selected chat channel of this Player
     * @param selectedChatChannel the new chat channel
     */
    public void setSelectedChatChannel(String selectedChatChannel) {
        this.selectedChatChannel = selectedChatChannel;
    }

    /**
     * Sets a player's character field to a certain value.
     * @param field The field you want to change
     * @param value The value to change the field to
     */
    public void setCharacterField(String field, String value) {
        characterFields.put(field, value);
    }

    /**
     * Unsets a player's character field.
     * This means that calling getCharacterField() afterwards will return null.
     * @param field The field to unset
     */
    public void unsetCharacterField(String field) {
        characterFields.remove(field);
    }

    /**
     * Returns whether a certain field contains a value.
     * This is used when checking if to show or not show the default value.
     * @param field The field to check
     * @return True if set, false otherwise
     */
    public boolean hasCharacterField(String field) {
        return characterFields.containsKey(field);
    }

    /**
     * Returns the value of a certain field, or null otherwise.
     * @param field The field to get the value of
     * @return The value, or null
     */
    public String getCharacterField(String field) {
        return characterFields.getOrDefault(field, null);
    }

    /**
     * Returns the value of a certain field, or resultIfNull if that field is unset.
     * @param field The field to get the value of
     * @param resultIfNull The return value in case the field is null
     * @return The value, or resultIfNull
     */
    public String getCharacterField(String field, String resultIfNull) {
        return characterFields.getOrDefault(field, resultIfNull);
    }

}
