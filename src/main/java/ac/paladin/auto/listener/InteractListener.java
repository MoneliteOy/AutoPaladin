package ac.paladin.auto.listener;

import ac.paladin.auto.registry.profile.IProfileRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class InteractListener implements Listener {

    private final IProfileRegistry i_profileRegistry;

    public InteractListener(IProfileRegistry i_profileRegistry) {
        this.i_profileRegistry = i_profileRegistry;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (i_profileRegistry.getProfile(event.getPlayer()).isFrozen()) {
            event.setCancelled(true);
        }
    }
}
