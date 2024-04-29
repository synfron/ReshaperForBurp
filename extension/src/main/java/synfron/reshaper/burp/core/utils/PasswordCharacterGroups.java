package synfron.reshaper.burp.core.utils;

import lombok.Getter;

public enum PasswordCharacterGroups {
    LowercaseLetters("abcdefghijklmnopqrstuvwxyz"),
    UppercaseLetters("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    Numbers("1234567890"),
    Symbols("}<[{~;)-=%(|/,:!*>&_\\@#$+].?^`\"'");

    @Getter
    private final String characters;

    PasswordCharacterGroups(String characters) {
        this.characters = characters;
    }
}
