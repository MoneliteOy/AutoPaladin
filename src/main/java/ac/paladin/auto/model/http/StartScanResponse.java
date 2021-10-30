package ac.paladin.auto.model.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(prefix = "m_")
public final class StartScanResponse {

    @JsonProperty("id")
    private String m_id;

    @JsonProperty("pin")
    private String m_pin;

    @JsonProperty("download")
    private String m_downloadLink;

    @JsonProperty("results")
    private String m_resultsLink;

    @JsonProperty("took")
    private String m_took;
}
