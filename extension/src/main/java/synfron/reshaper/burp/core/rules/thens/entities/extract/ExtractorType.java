package synfron.reshaper.burp.core.rules.thens.entities.extract;

import lombok.Getter;

public enum ExtractorType {
    Regex("Regex", "Pattern"),
    Json("JSONPath", "Path"),
    CssSelector("CSS Selector", "Selector"),
    XPath("XPath", "Path"),
    Chunk("Chunk", "Size");

    @Getter
    private final String name;
    @Getter
    private final String syntax;

    ExtractorType(String name, String syntax) {
        this.name = name;
        this.syntax = syntax;
    }


    @Override
    public String toString() {
        return name;
    }
}
