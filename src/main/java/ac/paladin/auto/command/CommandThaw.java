package ac.paladin.auto.command;

import ac.paladin.auto.PaladinPlugin;
import ac.paladin.auto.event.PlayerThawEvent;
import ac.paladin.auto.model.profile.Profile;
import ac.paladin.auto.registry.message.IMessageRegistry;
import ac.paladin.auto.registry.profile.IProfileRegistry;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("thaw|unfreeze|melt")
@CommandPermission("paladin.auto.freeze")
public final class CommandThaw extends BaseCommand {

    @Dependency
    private PaladinPlugin i_plugin;

    @Dependency
    private IMessageRegistry i_messageRegistry;

    @Dependency
    private IProfileRegistry i_profileRegistry;

    @Default
    @CommandCompletion("@players")
    @Syntax("<target>")
    public void execute(Player sender, OnlinePlayer onlinePlayer) {
        Player target = onlinePlayer.getPlayer();
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            i_messageRegistry.sendMessage(sender, "cannot_freeze_self");
            return;
        }

        Profile profile = (Profile) i_profileRegistry.getProfile(target);
        if (profile.isFrozen()) {
            i_plugin.getServer().getPluginManager().callEvent(new PlayerThawEvent(target));

            if (profile.getMessageTask() != null) {
                profile.getMessageTask().cancel();
            }

            profile.setFrozen(false);
            profile.restore(i_plugin, target);
            profile.setMessageTask(null);

            i_messageRegistry.sendMessage(sender, "sender.thaw", args -> args.setArgument("target", target.getName()));
            i_messageRegistry.sendMessage(target, "target.thaw", args -> args.setArgument("sender", sender.getName()));
        } else {
            i_messageRegistry.sendMessage(sender, "sender.not_frozen", args -> args.setArgument("target", target.getName()));
        }
    }
}