package ac.paladin.auto.registry.profile;

import ac.paladin.auto.model.IDisposable;
import ac.paladin.auto.model.profile.IProfile;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface IProfileRegistry extends IDisposable {

    IProfile getProfile(Player player);

    Map<UUID, IProfile> getAllProfiles();

    void removeProfile(Player player);
}
