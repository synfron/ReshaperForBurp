package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.PasswordGenerator;
import synfron.reshaper.burp.core.utils.PasswordCharacterGroup;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.EnumSet;
import java.util.List;

@Getter
public class PasswordGeneratorModel extends GeneratorModel<PasswordGeneratorModel, PasswordGenerator> implements IPasswordGeneratorModel {

    private String minLength = "8";
    private String maxLength = "64";
    private EnumSet<PasswordCharacterGroup> characterGroups;

    public PasswordGeneratorModel(PasswordGenerator generator) {
        super(generator);
        minLength = VariableString.toString(generator.getMinLength(), minLength);
        maxLength = VariableString.toString(generator.getMaxLength(), maxLength);
        characterGroups = EnumSet.copyOf(generator.getCharacterGroups());
    }

    @Override
    public void setMinLength(String minLength) {
        this.minLength = minLength;
        propertyChanged("minLength", minLength);
    }

    @Override
    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
        propertyChanged("maxLength", maxLength);
    }

    @Override
    public void addPasswordCharacterGroups(PasswordCharacterGroup passwordCharacterGroup) {
        characterGroups.add(passwordCharacterGroup);
        propertyChanged("characterGroups", characterGroups);
    }

    @Override
    public void removePasswordCharacterGroups(PasswordCharacterGroup passwordCharacterGroup) {
        characterGroups.remove(passwordCharacterGroup);
        propertyChanged("characterGroups", characterGroups);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(minLength)) {
            errors.add("Min Length is required");
        } else if (!VariableString.isPotentialInt(minLength)) {
            errors.add("Index must be an integer");
        }
        if (StringUtils.isEmpty(maxLength)) {
            errors.add("Index is required");
        } else if (!VariableString.isPotentialInt(maxLength)) {
            errors.add("Index must be an integer");
        }
        if (characterGroups.isEmpty()) {
            errors.add("Must choose at least one character group");
        }
        return errors;
    }

    @Override
    public boolean persist() {
        super.persist();
        generator.setMinLength(VariableString.getAsVariableString(minLength));
        generator.setMaxLength(VariableString.getAsVariableString(maxLength));
        generator.setCharacterGroups(EnumSet.copyOf(characterGroups));
        return true;
    }
}
