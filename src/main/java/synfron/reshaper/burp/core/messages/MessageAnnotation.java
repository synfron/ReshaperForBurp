package synfron.reshaper.burp.core.messages;

import lombok.Getter;

public enum MessageAnnotation {
    Comment("Comment"),
    HighlightColor("Highlight Color");

    @Getter
    private final String name;

    MessageAnnotation(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
