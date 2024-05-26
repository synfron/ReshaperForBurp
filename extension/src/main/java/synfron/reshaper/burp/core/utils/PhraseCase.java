package synfron.reshaper.burp.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PhraseCase {
    LowerCase("lower case"),
    UpperCase("UPPER CASE"),
    FlatCase("flatcase"),
    CamelCase("camelCase"),
    PascalCase("PascalCase"),
    SnakeCase("snake_case"),
    ConstantCase("CONSTANT_CASE"),
    DashCase("dash-case"),
    CobolCase("COBOL-CASE"),
    TitleCase("Title Case"),
    SentenceCase("Sentence case");

    private final String name;


    @Override
    public String toString() {
        return name;
    }
}
