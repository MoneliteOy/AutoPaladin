package ac.paladin.auto.registry.profile;

import ac.paladin.auto.factory.profile.IProfileFactory;
import ac.paladin.auto.model.profile.IProfile;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class ProfileRegistry implements IProfileRegistry {

    private final IProfileFactory i_playerFactory;

    private final Map<UUID, IProfile> m_profiles = new HashMap<>();

    @Override
    public IProfile getProfile(Player player) {
        UUID uniqueId = player.getUniqueId();
        return m_profiles.computeIfAbsent(uniqueId, u -> i_playerFactory.createProfile(player));
    }

    @Override
    public Map<UUID, IProfile> getAllProfiles() {
        return m_profiles;
    }

    @Override
    public void removeProfile(Player player) {
        m_profiles.remove(player.getUniqueId());
    }

    @Override
    public void dispose() {
        m_profiles.clear();
    }
}
