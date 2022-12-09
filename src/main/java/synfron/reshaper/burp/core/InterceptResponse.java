package synfron.reshaper.burp.core;

import lombok.Getter;

public enum InterceptResponse {
    UserDefined("Burp Defined"),
    Drop("Drop"),
    Disable("Disable"),
    Intercept("Intercept");

    @Getter
    private final String name;

    InterceptResponse(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
