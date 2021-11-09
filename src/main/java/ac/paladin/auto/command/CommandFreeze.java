package ac.paladin.auto.command;

import ac.paladin.auto.PaladinPlugin;
import ac.paladin.auto.config.PluginConfig;
import ac.paladin.auto.event.PlayerFreezeEvent;
import ac.paladin.auto.manager.IScanManager;
import ac.paladin.auto.model.profile.Profile;
import ac.paladin.auto.registry.message.IMessageRegistry;
import ac.paladin.auto.registry.profile.IProfileRegistry;
import ac.paladin.auto.task.FrozenMessageTask;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CommandAlias("paladin|freeze")
@CommandPermission("paladin.auto.freeze")
public final class CommandFreeze extends BaseCommand {

    @Dependency
    private PaladinPlugin i_plugin;

    @Dependency
    private IMessageRegistry i_messageRegistry;

    @Dependency
    private IProfileRegistry i_profileRegistry;

    @Dependency
    private IScanManager i_scanManager;

    @Dependency
    private PluginConfig i_pluginConfig;

    @Default
    @CommandCompletion("@players")
    @Syntax("<target>")
    public void execute(Player sender, OnlinePlayer onlinePlayer) {
        Player target = onlinePlayer.getPlayer();
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            i_messageRegistry.sendMessage(sender, "sender.cannot_freeze_self");
            return;
        }

        Profile profile = (Profile) i_profileRegistry.getProfile(target);
        if (profile.isFrozen()) {
            i_messageRegistry.sendMessage(sender, "sender.already_frozen", args -> args.setArgument("target", target.getName()));
            return;
        }

        i_plugin.getServer().getPluginManager().callEvent(new PlayerFreezeEvent(target));

        profile.setFrozen(true);
        profile.setAdmin(sender.getUniqueId());
        profile.store(target);

        target.setFoodLevel(20);
        target.setGameMode(GameMode.ADVENTURE);
        target.setAllowFlight(false);
        target.setFlying(false);
        if (i_pluginConfig.isClearInventoryWhenFrozen()) {
            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
        }

        i_messageRegistry.sendMessage(sender, "sender.freeze", args -> args.setArgument("target", target.getName()));

        int currentSenderLevel = sender.getLevel();

        i_scanManager.createScan(sender, target, scan -> {
            FrozenMessageTask task = new FrozenMessageTask(i_messageRegistry, profile, target, sender, scan);
            task.runTaskTimerAsynchronously(i_plugin, 0, 200);

            profile.setMessageTask(task);

            scan.onStarted(() -> {
                task.cancel();
                profile.setMessageTask(null);
                i_messageRegistry.sendMessage(sender, "scan.started", args -> args.setArgument("target", target.getName()));
            });

            scan.onProgressUpdate(sender::setLevel);

            scan.onComplete(() -> {
                i_messageRegistry.sendMessage(sender, "scan.complete", args -> {
                    args.setArgument("target", target.getName());
                    args.setArgument("link", scan.getResultsLink());
                }, scan.getResultsLink());

                sender.setLevel(currentSenderLevel);

                profile.setCompletedScan(true);
            });

            scan.onAborted(() -> {
                task.cancel();
                profile.setMessageTask(null);
                profile.restore(i_plugin, target);
                i_messageRegistry.sendMessage(sender, "scan.abort", args -> args.setArgument("target", target.getName()));
            });
        });
    }
}