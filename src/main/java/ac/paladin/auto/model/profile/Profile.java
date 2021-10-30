/*
 *  Copyright (c) 2021, Paladin.ac
 *
 *  All rights reserved.
 *
 *  Author(s):
 *   Marshall Walker
 */

package ac.paladin.auto.model.profile;

import ac.paladin.auto.PaladinPlugin;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@Data
@Accessors(prefix = "m_")
public final class Profile implements IProfile {

    private UUID m_uuid;

    private boolean m_frozen;

    private boolean m_completedScan;

    private long m_scanCompleteTimestamp;

    private long m_frozenTimestamp;

    private UUID m_admin;

    private GameMode m_gameMode;

    private boolean m_allowFlight;

    private boolean m_flying;

    private int m_foodLevel;

    private BukkitRunnable m_messageTask;

    @Override
    public void store(Player player) {
        this.m_gameMode = player.getGameMode();
        this.m_allowFlight = player.getAllowFlight();
        this.m_flying = player.isFlying();
        this.m_foodLevel = player.getFoodLevel();
    }

    @Override
    public void restore(PaladinPlugin plugin, Player player) {
        // This has to be run on the main thread
        // or spigot will scream
        if (!Bukkit.isPrimaryThread()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> restore(plugin, player));
            return;
        }

        player.setGameMode(m_gameMode);
        player.setAllowFlight(m_allowFlight);
        player.setFlying(m_flying);
        player.setFoodLevel(m_foodLevel);
    }

    @Override
    public void setFrozen(boolean frozen) {
        m_frozen = frozen;
        m_frozenTimestamp = frozen ? System.currentTimeMillis() : -1;

        if (!frozen) {
            m_admin = null;
        }
    }

    @Override
    public void setCompletedScan(boolean completedScan) {
        m_completedScan = completedScan;
        if (completedScan) {
            m_scanCompleteTimestamp = System.currentTimeMillis();
        }
    }
}
