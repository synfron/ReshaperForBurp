package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.transform.ITransformer;
import synfron.reshaper.burp.core.rules.thens.entities.transform.TransformOption;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThenTransform extends Then<ThenTransform> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter @Setter
    private TransformOption transformOption = TransformOption.Base64;
    @Getter @Setter
    private ITransformer transformer;
    @Getter @Setter
    private VariableSource destinationVariableSource = VariableSource.Global;
    @Getter @Setter
    private VariableString destinationVariableName;
    @Getter @Setter
    private SetListItemPlacement itemPlacement = SetListItemPlacement.Index;
    @Getter @Setter
    private VariableString delimiter;
    @Getter @Setter
    private VariableString index;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = true;
        List<Pair<String, ? extends Serializable>> diagnosticProperties = eventInfo.getDiagnostics().isEnabled() ?
                new ArrayList<>() :
                null;
        try {
            String value = transformer.transform(eventInfo, diagnosticProperties);
            setVariable(
                    destinationVariableSource, eventInfo,
                    destinationVariableName.getText(eventInfo),
                    itemPlacement,
                    VariableString.getTextOrDefault(eventInfo, delimiter, "\n"),
                    VariableString.getIntOrDefault(eventInfo, index, 0),
                    value
            );
            hasError = false;
        } finally {
            if (diagnosticProperties != null) {
                diagnosticProperties.addAll(
                        Arrays.asList(
                                Pair.of("transformOption", transformOption),
                                Pair.of("destinationVariableSource", destinationVariableSource),
                                Pair.of("destinationVariableName", VariableString.getTextOrDefault(eventInfo, destinationVariableName, null)),
                                Pair.of("itemPlacement", destinationVariableSource.isList() ? itemPlacement : null),
                                Pair.of("delimiter", destinationVariableSource.isList() && itemPlacement.isHasDelimiterSetter() ? delimiter : null),
                                Pair.of("index", destinationVariableSource.isList() && itemPlacement.isHasIndexSetter() ? VariableString.getTextOrDefault(eventInfo, index, null) : null)
                        )
                );
                eventInfo.getDiagnostics().logProperties(this, hasError, diagnosticProperties);
            }
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenTransform> getType() {
        return ThenType.Transform;
    }
}
