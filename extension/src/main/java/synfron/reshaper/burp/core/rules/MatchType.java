package synfron.reshaper.burp.core.rules;

import lombok.Getter;

public enum MatchType {
    Equals("Equals"),
    Contains("Contains"),
    BeginsWith("Begins With"),
    EndsWith("Ends With"),
    Regex("Regex"),
    LessThan("Less Than"),
    LessThanOrEqual("Less Than Or Equal"),
    GreaterThan("Greater Than"),
    GreaterThanOrEqual("Greater Than Or Equal");

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
