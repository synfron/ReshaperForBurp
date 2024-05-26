package synfron.reshaper.burp.core.rules.thens.entities.sendto;

import lombok.Getter;

public enum SendToOption {
    Comparer,
    Intruder,
    Repeater,
    Spider, // Unused
    Browser,
    Organizer,
    Decoder,
    SiteMap("Site Map");

    private final String name;

    SendToOption() {
        name = name();
    }

    SendToOption(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
