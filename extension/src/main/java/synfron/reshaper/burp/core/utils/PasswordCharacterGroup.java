package synfron.reshaper.burp.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum PasswordCharacterGroup {
    LowercaseLetters("Lowercase Letters", "abcdefghijklmnopqrstuvwxyz"),
    UppercaseLetters("Uppercase Letters", "ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    Numbers("1234567890"),
    Symbols("}<[{~;)-=%(|/,:!*>&_\\@#$+].?^`\"'");

    private String name;
    private final String characters;

    public String getName() {
        return StringUtils.defaultString(name, name());
    }

    @Override
    public String toString() {
        return getName();
    }
}
