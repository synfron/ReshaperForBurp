package synfron.reshaper.burp.core.vars.getters;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.*;

import java.util.List;

public class CustomListVariableGetter extends VariableGetter {

    @Override
    public String getText(VariableSourceEntry variableSourceEntry, EventInfo eventInfo) {
        List<String> locatorParts = variableSourceEntry.getParams();
        String variableName = locatorParts.getFirst();
        ListVariable variable = getVariable(variableSourceEntry, variableName, eventInfo);
        String place = CollectionUtils.elementAtOrDefault(locatorParts, 1);
        if (variable == null) {
            return null;
        }
        else if (StringUtils.isEmpty(place)) {
            return TextUtils.toString(variable.getValue());
        }

        GetListItemPlacement itemPlacement = EnumUtils.getEnumIgnoreCase(GetListItemPlacement.class, place);
        Integer index = TextUtils.asInt(place);
        return TextUtils.toString(variable.getValue(itemPlacement, index));
    }

    private static ListVariable getVariable(VariableSourceEntry variable, String variableName, EventInfo eventInfo) {
        Variable value = switch (variable.getVariableSource()) {
            case GlobalList -> (ListVariable) eventInfo.getWorkspace().getGlobalVariables().getOrDefault(Variables.asKey(variableName, true));
            case EventList -> (ListVariable) eventInfo.getVariables().getOrDefault(Variables.asKey(variableName, true));
            case SessionList -> (ListVariable) eventInfo.getSessionVariables().getOrDefault(Variables.asKey(variableName, true));
            default -> null;
        };
        return value instanceof ListVariable listVariable ? listVariable : null;
    }

}
