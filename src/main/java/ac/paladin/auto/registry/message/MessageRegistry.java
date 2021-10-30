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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class MessageRegistry implements IMessageRegistry {

    private static final String[] s_languages = {
            "en_us",
            "lol_aa"
    };

    private final Plugin i_plugin;

    private final PluginConfig i_pluginConfig;

    private final Map<String, YamlConfiguration> m_messages = new HashMap<>();

    @Override
    public void load() {
        File dataFolder = i_plugin.getDataFolder();
        File languages = new File(dataFolder, "languages");

        m_messages.clear();

        try {
            Files.createDirectories(languages.toPath());

            // Copy default locales
            for (String language : s_languages) {
                File languageFile = new File(languages, language + ".yml");
                if (languageFile.exists()) {
                    continue;
                }

                InputStream stream = i_plugin.getResource("languages/" + language + ".yml");
                assert stream != null;

                Files.copy(stream, languageFile.toPath());
                stream.close();
            }

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
                    m_messages.put(fileName.substring(0, fileName.indexOf(".")), configuration);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLang(CommandSender sender) {
        if (sender instanceof Player) {
            return ReflectionUtil.getLocale((Player) sender);
        }

        return i_pluginConfig.getLanguage();
    }

    private List<String> getMessagesAndReplace(CommandSender receiver, String key, Consumer<IMessageArguments> args) {
        MessageArguments arguments = new MessageArguments();
        args.accept(arguments);

        List<String> messages = getMessages(key, getLang(receiver));
        replaceArguments(messages, arguments);

        return messages;
    }

    @Override
    public List<String> getMessages(String key, String lang) {
        YamlConfiguration config = m_messages.get(lang);
        if (config == null) {
            // Get default server language
            config = m_messages.get(i_pluginConfig.getLanguage());
        }

        if (config.isString(key)) {
            // Make a new arraylist here because if we used a singleton list we would have to copy it regardless
            List<String> list = new ArrayList<>();
            list.add(config.getString("prefix") + config.getString(key));
            return list;
        } else if (config.isList(key)) {
            List<String> messages = config.getStringList(key);
            for (int i = 0; i < messages.size(); i++) {
                messages.set(i, config.get("prefix") + messages.get(i));
            }

            return messages;
        }

        return Collections.singletonList(String.format("key.not.found (%s)", key));
    }

    @Override
    public void replaceArguments(List<String> strings, MessageArguments arguments) {
        for (Entry<String, Object> entry : arguments.getArguments().entrySet()) {
            for (int i = 0; i < strings.size(); i++) {
                String message = strings.get(i);

                // Replace placeholders
                message = message.replaceAll(String.format("\\{%s\\}", entry.getKey()), entry.getValue().toString());
                message = ChatColor.translateAlternateColorCodes('&', message);

                strings.set(i, message);
            }
        }
    }

    @Override
    public void sendMessage(Player receiver, String key) {
        List<String> messages = getMessages(key, getLang(receiver));
        for (String message : messages) {
            receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    @Override
    public void sendMessage(Player receiver, String key, Consumer<IMessageArguments> args) {
        List<String> messages = getMessagesAndReplace(receiver, key, args);
        for (String message : messages) {
            receiver.sendMessage(message);
        }
    }

    @Override
    public void sendMessage(Player receiver, String key, Consumer<IMessageArguments> args, String clickUrl) {
        List<String> messages = getMessagesAndReplace(receiver, key, args);

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
