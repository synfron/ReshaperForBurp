package synfron.reshaper.burp.core.rules.whens.entities.repeat;

import lombok.Getter;

public enum SuccessCriteria {
    AnyMatch("Any Match"),
    AllMatch("All Match");

    @Getter
    private final String name;

    SuccessCriteria(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
