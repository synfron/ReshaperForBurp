package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.repeat.RepeatCondition;
import synfron.reshaper.burp.core.vars.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThenRepeat extends Then<ThenRepeat> implements IHttpRuleOperation, IWebSocketRuleOperation, IThenGroup {

    @Getter @Setter
    private int subGroupCount = 1;
    @Getter @Setter
    private RepeatCondition repeatCondition = RepeatCondition.Count;
    @Getter @Setter
    private VariableString count;
    @Getter @Setter
    private VariableSource listVariableSource = VariableSource.GlobalList;
    @Getter @Setter
    private VariableString listVariableName;
    @Getter @Setter
    private VariableString entryVariableName;
    @Getter @Setter
    private VariableString booleanValue;
    @Getter @Setter
    private int maxCount = 1000;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        return perform(List.of(), eventInfo);
    }

    @Override
    public RuleResponse perform(List<? extends Then<?>> thensSubList, EventInfo eventInfo) {
        boolean hasError = false;
        int diagnosticsPosition = -1;
        RuleResponse thenResult = RuleResponse.Continue;
        List<Pair<String, ? extends Serializable>> diagnosticProperties = eventInfo.getDiagnostics().isEnabled() ? new ArrayList<>() : null;
        try {
            MutableObject<String> conditionData = new MutableObject<>();
                Iterator<Object> iterator = switch (repeatCondition) {
                    case Count -> getCountIterator(eventInfo, diagnosticProperties);
                    case HasNextItem -> getHasNextItemIterator(eventInfo, diagnosticProperties, conditionData);
                    case WhileTrue  -> getWhileTrueIterator(eventInfo, diagnosticProperties);
                };
                if (eventInfo.getDiagnostics().isEnabled()) {
                    diagnosticsPosition = eventInfo.getDiagnostics().size();
                    eventInfo.getDiagnostics().logGroupContainerStart();
                }
                int iterationCount = 0;
                if (iterator != null) {
                    while (iterator.hasNext()) {
                        thenResult = RuleResponse.Continue;
                        iterationCount++;
                        Object value = iterator.next();
                        if (repeatCondition == RepeatCondition.HasNextItem) {
                            Variable entryVariable = eventInfo.getVariables().add(Variables.asKey(conditionData.getValue(), false));
                            entryVariable.setValue(value);
                            if (eventInfo.getDiagnostics().isEnabled())
                                eventInfo.getDiagnostics().logGroupStart(String.format("Repeat #%s for value `%s`", iterationCount, value));
                        } else {
                            if (eventInfo.getDiagnostics().isEnabled())
                                eventInfo.getDiagnostics().logGroupStart("Repeat #" + iterationCount);
                        }
                        RuleResponse result = IThenGroup.super.perform(thensSubList, eventInfo);
                        thenResult = thenResult.or(result);
                        if (eventInfo.getDiagnostics().isEnabled())
                            eventInfo.getDiagnostics().logGroupEnd("End Repeat #" + iterationCount);
                        if (result.hasFlags(RuleResponse.BreakThens) || result.hasFlags(RuleResponse.BreakRules)) {
                            break;
                        }
                        if (repeatCondition == RepeatCondition.WhileTrue && iterationCount >= maxCount) {
                            break;
                        }
                    }
                    if (eventInfo.getDiagnostics().isEnabled())
                        eventInfo.getDiagnostics().logGroupContainerEnd();
                }
        }
        catch (Exception e) {
            hasError = true;
            throw e;
        }
        finally {
            if (eventInfo.getDiagnostics().isEnabled()) {
                diagnosticProperties.add(Pair.of("subGroupCount", subGroupCount));
                diagnosticProperties.add(Pair.of("repeatCondition", repeatCondition));
                eventInfo.getDiagnostics().logProperties(this, hasError, diagnosticProperties);
                if (diagnosticsPosition != -1) {
                    eventInfo.getDiagnostics().moveLast(diagnosticsPosition);
                }
            }
        }
        return thenResult;
    }

    private Iterator<Object> getWhileTrueIterator(EventInfo eventInfo, List<Pair<String,? extends Serializable>> diagnosticProperties) {
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("booleanValue", booleanValue.toString()));
            diagnosticProperties.add(Pair.of("maxCount", maxCount));
        }
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return BooleanUtils.toBoolean(booleanValue.getText(eventInfo));
            }

            @Override
            public Object next() {
                return hasNext();
            }
        };
    }

    private Iterator<Object> getCountIterator(EventInfo eventInfo, List<Pair<String,? extends Serializable>> diagnosticProperties) {
        Integer max = count.getInt(eventInfo);
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("count", max));
        }
        return max != null ? new Iterator<>() {

            private int iterationCount = 0;

            @Override
            public boolean hasNext() {
                return iterationCount < max;
            }

            @Override
            public Object next() {
                return iterationCount++;
            }
        } : null;
    }

    private Iterator<Object> getHasNextItemIterator(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties, Mutable<String> conditionData) {
        Integer listSize;
        String listVariableNameText;
        String entryVariableNameText;
        listVariableNameText = listVariableName.getText(eventInfo);
        entryVariableNameText = entryVariableName.getText(eventInfo);
        VariableSourceEntry variable1 = new VariableSourceEntry(listVariableSource, List.of(listVariableNameText));
        ListVariable variable = switch (variable1.getVariableSource()) {
            case GlobalList -> (ListVariable) eventInfo.getWorkspace().getGlobalVariables().getOrDefault(Variables.asKey(variable1.getParams().getFirst(), true));
            case EventList -> (ListVariable) eventInfo.getVariables().getOrDefault(Variables.asKey(variable1.getParams().getFirst(), true));
            case SessionList -> (ListVariable) eventInfo.getSessionVariables().getOrDefault(Variables.asKey(variable1.getParams().getFirst(), true));
            default -> null;
        };
        listSize = variable != null ? variable.size() : null;
        conditionData.setValue(entryVariableNameText);
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("listVariable", VariableTag.getTag(listVariableSource, listVariableNameText)));
            diagnosticProperties.add(Pair.of("listSize", listSize != null ? listSize.toString() : "null"));
            diagnosticProperties.add(Pair.of("entryVariable", VariableTag.getTag(VariableSource.Event, entryVariableNameText)));
        }
        return variable != null ? variable.getIterator() : null;
    }

    @Override
    public RuleOperationType<ThenRepeat> getType() {
        return ThenType.Repeat;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public int groupSize() {
        return Math.max(subGroupCount, 0);
    }
}
