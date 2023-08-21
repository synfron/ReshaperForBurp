package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SpecialVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private final List<SpecialChar> specialChars = List.of(
           new SpecialChar("New Line", "n"),
            new SpecialChar("Carriage Return", "r"),
            new SpecialChar("Carriage Return + New Line", "rn"),
            new SpecialChar("Tab", "t"),
            new SpecialChar("Form Feed", "f"),
            new SpecialChar("Backspace", "b"),
            new SpecialChar("ASCII Code", "x", "^[0-9a-fA-F]+$", 2),
            new SpecialChar("Unicode Code", "u", "^[0-9a-fA-F]+$", 4),
            new SpecialChar("Other Character", "", "^[^\\}xu]$", 1)
    );

    @Getter
    private SpecialChar specialChar = specialChars.get(0);

    @Getter
    private String value;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setSpecialChar(SpecialChar specialChar) {
        this.specialChar = specialChar;
        propertyChanged("specialChar", specialChar);
    }

    public void setValue(String value) {
        this.value = value;
        propertyChanged("value", value);
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (specialChar.isValueRequired()) {
            if (StringUtils.isEmpty(value)) {
                errors.add("Value is required");
            } else if (specialChar.valueLength != null && value.length() != specialChar.valueLength) {
                errors.add(String.format("Value does not have the expect length (%s)", specialChar.valueLength));
            } else if (specialChar.valuePattern != null && !Pattern.matches(specialChar.valuePattern, value)) {
                errors.add("Value has invalid text");
            }
        }
        return errors;
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.Special;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableSourceEntry.getShortTag(
                        VariableSource.Special,
                        specialChar.code + (specialChar.isValueRequired() ? value : "")
                ) :
                null;
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class SpecialChar {
        @NonNull
        private String displayName;
        @NonNull
        private String code;
        private String valuePattern;
        private Integer valueLength;

        public boolean isValueRequired() {
            return valuePattern != null || (valueLength != null && valueLength > 0);
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
