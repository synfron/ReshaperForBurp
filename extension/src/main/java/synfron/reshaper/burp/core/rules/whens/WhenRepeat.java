package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.whens.entities.repeat.SuccessCriteria;
import synfron.reshaper.burp.core.vars.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WhenRepeat extends When<WhenRepeat> implements IHttpRuleOperation, IWebSocketRuleOperation, IWhenGroup {

    @Getter @Setter
    private int subGroupCount = 1;
    @Getter @Setter
    private VariableSource listVariableSource = VariableSource.GlobalList;
    @Getter @Setter
    private VariableString listVariableName;
    @Getter @Setter
    private SuccessCriteria successCriteria = SuccessCriteria.AnyMatch;
    @Getter @Setter
    private VariableString entryVariableName;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        return isMatch(List.of(), eventInfo);
    }

    @Override
    public boolean isMatch(List<? extends When<?>> whensSubList, EventInfo eventInfo) {
        boolean isMatch = false;
        String listVariableNameText = null;
        String entryVariableNameText = null;
        Integer listSize = null;
        int diagnosticsPosition = -1;
        try {
            listVariableNameText = listVariableName.getText(eventInfo);
            entryVariableNameText = entryVariableName.getText(eventInfo);
            VariableSourceEntry variable1 = new VariableSourceEntry(listVariableSource, List.of(listVariableNameText));
            ListVariable variable = switch (variable1.getVariableSource()) {
                case GlobalList -> (ListVariable) GlobalVariables.get().getOrDefault(Variables.asKey(variable1.getParams().getFirst(), true));
                case EventList -> (ListVariable) eventInfo.getVariables().getOrDefault(Variables.asKey(variable1.getParams().getFirst(), true));
                case SessionList -> (ListVariable) eventInfo.getSessionVariables().getOrDefault(Variables.asKey(variable1.getParams().getFirst(), true));
                default -> null;
            };

            if (variable != null) {
                listSize = variable.size();
                Iterator<Object> iterator = variable.getIterator();
                if (eventInfo.getDiagnostics().isEnabled()) {
                    diagnosticsPosition = eventInfo.getDiagnostics().size();
                    eventInfo.getDiagnostics().logGroupContainerStart();
                }
                int iterationCount = 0;
                while (iterator.hasNext()) {
                    iterationCount++;
                    Object value = iterator.next();
                    Variable entryVariable = eventInfo.getVariables().add(Variables.asKey(entryVariableNameText, false));
                    entryVariable.setValue(value);
                    if (eventInfo.getDiagnostics().isEnabled())
                        eventInfo.getDiagnostics().logGroupStart(String.format("Repeat #%s for value `%s`", iterationCount, value));
                    isMatch = IWhenGroup.super.isMatch(whensSubList, eventInfo);
                    if (eventInfo.getDiagnostics().isEnabled())
                        eventInfo.getDiagnostics().logGroupEnd("End Repeat #" + iterationCount);
                    if (isMatch && successCriteria == SuccessCriteria.AnyMatch) {
                        break;
                    }
                }
                if (eventInfo.getDiagnostics().isEnabled())
                    eventInfo.getDiagnostics().logGroupContainerEnd();
            }
        } catch (Exception ignored) {
            isMatch = false;
        }
        finally {
            if (eventInfo.getDiagnostics().isEnabled()) {
                eventInfo.getDiagnostics().logProperties(this, isMatch, Arrays.asList(
                        Pair.of("listVariable", VariableTag.getTag(listVariableSource, listVariableNameText)),
                        Pair.of("listSize", listSize != null ? listSize.toString() : "null"),
                        Pair.of("subGroupCount", subGroupCount),
                        Pair.of("successCriteria", successCriteria),
                        Pair.of("entryVariable", VariableTag.getTag(VariableSource.Event, entryVariableNameText))
                ));
                if (diagnosticsPosition != -1) {
                    eventInfo.getDiagnostics().moveLast(diagnosticsPosition);
                }
            }
        }
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenRepeat> getType() {
        return WhenType.Repeat;
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
