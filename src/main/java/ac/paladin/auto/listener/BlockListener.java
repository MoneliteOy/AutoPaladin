/*
 *  Copyright (c) 2021, Paladin.ac
 *
 *  All rights reserved.
 *
 *  Author(s):
 *   Marshall Walker
 */

package ac.paladin.auto.listener;

import ac.paladin.auto.registry.profile.IProfileRegistry;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@RequiredArgsConstructor
public final class BlockListener implements Listener {

    private final IProfileRegistry i_profileRegistry;

    private boolean isPlayerFrozen(Player player) {
        return i_profileRegistry.getProfile(player).isFrozen();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isPlayerFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isPlayerFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}