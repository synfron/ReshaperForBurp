package synfron.reshaper.burp.core.rules.thens.entities.transform;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EncoderType {
    Html("HTML"),
    Xml("XML"),
    Json("JSON"),
    Url("URL");

    private final String name;


    @Override
    public String toString() {
        return name;
    }
}
