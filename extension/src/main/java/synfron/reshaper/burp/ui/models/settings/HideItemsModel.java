package synfron.reshaper.burp.ui.models.settings;

import lombok.Getter;
import synfron.reshaper.burp.core.WorkspaceTab;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.settings.GeneralSettings;
import synfron.reshaper.burp.ui.utils.IPrompterModel;

import java.util.HashSet;

@Getter
public class HideItemsModel implements IPrompterModel<HideItemsModel> {
    private final GeneralSettings generalSettings;
    private final HashSet<String> hiddenThenTypes = new HashSet<>();
    private final HashSet<String> hiddenWhenTypes = new HashSet<>();
    private final HashSet<WorkspaceTab> hiddenTabs = new HashSet<>();
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    private boolean dismissed;

    public HideItemsModel(GeneralSettings generalSettings) {
        this.generalSettings = generalSettings;
        hiddenWhenTypes.addAll(generalSettings.getHiddenWhenTypes());
        hiddenThenTypes.addAll(generalSettings.getHiddenThenTypes());
        hiddenTabs.addAll(generalSettings.getHiddenTabs());
    }

    public HideItemsModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void addHiddenWhenType(String whenType) {
        hiddenWhenTypes.add(whenType);
        propertyChanged("hiddenWhenTypes", whenType);

    }

    public void addHiddenThenType(String thenType) {
        hiddenThenTypes.add(thenType);
        propertyChanged("hiddenThenTypes", thenType);
    }

    public void addHiddenTab(WorkspaceTab tab) {
        hiddenTabs.add(tab);
        propertyChanged("hiddenTabs", tab);
    }

    public void removeHiddenWhenType(String whenType) {
        hiddenWhenTypes.remove(whenType);
        propertyChanged("hiddenWhenTypes", whenType);
    }

    public void removeHiddenThenType(String thenType) {
        hiddenThenTypes.remove(thenType);
        propertyChanged("hiddenThenTypes", thenType);
    }

    public void removeHiddenTab(WorkspaceTab tab) {
        hiddenTabs.remove(tab);
        propertyChanged("hiddenTabs", tab);
    }

    public boolean save() {
        generalSettings.setHiddenTabs(hiddenTabs);
        generalSettings.setHiddenThenTypes(hiddenThenTypes);
        generalSettings.setHiddenWhenTypes(hiddenWhenTypes);
        return true;
    }

    @Override
    public boolean isInvalidated() {
        return false;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
        propertyChanged("dismissed", dismissed);
    }

    @Override
    public boolean submit() {
        if (save()) {
            setDismissed(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancel() {
        setDismissed(true);
        return true;
    }
}
