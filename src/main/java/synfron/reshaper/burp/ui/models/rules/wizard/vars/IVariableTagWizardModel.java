package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.vars.VariableSource;

import java.util.List;

public interface IVariableTagWizardModel {
    String getTag();
    List<String> validate();
    VariableSource getVariableSource();
    PropertyChangedEvent getPropertyChangedEvent();
}
