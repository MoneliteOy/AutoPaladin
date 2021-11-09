package ac.paladin.auto.command;

import ac.paladin.auto.PaladinPlugin;
import ac.paladin.auto.manager.IScanManager;
import ac.paladin.auto.registry.message.IMessageRegistry;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("paladin")
@CommandPermission("paladin.auto.reload")
public final class CommandReload extends BaseCommand {

    @Dependency
    private PaladinPlugin i_plugin;

    @Dependency
    private IMessageRegistry i_messageRegistry;

    @Dependency
    private IScanManager i_scanManager;

    @Subcommand("reload")
    public void reload(CommandSender sender) {
        i_plugin.loadPluginConfig();
        i_messageRegistry.load();
        if (!i_scanManager.isConnected()) {
            i_scanManager.connect();
            sender.sendMessage(ChatColor.GREEN + "Connecting to websocket!");
        }

        sender.sendMessage(ChatColor.GREEN + "Configuration has been reloaded!");
    }
}