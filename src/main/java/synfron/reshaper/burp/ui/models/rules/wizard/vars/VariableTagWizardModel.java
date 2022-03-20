package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.ui.models.rules.thens.VariableCreatorRegistry;
import synfron.reshaper.burp.ui.utils.IPrompterModel;
import synfron.reshaper.burp.ui.utils.ModalPrompter;

import java.util.List;
import java.util.Map;

public class VariableTagWizardModel implements IVariableTagWizardModel, IPrompterModel<VariableTagWizardModel> {

    @Getter
    private VariableSource variableSource = VariableSource.Event;

    private final Map<VariableSource, IVariableTagWizardModel> tagModelMap;

    @Getter
    private IVariableTagWizardModel tagModel;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    @Getter
    private boolean invalidated;
    @Getter
    private boolean dismissed;

    @Setter @Getter
    private ModalPrompter<VariableTagWizardModel> modalPrompter;

    public VariableTagWizardModel() {
        List<VariableSourceEntry> variableSourceEntries = VariableCreatorRegistry.getVariableEntries();

        tagModelMap = Map.of(
          VariableSource.Event, new EventVariableTagWizardModel(variableSourceEntries),
          VariableSource.Global, new GlobalVariableTagWizardModel(variableSourceEntries),
          VariableSource.Message, new MessageVariableTagWizardModel(),
          VariableSource.File, new FileVariableTagWizardModel(),
          VariableSource.Special, new SpecialVariableTagWizardModel(),
          VariableSource.CookieJar, new CookieJarVariableTagWizardModel()
        );

        tagModel = tagModelMap.get(variableSource);
    }

    public VariableTagWizardModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    @Override
    public void resetPropertyChangedListener() {
        propertyChangedEvent.clearListeners();
        tagModelMap.values().forEach(item -> item.getPropertyChangedEvent().clearListeners());
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setVariableSource(VariableSource variableSource) {
        this.variableSource = variableSource;
        this.tagModel = tagModelMap.get(variableSource);
        propertyChanged("variableSource", variableSource);
        propertyChanged("tagModel", tagModel);
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
        propertyChanged("invalidated", invalidated);
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
        propertyChanged("dismissed", dismissed);
    }

    @Override
    public String getTag() {
        String tag = validate().isEmpty() ?
                tagModel.getTag() :
                null;
        return tag;
    }

    @Override
    public List<String> validate() {
        List<String> errors = tagModel.validate();
        setInvalidated(!errors.isEmpty());
        return errors;
    }
}
