package ac.paladin.auto.task;

import ac.paladin.auto.model.profile.Profile;
import ac.paladin.auto.model.scan.IScan;
import ac.paladin.auto.registry.message.IMessageRegistry;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public final class FrozenMessageTask extends BukkitRunnable {

    private final IMessageRegistry i_messageRegistry;

    private final Profile i_target;

    private final Player i_targetPlayer;

    private final Player i_senderPlayer;

    private final IScan i_scan;

    @Override
    public void run() {
        if (!i_target.isFrozen() || !i_targetPlayer.isOnline()) {
            cancel();
            return;
        }

        i_messageRegistry.sendMessage(i_targetPlayer, "target.freeze", args -> {
            args.setArgument("sender", i_senderPlayer.getName());
            args.setArgument("link", i_scan.getLink());
            args.setArgument("pin", i_scan.getId());
        });
    }
}
