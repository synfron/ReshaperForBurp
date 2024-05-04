package synfron.reshaper.burp.core.rules.diagnostics;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.messages.WebSocketEventInfo;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.rules.whens.When;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Diagnostics implements IDiagnostics {
    private Integer diagnosticValueMaxLength;

    private List<DiagnosticRecord> records;
    @Getter @Setter
    private boolean ruleEnabled;
    @Getter @Setter
    private boolean eventEnabled;
    private DiagnosticRecord startRecord;
    private DiagnosticRecord endRecord;

    private static final CharSequenceTranslator EscapeChars;
    static {
        final Map<CharSequence, CharSequence> escapeCharsMap = Map.of(
                "'", "\\'",
                "\\", "\\\\",
                "\n", "\\n",
                "\r", "\\r",
                "\t", "\\t"
        );
        EscapeChars = new LookupTranslator(escapeCharsMap);
    }

    @Override
    public int size() {
        return records.size();
    }

    @Override
    public void moveLast(int position) {
        records.add(position, records.get(records.size() - 1));
        records.remove(records.size() - 1);
    }

    @Override
    public void logCompare(When<?> when, List<? extends Pair<String, ? extends Serializable>> properties, MatchType matchType, Object matcher, Object value, boolean result) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.When, String.format(
                "%-4sWhen %s(%s'%s' %s '%s') - %s\n",
                isLast(DiagnosticEntityType.When) ? toPrefix(when.isUseOrCondition()) : "",
                when.getType().getName(),
                properties != null && !properties.isEmpty() ? toPropertiesPhrase(properties) + ", " : "",
                toValuePhrase(value),
                toMatchPhrase(matchType, when.isNegate()),
                toValuePhrase(matcher),
                toResultPhrase(result, when.isNegate())
        )));
    }

    @Override
    public void logHas(When<?> when, Object value, Object subValue, boolean result) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.When, String.format(
                "%-4sWhen %s('%s' %s) - %s\n",
                isLast(DiagnosticEntityType.When) ? toPrefix(when.isUseOrCondition()) : "",
                when.getType().getName(),
                toValuePhrase(value, subValue),
                toExistsPhrase(when.isNegate()),
                toResultPhrase(result, when.isNegate())
        )));
    }

    @Override
    public void logValue(When<?> when, boolean result, Object... values) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.When, String.format(
                "%-4sWhen %s('%s') %s\n",
                isLast(DiagnosticEntityType.When) ? toPrefix(when.isUseOrCondition()) : "",
                when.getType().getName(),
                toValuePhrase(values),
                toResultPhrase(result, when.isNegate())
        )));
    }

    @Override
    public void logProperties(When<?> when, boolean result, List<? extends Pair<String, ? extends Serializable>> properties) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.Then, String.format(
                "%-4sWhen %s(%s) %s\n",
                isLast(DiagnosticEntityType.When) ? toPrefix(when.isUseOrCondition()) : "",
                when.getType().getName(),
                toPropertiesPhrase(properties),
                toResultPhrase(result, when.isNegate())
        )));
    }

    private boolean isLast(DiagnosticEntityType entityType) {
        List<DiagnosticRecord> records = getRecords();
        return !records.isEmpty() && records.get(records.size() - 1).getEntityType() == entityType;
    }

    @Override
    public void logValue(Then<?> then, boolean hasError, Object... values) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.Then, String.format(
                "%-4sThen %s('%s') %s\n",
                "",
                then.getType().getName(),
                toValuePhrase(values),
                toErroredPhrase(hasError)
        )));
    }

    @Override
    public void logProperties(Then<?> then, boolean hasError, List<? extends Pair<String, ? extends Serializable>> properties) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.Then, String.format(
                "%-4sThen %s(%s) %s\n",
                "",
                then.getType().getName(),
                toPropertiesPhrase(properties),
                toErroredPhrase(hasError)
        )));
    }

    private String toPropertiesPhrase(List<? extends Pair<String, ? extends Serializable>> properties) {
        return properties != null ? properties.stream()
                .filter(pair -> pair.getRight() != null)
                .map(pair -> String.format("%s='%s'", EscapeChars.translate(pair.getLeft()), toValuePhrase(pair.getRight())))
                .collect(Collectors.joining(" ")) : "";
    }

    @Override
    public void logStart(Rule rule) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.StartRule, String.format("Rule: %s\n", rule.getName())));
    }

    @Override
    public void logEnd(Rule rule) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.EndRule, "End Rule\n"));
    }

    @Override
    public void logGroupContainerStart() {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.StartGroupContainer,"\n"));
    }

    @Override
    public void logGroupContainerEnd() {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.EndGroupContainer, "\n"));
    }

    @Override
    public void logGroupStart(String name) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.StartGroup, name + "\n"));
    }

    @Override
    public void logGroupEnd(String name) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.EndGroup, name + "\n"));
    }

    @Override
    public void logStart(EventInfo eventInfo) {
        if (eventInfo instanceof HttpEventInfo httpEventInfo) {
            logStart(httpEventInfo);
        } else if (eventInfo instanceof WebSocketEventInfo<?> webSocketEventInfo) {
            logStart(webSocketEventInfo);
        }
    }

    @Override
    public void logEnd(EventInfo eventInfo) {
        if (eventInfo instanceof HttpEventInfo httpEventInfo) {
            logEnd(httpEventInfo);
        } else if (eventInfo instanceof WebSocketEventInfo<?> webSocketEventInfo) {
            logEnd(webSocketEventInfo);
        }
    }

    private void logStart(HttpEventInfo eventInfo) {
        startRecord = new DiagnosticRecord(DiagnosticEntityType.StartEvent, String.format("%s: %s\n", eventInfo.getInitialDataDirection(), eventInfo.getInitialHttpRequest().url()));
    }

    private void logStart(WebSocketEventInfo<?> eventInfo) {
        startRecord = new DiagnosticRecord(DiagnosticEntityType.StartEvent, String.format("%s: %s\n", eventInfo.getInitialDataDirection(), eventInfo.getInitialHttpRequest().url()));
    }

    private void logEnd(HttpEventInfo eventInfo) {
        endRecord = new DiagnosticRecord(DiagnosticEntityType.EndEvent, String.format("End %s\n", eventInfo.getDataDirection().toString()));
    }

    private void logEnd(WebSocketEventInfo<?> eventInfo) {
        endRecord = new DiagnosticRecord(DiagnosticEntityType.EndEvent, String.format("End %s\n", eventInfo.getDataDirection().toString()));
    }

    @Override
    public String getLogs() {
        StringBuilder buffer = new StringBuilder();
        if (records != null) {
            int indent = 1;
            buffer.append(startRecord.getLog());
            DiagnosticEntityType lastEntity = null;
            for (DiagnosticRecord record : records) {
                if (record.getEntityType() != null) {
                    if (lastEntity != null && record.getEntityType().getLevel() != lastEntity.getLevel()) {
                        if (lastEntity.getLevelChange() > 0 || record.getEntityType().getLevelChange() < 0) {
                            indent += record.getEntityType().getLevelChange() * 3;
                        } else {
                            indent += lastEntity.getLevelChange() * 3;
                        }
                    }
                    lastEntity = record.getEntityType();
                }
                buffer.append("\t".repeat(indent)).append(record.getLog());
            }
            buffer.append(endRecord.getLog());
        }
        return buffer.toString();
    }

    @Override
    public boolean isEnabled() {
        return ruleEnabled || eventEnabled;
    }

    @Override
    public boolean hasLogs() {
        return records != null;
    }

    private List<DiagnosticRecord> getRecords() {
        return records != null ? records : (records = new ArrayList<>());
    }

    private String toExistsPhrase(boolean negate) {
        return !negate ? "is present" : "is empty or missing";
    }

    private String toErroredPhrase(boolean hasError) {
        return hasError ? "- ERRORED" : "";
    }

    private String toPrefix(boolean isOr) {
        return isOr ? "OR" : "AND";
    }

    private String toValuePhrase(Object... values) {
        return EscapeChars.translate(StringUtils.stripEnd(Arrays.stream(values)
                .map(value -> StringUtils.abbreviate(Objects.toString(value, ""), getDiagnosticValueMaxLength()))
                .collect(Collectors.joining(":")), ":"));
    }

    private String toMatchPhrase(MatchType matchType, boolean negated) {
        if (!negated) {
            return switch (matchType) {
                case Equals -> "equals";
                case Contains -> "contains";
                case EndsWith -> "ends with";
                case BeginsWith -> "begins with";
                case Regex -> "is matched by";
            };
        } else {
            return switch (matchType) {
                case Equals -> "does not equal";
                case Contains -> "does not contain";
                case EndsWith -> "does not end with";
                case BeginsWith -> "does not begin with";
                case Regex -> "is not matched by";
            };
        }
    }

    private String toResultPhrase(boolean result, boolean negated) {
        return result == !negated ? "PASS" : "FAIL";
    }

    private int getDiagnosticValueMaxLength() {
        if (diagnosticValueMaxLength == null) {
            diagnosticValueMaxLength = BurpExtender.getGeneralSettings().getDiagnosticValueMaxLength();
        }
        return diagnosticValueMaxLength;
    }
}
