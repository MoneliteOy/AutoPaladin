/*
 *  Copyright (c) 2021, Paladin.ac
 *
 *  All rights reserved.
 *
 *  Author(s):
 *   Marshall Walker
 */

package ac.paladin.auto.factory.profile;

import ac.paladin.auto.model.profile.IProfile;
import org.bukkit.entity.Player;

public interface IProfileFactory {

    /**
     * Create a new profile.
     *
     * @return new profile
     */
    IProfile createProfile(Player player);
}
