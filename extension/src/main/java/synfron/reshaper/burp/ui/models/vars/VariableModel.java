package synfron.reshaper.burp.ui.models.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VariableModel {
    @Getter
    private Variable variable;
    @Getter
    private String name = "";
    @Getter
    private String value = "";
    @Getter
    private String delimiter = "";
    @Getter
    private boolean isList;

    @Getter
    private VariableValueType valueType = VariableValueType.Text;
    @Getter
    private boolean persistent;
    @Getter
    private boolean saved = true;
    private boolean activated;
    private boolean changedWhileInactive;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    private final IEventListener<PropertyChangedArgs> variableChanged = this::onVariableChanged;
    private final IEventListener<PropertyChangedArgs> activationChanged = this::onActivationChanged;

    public VariableModel(boolean isList) {
        this.isList = isList;
        delimiter = StringEscapeUtils.escapeJava("\n");
        saved = false;
    }

    public VariableModel(Variable variable) {
        this.variable = variable.withListener(variableChanged);
        name = variable.getName();
        valueType = variable.getValueType();
        value = StringUtils.defaultString(TextUtils.toString(variable.getValue()));
        if (variable instanceof ListVariable listVariable) {
            isList = true;
            delimiter = StringEscapeUtils.escapeJava(listVariable.getDelimiter());
        }
        persistent = variable.isPersistent();
    }

    private void onVariableChanged(PropertyChangedArgs propertyChangedArgs) {
        if (activated) {
            syncProperties();
        } else {
            changedWhileInactive = true;
        }
    }

    private void syncProperties() {
        SwingUtilities.invokeLater(() -> {
            value = StringUtils.defaultString(TextUtils.toString(variable.getValue()));
            if (isList) {
                delimiter = StringEscapeUtils.escapeJava(((ListVariable) variable).getDelimiter());
            }
            persistent = variable.isPersistent();
            propertyChanged("this", this);
            setSaved(true);
        });
    }

    private void onActivationChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getValue() == this) {
            if (!activated) {
                activated = true;
                if (changedWhileInactive) {
                    syncProperties();
                    changedWhileInactive = false;
                }
            }
        } else {
            activated = false;
        }

    }

    public VariableModel withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public VariableModel bindActivationChangedEvent(PropertyChangedEvent activationChangedEvent) {
        activationChangedEvent.add(activationChanged);
        return this;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(name)) {
            errors.add("Variable Name is required");
        } else if ((variable == null || !StringUtils.equals(variable.getName(), name)) && GlobalVariables.get().has(Variables.asKey(name, isList))) {
            errors.add("Variable Name must be unique");
        } else if (!VariableString.isValidVariableName(name)) {
            errors.add("Variable Name is invalid");
        }
        return errors;
    }

    public void setName(String name) {
        this.name = name;
        propertyChanged("name", name);
    }

    public void setValue(String value) {
        this.value = value;
        propertyChanged("value", value);
    }

    public void setValueType(VariableValueType valueType) {
        this.valueType = valueType;
        propertyChanged("valueType", valueType);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        propertyChanged("delimiter", delimiter);
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
        propertyChanged("persistent", persistent);
    }

    private void setSaved(boolean saved) {
        if (saved != this.saved) {
            this.saved = saved;
            propertyChangedEvent.invoke(new PropertyChangedArgs(this, "saved", saved));
        }
    }

    private void propertyChanged(String name, Object value) {
        setSaved(false);
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public boolean save() {
        if (!validate().isEmpty()) {
            return false;
        }
        Variable variable = this.variable;
        if (variable == null) {
            variable = GlobalVariables.get().add(Variables.asKey(name, isList));
        } else if (!StringUtils.equals(variable.getName(), name)) {
            GlobalVariables.get().remove(Variables.asKey(variable.getName(), isList));
            variable = GlobalVariables.get().add(Variables.asKey(name, isList));
        }
        if (isList) {
            ((ListVariable)variable).setDelimiter(StringEscapeUtils.unescapeJava(delimiter));
        }
        variable.setValue(value);
        variable.setValueType(valueType);
        variable.setPersistent(persistent);
        setSaved(true);
        return saved;
    }

    @Override
    public int hashCode() {
        return variable != null ? variable.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VariableModel other) {
            return (variable != null && Objects.equals(other.variable, variable)) || super.equals(obj);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return StringUtils.defaultIfEmpty(name, "untitled") + (isList ? "[]" : "") + (saved ? "" : " *");
    }
}
