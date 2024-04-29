package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.extract.ExtractorType;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.*;

import java.util.List;

public class ThenExtract extends Then<ThenExtract> implements IHttpRuleOperation, IWebSocketRuleOperation {

    @Getter @Setter
    private VariableString text;
    @Getter @Setter
    private ExtractorType extractorType = ExtractorType.Regex;
    @Getter @Setter
    private VariableString extractor;
    @Getter @Setter
    private VariableSource listVariableSource = VariableSource.GlobalList;
    @Getter @Setter
    private VariableString listVariableName;
    @Getter @Setter
    private VariableString delimiter;
    @Getter @Setter
    private SetListItemsPlacement itemsPlacement = SetListItemsPlacement.Overwrite;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        String valueText = null;
        String extractorText = null;
        String listVariableNameText = null;
        String delimiterText = null;
        ListVariable variable = null;

        boolean hasError = false;
        try {
            valueText = text.getText(eventInfo);
            extractorText = extractor.getText(eventInfo);
            listVariableNameText = listVariableName.getText(eventInfo);
            delimiterText = delimiter.getText(eventInfo);
            VariableSourceEntry variableSource = new VariableSourceEntry(listVariableSource, listVariableNameText);
            variable = switch (variableSource.getVariableSource()) {
                case GlobalList -> (ListVariable) GlobalVariables.get().add(Variables.asKey(variableSource.getName(), true));
                case EventList -> (ListVariable) eventInfo.getVariables().add(Variables.asKey(variableSource.getName(), true));
                case SessionList -> (ListVariable) eventInfo.getSessionVariables().add(Variables.asKey(variableSource.getName(), true));
                default -> null;
            };

            List<String> values = switch (extractorType) {
                case Regex -> TextUtils.getRegexValues(valueText, extractorText);
                case Json -> TextUtils.getJsonPathValues(valueText, extractorText);
                case CssSelector -> TextUtils.getCssSelectorValues(valueText, extractorText);
                case XPath -> TextUtils.getXPathValues(valueText, extractorText);
                case Chunk -> TextUtils.getChunks(valueText, TextUtils.asInt(extractorText));
            };

            variable.setValues(values.toArray(), delimiterText, itemsPlacement);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, List.of(
                    Pair.of("text", valueText),
                    Pair.of("extractorType", extractorType),
                    Pair.of("extractor", extractorText),
                    Pair.of("listVariableSource", listVariableSource),
                    Pair.of("listVariableName", listVariableNameText),
                    Pair.of("delimiter", delimiterText),
                    Pair.of("itemsPlacement", itemsPlacement),
                    Pair.of("value", variable != null && variable.hasValue() ? variable.getValue().toString() : null)
            ));
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenExtract> getType() {
        return ThenType.Extract;
    }
}
