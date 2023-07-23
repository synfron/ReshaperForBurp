package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.vars.GetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;

public abstract class CustomListVariableTagWizardModel extends CustomVariableTagWizardModel {

    @Getter
    private GetListItemPlacement itemPlacement = GetListItemPlacement.Index;

    @Getter
    private String index;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public CustomListVariableTagWizardModel(List<VariableSourceEntry> entries) {
        super(entries);
    }

    public void setItemPlacement(GetListItemPlacement itemPlacement) {
        this.itemPlacement = itemPlacement;
        propertyChanged("itemPlacement", itemPlacement);
    }

    public void setIndex(String index) {
        this.index = index;
        propertyChanged("index", index);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (itemPlacement.isHasIndexSetter()) {
            if (StringUtils.isEmpty(index)) {
                errors.add("Index is required");
            } else if (!VariableString.isPotentialInt(index)) {
                errors.add("Index must be an integer");
            }
        }
        return errors;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableSourceEntry.getShortTag(
                        getVariableSource(),
                        getVariableName(),
                        itemPlacement == GetListItemPlacement.Index ? index : itemPlacement.name()
                ) :
                null;
    }
}
