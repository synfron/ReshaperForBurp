package synfron.reshaper.burp.core.messages;

import lombok.Getter;

public enum MessageValueType {
    Text("Text"),
    Json("JSON"),
    Html("HTML");

    @Getter
    private final String name;

    MessageValueType(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
