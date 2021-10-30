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
import ac.paladin.auto.model.profile.Profile;
import org.bukkit.entity.Player;

public final class ProfileFactory implements IProfileFactory {

    @Override
    public IProfile createProfile(Player player) {
        Profile profile = new Profile();
        profile.setUuid(player.getUniqueId());
        return profile;
    }
}
