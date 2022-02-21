package synfron.reshaper.burp.ui.models.rules.wizard;

import lombok.Getter;

public enum WizardMatchType {
    Exists("Exists", false),
    Equals("Equals", true),
    Contains("Contains", true),
    BeginsWith("Begins With", true),
    EndsWith("Ends With", true),
    Regex("Regex", true);

    @Getter
    private final String name;
    @Getter
    private final boolean matcher;

    WizardMatchType(String name, boolean matcher) {
        this.name = name;
        this.matcher = matcher;
    }

    @Override
    public String toString() {
        return name;
    }
}
