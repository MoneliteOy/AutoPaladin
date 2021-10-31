package ac.paladin.auto.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "m_")
public final class PluginConfig {

    @JsonProperty("api_key")
    private String m_apiKey;

    @JsonProperty("language")
    private String m_language;

    @JsonProperty("ban_command_on_logout")
    private String m_banCommandOnLogout;

    @JsonProperty("disable_commands_when_frozen")
    private boolean m_disableCommandsWhenFrozen;

    @JsonProperty("clear_inventory_when_frozen")
    private boolean m_clearInventoryWhenFrozen;
}
