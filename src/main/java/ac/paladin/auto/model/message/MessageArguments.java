package ac.paladin.auto.model.message;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Accessors(prefix = "m_")
public final class MessageArguments implements IMessageArguments {

    private final Map<String, Object> m_arguments = new HashMap<>();

    @Override
    public void setArgument(String key, Object value) {
        m_arguments.put(key, value);
    }
}
