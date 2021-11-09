package ac.paladin.auto.registry.message;

import ac.paladin.auto.config.PluginConfig;
import ac.paladin.auto.model.message.IMessageArguments;
import ac.paladin.auto.model.message.MessageArguments;
import ac.paladin.auto.util.ReflectionUtil;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class MessageRegistry implements IMessageRegistry {

    private final Plugin i_plugin;

    private final PluginConfig i_pluginConfig;

    // language -> (key -> message)
    private final Map<String, Map<String, String[]>> m_messages = new HashMap<>();

    @Override
    public void load() {
        File dataFolder = i_plugin.getDataFolder();
        File languages = new File(dataFolder, "languages");

        m_messages.clear();

        try {
            i_plugin.saveResource("languages/en_us.yml", false);
            i_plugin.saveResource("languages/lol_aa.yml", false);

            // Get all yml files in the languages path
            File[] ymlFiles = languages.listFiles((dir, name) -> name.endsWith(".yml"));
            if (ymlFiles == null) {
                throw new RuntimeException("No language files found.");
            }

            for (File languageFile : ymlFiles) {
                try {
                    // Read the file content in UTF-8
                    StringBuilder content = new StringBuilder();
                    FileInputStream fileInputStream = new FileInputStream(languageFile);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        content.append(str);
                        content.append("\n");
                    }
                    bufferedReader.close();

                    // Load a bukkit YamlConfig from the content
                    YamlConfiguration configuration = new YamlConfiguration();
                    configuration.loadFromString(content.toString());

                    String fileName = languageFile.getName();
                    m_messages.put(fileName.substring(0, fileName.indexOf(".")), loadMessages(configuration));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String[]> loadMessages(ConfigurationSection section) {
        Map<String, String[]> map = new HashMap<>();
        for (String key : section.getKeys(true)) {
            if (section.isConfigurationSection(key)) {
                map.putAll(loadMessages(Objects.requireNonNull(section.getConfigurationSection(key))));
            } else if (section.isString(key)) {
                map.put(key, new String[]{section.getString(key)});
            } else if (section.isList(key)) {
                map.put(key, section.getStringList(key).toArray(new String[0]));
            }
        }
        return map;
    }

    private String getLang(CommandSender sender) {
        if (sender instanceof Player) {
            return ReflectionUtil.getLocale((Player) sender);
        }

        return i_pluginConfig.getLanguage();
    }

    private String[] getMessagesAndReplace(CommandSender receiver, String key, Consumer<IMessageArguments> args) {
        MessageArguments arguments = new MessageArguments();
        args.accept(arguments);

        String[] messages = getMessages(key, getLang(receiver));
        replaceArguments(messages, arguments);

        return messages;
    }

    @Override
    public String[] getMessages(String key, String lang) {
        Map<String, String[]> map = m_messages.get(lang);
        if (map == null) {
            // Get default server language
            map = m_messages.get(i_pluginConfig.getLanguage());
        }

        String[] messages = map.getOrDefault(key, new String[]{String.format("key.not.found (%s)", key)}).clone();
        for (int i = 0; i < messages.length; i++) {
            messages[i] = map.get("prefix")[0] + messages[i];
        }

        return messages;
    }

    @Override
    public String[] replaceArguments(String[] strings, MessageArguments arguments) {
        for (Entry<String, Object> entry : arguments.getArguments().entrySet()) {
            for (int i = 0; i < strings.length; i++) {
                String message = strings[i];

                // Replace placeholders
                message = message.replaceAll(String.format("\\{%s\\}", entry.getKey()), entry.getValue().toString());
                message = ChatColor.translateAlternateColorCodes('&', message);

                strings[i] = message;
            }
        }

        return strings;
    }

    @Override
    public void sendMessage(Player receiver, String key) {
        String[] messages = getMessages(key, getLang(receiver));
        for (String message : messages) {
            receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    @Override
    public void sendMessage(Player receiver, String key, Consumer<IMessageArguments> args) {
        String[] messages = getMessagesAndReplace(receiver, key, args);
        for (String message : messages) {
            receiver.sendMessage(message);
        }
    }

    @Override
    public void sendMessage(Player receiver, String key, Consumer<IMessageArguments> args, String clickUrl) {
        String[] messages = getMessagesAndReplace(receiver, key, args);

        for (String message : messages) {
            BaseComponent[] components = TextComponent.fromLegacyText(message);
            for (BaseComponent component : components) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, clickUrl));
            }

            receiver.spigot().sendMessage(components);
        }
    }

    @Override
    public void dispose() {
        m_messages.clear();
    }
}
