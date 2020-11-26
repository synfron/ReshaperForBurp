package synfron.reshaper.burp.core.rules;

import lombok.Getter;

public enum MatchType {
    Equals("Equals"),
    Contains("Contains"),
    BeginsWith("Begins With"),
    EndsWith("Ends With"),
    Regex("Regex");

    @Getter
    private final String name;

    MatchType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
