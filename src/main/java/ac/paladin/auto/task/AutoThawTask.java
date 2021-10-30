package ac.paladin.auto.task;

import ac.paladin.auto.PaladinPlugin;
import ac.paladin.auto.model.profile.IProfile;
import ac.paladin.auto.registry.message.IMessageRegistry;
import ac.paladin.auto.registry.profile.IProfileRegistry;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public final class AutoThawTask implements Runnable {

    private static final long s_fiveSecondsMillis = 500000;

    private final PaladinPlugin i_plugin;

    private final IProfileRegistry i_profileRegistry;

    private final IMessageRegistry i_messageRegistry;

    @Override
    public void run() {
        for (Map.Entry<UUID, IProfile> entry : i_profileRegistry.getAllProfiles().entrySet()) {
            UUID uuid = entry.getKey();
            IProfile profile = entry.getValue();

            if (profile.isCompletedScan() && profile.isFrozen() && System.currentTimeMillis() >= profile.getScanCompleteTimestamp() + s_fiveSecondsMillis) {
                Player targetPlayer = Bukkit.getPlayer(uuid);
                Player adminPlayer = Bukkit.getPlayer(profile.getAdmin());
                if (targetPlayer == null) {
                    return;
                }

                profile.setFrozen(false);
                profile.restore(i_plugin, targetPlayer);

                if (adminPlayer != null) {
                    i_messageRegistry.sendMessage(adminPlayer, "autothaw.admin", args -> args.setArgument("target", targetPlayer.getName()));
                }

                i_messageRegistry.sendMessage(targetPlayer, "autothaw.target");
            }
        }
    }
}
