package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.ui.components.rules.RuleOperationContainerComponent;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.thens.ThenModelType;

import java.util.HashMap;
import java.util.Map;

public class ThenContainerComponent extends RuleOperationContainerComponent {

    private final static Map<RuleOperationModelType<?,?>, Class<?>> componentMap;

    static {
        componentMap = new HashMap<>();
        componentMap.put(ThenModelType.Break, ThenBreakComponent.class);
        componentMap.put(ThenModelType.Delay, ThenDelayComponent.class);
        componentMap.put(ThenModelType.Log, ThenLogComponent.class);
        componentMap.put(ThenModelType.Highlight, ThenHighlightComponent.class);
        componentMap.put(ThenModelType.RunRules, ThenRunRulesComponent.class);
        componentMap.put(ThenModelType.RunScript, ThenRunScriptComponent.class);
        componentMap.put(ThenModelType.SetEventDirection, ThenSetEventDirectionComponent.class);
        componentMap.put(ThenModelType.SetValue, ThenSetValueComponent.class);
        componentMap.put(ThenModelType.DeleteValue, ThenDeleteValueComponent.class);
        componentMap.put(ThenModelType.SetVariable, ThenSetVariableComponent.class);
        componentMap.put(ThenModelType.DeleteVariable, ThenDeleteVariableComponent.class);
        componentMap.put(ThenModelType.SendTo, ThenSendToComponent.class);
        componentMap.put(ThenModelType.Drop, ThenDropComponent.class);
    }

    @Override
    protected Map<RuleOperationModelType<?,?>, Class<?>> getComponentMap() {
        return componentMap;
    }
}
