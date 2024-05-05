package synfron.reshaper.burp.ui.models.rules.wizard.vars.generator;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.utils.PasswordCharacterGroup;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IPasswordGeneratorModel;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PasswordGeneratorVariableModel extends GeneratorVariableModel<PasswordGeneratorVariableModel> implements IPasswordGeneratorModel {

    private String minLength = "8";
    private String maxLength = "64";
    private EnumSet<PasswordCharacterGroup> characterGroups = EnumSet.allOf(PasswordCharacterGroup.class);

    public void setMinLength(String minLength) {
        this.minLength = minLength;
        propertyChanged("minLength", minLength);
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
        propertyChanged("maxLength", maxLength);
    }

    public void addPasswordCharacterGroups(PasswordCharacterGroup passwordCharacterGroup) {
        characterGroups.add(passwordCharacterGroup);
        propertyChanged("characterGroups", characterGroups);
    }

    public void removePasswordCharacterGroups(PasswordCharacterGroup passwordCharacterGroup) {
        characterGroups.remove(passwordCharacterGroup);
        propertyChanged("characterGroups", characterGroups);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(minLength)) {
            errors.add("Min Length is required");
        } else if (!TextUtils.isInt(minLength)) {
            errors.add("Index must be an integer");
        }
        if (StringUtils.isEmpty(maxLength)) {
            errors.add("Index is required");
        } else if (!TextUtils.isInt(maxLength)) {
            errors.add("Index must be an integer");
        }
        if (characterGroups.isEmpty()) {
            errors.add("Must choose at least one character group");
        }
        return errors;
    }

    @Override
    public String getTagInternal() {
        return VariableTag.getShortTag(VariableSource.Generator, GenerateOption.Password.name().toLowerCase(), minLength, maxLength, characterGroups.stream().map(Enum::name).map(String::toLowerCase).collect(Collectors.joining(",")));
    }
}
