package ac.paladin.auto.registry.message;

import ac.paladin.auto.model.IDisposable;
import ac.paladin.auto.model.message.IMessageArguments;
import ac.paladin.auto.model.message.MessageArguments;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public interface IMessageRegistry extends IDisposable {

    /**
     * Save default language files and load all existing ones.
     */
    void load();

    /**
     * Gets the messages associated with the key for the language
     *
     * @param key  The key
     * @param lang The language
     * @return The messages
     */
    String[] getMessages(String key, String lang);

    /**
     * Replaces arguments in provided strings
     *
     * @param strings   The strings
     * @param arguments The args
     */
    String[] replaceArguments(String[] strings, MessageArguments arguments);

    /**
     * Sends a message associated with the key
     *
     * @param receiver The receiver
     * @param key      The key
     */
    void sendMessage(Player receiver, String key);

    /**
     * Sends a message associated with the key with custom arguments
     *
     * @param receiver The receiver
     * @param key      The key
     * @param args     The args
     */
    void sendMessage(Player receiver, String key, Consumer<IMessageArguments> args);

    /**
     * Sends a clickable message associated with the key with custom arguments
     *
     * @param receiver The receiver
     * @param key      The key
     * @param args     The args
     * @param clickUrl The url
     */
    void sendMessage(Player receiver, String key, Consumer<IMessageArguments> args, String clickUrl);
}
