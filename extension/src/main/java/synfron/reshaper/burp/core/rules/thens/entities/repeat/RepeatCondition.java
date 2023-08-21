package synfron.reshaper.burp.core.rules.thens.entities.repeat;

import lombok.Getter;

public enum RepeatCondition {
    Count("Count"),
    HasNextItem("Has Next Item"),
    WhileTrue("While True");

    @Getter
    private final String name;

    RepeatCondition(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
