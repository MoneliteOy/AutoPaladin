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
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IProfile {

    UUID getUuid();

    boolean isFrozen();

    void setFrozen(boolean frozen);

    boolean isCompletedScan();

    void setCompletedScan(boolean completedScan);

    /**
     * @return The timestamp (ms) of when this target completed a scan or -1 if they haven't
     */
    long getScanCompleteTimestamp();

    /**
     * @return The timestamp (ms) of when this target was frozen or -1 if they aren't frozen
     */
    long getFrozenTimestamp();

    /**
     * @return The UUID of the player who froze this target
     */
    UUID getAdmin();

    void setAdmin(UUID uuid);

    /**
     * Store player settings.
     *
     * @param player target
     */
    void store(Player player);

    /**
     * Restore player settings.
     *
     * @param player target
     */
    void restore(PaladinPlugin plugin, Player player);
}
