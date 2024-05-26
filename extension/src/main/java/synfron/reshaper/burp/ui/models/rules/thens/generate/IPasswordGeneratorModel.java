package synfron.reshaper.burp.ui.models.rules.thens.generate;

import synfron.reshaper.burp.core.utils.PasswordCharacterGroup;

public interface IPasswordGeneratorModel extends IGeneratorModel<IPasswordGeneratorModel> {
    void setMinLength(String minLength);

    void setMaxLength(String maxLength);

    void addPasswordCharacterGroups(PasswordCharacterGroup passwordCharacterGroup);

    void removePasswordCharacterGroups(PasswordCharacterGroup passwordCharacterGroup);

    String getMinLength();

    String getMaxLength();

    java.util.EnumSet<PasswordCharacterGroup> getCharacterGroups();
}
