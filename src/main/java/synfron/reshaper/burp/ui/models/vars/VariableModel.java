package synfron.reshaper.burp.ui.models.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.core.vars.VariableString;

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
    private boolean persistent;
    @Getter
    private boolean saved = true;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    private IEventListener<PropertyChangedArgs> variableChanged = this::onVariableChanged;

    public VariableModel() {
        saved = false;
    }

    public VariableModel(Variable variable) {
        this.variable = variable.withListener(variableChanged);
        name = variable.getName();
        value = StringUtils.defaultString(TextUtils.toString(variable.getValue()));
        persistent = variable.isPersistent();
    }

    private void onVariableChanged(PropertyChangedArgs propertyChangedArgs) {
        SwingUtilities.invokeLater(() -> {
            value = StringUtils.defaultString(TextUtils.toString(variable.getValue()));
            persistent = variable.isPersistent();
            propertyChanged("this", this);
            setSaved(true);
        });
    }

    public VariableModel withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(name)) {
            errors.add("Variable Name is required");
        } else if ((variable == null || !StringUtils.equals(variable.getName(), name)) && GlobalVariables.get().has(name)) {
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
        if (validate().size() != 0) {
            return false;
        }
        Variable variable = this.variable;
        if (variable == null) {
            variable = GlobalVariables.get().add(name);
        } else if (!StringUtils.equals(variable.getName(), name)) {
            GlobalVariables.get().remove(variable.getName());
            variable = GlobalVariables.get().add(name);
        }
        variable.setValue(value);
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
        if (obj instanceof VariableModel) {
            VariableModel other = (VariableModel)obj;
            return (variable != null && Objects.equals(other.variable, variable)) || super.equals(obj);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return StringUtils.defaultIfEmpty(name, "untitled") + (saved ? "" : " *");
    }
}
