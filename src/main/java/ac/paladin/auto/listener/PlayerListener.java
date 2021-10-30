/*
 *  Copyright (c) 2021, Paladin.ac
 *
 *  All rights reserved.
 *
 *  Author(s):
 *   Marshall Walker
 */

package ac.paladin.auto.listener;

import ac.paladin.auto.PaladinPlugin;
import ac.paladin.auto.config.PluginConfig;
import ac.paladin.auto.event.PlayerAbortScanEvent;
import ac.paladin.auto.manager.IScanManager;
import ac.paladin.auto.model.profile.IProfile;
import ac.paladin.auto.registry.profile.IProfileRegistry;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.event.EventPriority.MONITOR;

@RequiredArgsConstructor
public final class PlayerListener implements Listener {

    private final IProfileRegistry i_profileRegistry;

    private final IScanManager i_scanManager;

    private final PaladinPlugin i_plugin;

    private final PluginConfig i_pluginConfig;

    private boolean movedOneBlock(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() ||
                from.getBlockY() < to.getBlockY() ||
                from.getBlockZ() != to.getBlockZ();
    }

    private boolean isPlayerFrozen(Player player) {
        return i_profileRegistry.getProfile(player).isFrozen();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (isPlayerFrozen(player)) {
            i_scanManager.abortScan(player);
            IProfile profile = i_profileRegistry.getProfile(event.getPlayer());
            profile.restore(i_plugin, event.getPlayer());
        }

        i_profileRegistry.removeProfile(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (!movedOneBlock(from, to)) {
            return;
        }

        Player player = event.getPlayer();

        if (!isPlayerFrozen(player)) {
            return;
        }

        // This will allow the player to smoothly look around
        Location clone = from.clone();
        clone.setYaw(to.getYaw());
        clone.setPitch(to.getPitch());
        event.setTo(clone);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (isPlayerFrozen(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) {
            return;
        }

        if (isPlayerFrozen((Player) attacker)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity victim = event.getEntity();

        if (!(victim instanceof Player)) {
            return;
        }

        if (isPlayerFrozen((Player) victim)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (i_pluginConfig.isDisableCommandsWhenFrozen() && isPlayerFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isPlayerFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    /*
     * default behavior for a scan abort
     */
    @EventHandler(priority = MONITOR, ignoreCancelled = true)
    public void onPlayerAbortScan(PlayerAbortScanEvent event) {
        String cmd = i_pluginConfig.getBanCommandOnLogout().replaceAll("\\{target}", event.getPlayer().getName());
        Bukkit.dispatchCommand(i_plugin.getServer().getConsoleSender(), cmd);
    }
}