package ac.paladin.auto.listener;

import ac.paladin.auto.registry.profile.IProfileRegistry;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

@AllArgsConstructor
public final class PickupListener implements Listener {

    private final IProfileRegistry i_profileRegistry;

    private boolean isPlayerFrozen(Player player) {
        return i_profileRegistry.getProfile(player).isFrozen();
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player && isPlayerFrozen((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}