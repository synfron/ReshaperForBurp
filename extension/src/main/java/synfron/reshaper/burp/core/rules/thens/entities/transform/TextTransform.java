package synfron.reshaper.burp.core.rules.thens.entities.transform;

public enum TextTransform {
    FromText("From Text"),
    ToText("To Text");

    TextTransform(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
