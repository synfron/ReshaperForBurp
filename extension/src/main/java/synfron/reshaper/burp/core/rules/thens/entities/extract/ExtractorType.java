package synfron.reshaper.burp.core.rules.thens.entities.extract;

import lombok.Getter;

public enum ExtractorType {
    Regex("Regex", "Expression"),
    Json("JSON Path", "Path"),
    CssSelector("CSS Selector", "Selector"),
    XPath("XPath", "Path"),
    Chunk("Chunk", "Size");

    @Getter
    private final String name;
    @Getter
    private final String extractorType;

    ExtractorType(String name, String extractorType) {
        this.name = name;
        this.extractorType = extractorType;
    }


    @Override
    public String toString() {
        return name;
    }
}
