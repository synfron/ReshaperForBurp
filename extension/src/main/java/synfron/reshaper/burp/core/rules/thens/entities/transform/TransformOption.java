package synfron.reshaper.burp.core.rules.thens.entities.transform;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
public enum TransformOption {
    Base64,
    Html("HTML"),
    Xml("XML"),
    Json("JSON"),
    Url("URL"),
    Case,
    Hash,
    Hex,
    Integer,
    Trim;

    private String name;

    public String getName() {
        return StringUtils.defaultString(name, name());
    }

    @Override
    public String toString() {
        return getName();
    }
}
