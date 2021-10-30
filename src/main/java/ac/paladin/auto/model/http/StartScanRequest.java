package ac.paladin.auto.model.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public final class StartScanRequest {

    @JsonProperty("sender")
    private final UUID i_uuid;
}
