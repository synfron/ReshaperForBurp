package synfron.reshaper.burp.core.rules.diagnostics;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.rules.whens.When;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Diagnostics {
    private Integer diagnosticValueMaxLength;

    private List<DiagnosticRecord> records;
    @Getter @Setter
    private boolean enabled;

    public void logCompare(When<?> when, MatchType matchType, Object matcher, Object subMatcher, Object value, boolean result) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.When, String.format(
                "%-4sWhen %s('%s' %s '%s') - %s\n",
                isLast(DiagnosticEntityType.When) ? toPrefix(when.isUseOrCondition()) : "",
                when.getType().getName(),
                toValuePhrase(value),
                toMatchPhrase(matchType, when.isNegate()),
                toValuePhrase(matcher, subMatcher),
                toResultPhrase(result, when.isNegate())
        )));
    }

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

    private boolean isLast(DiagnosticEntityType entityType) {
        List<DiagnosticRecord> records = getRecords();
        return records.size() > 0 && records.get(records.size() - 1).getEntityType() == entityType;
    }

    public void logValue(Then<?> then, boolean hasError, Object... values) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.Then, String.format(
                "%-4sThen %s('%s') %s\n",
                "",
                then.getType().getName(),
                toValuePhrase(values),
                hasError ? "Errored" : ""
        )));
    }

    public void logProperties(Then<?> then, boolean hasError, List<? extends Pair<String,? extends Serializable>> properties) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.Then, String.format(
                "%-4sThen %s(%s) %s\n",
                "",
                then.getType().getName(),
                properties.stream()
                        .filter(pair -> pair.getRight() != null)
                        .map(pair -> String.format("%s='%s'", pair.getLeft(), toValuePhrase(pair.getRight())))
                        .collect(Collectors.joining(" ")),
                hasError ? "Errored" : ""
        )));
    }

    public void logStart(Rule rule) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.StartRule, String.format("Rule: %s\n", rule.getName())));
    }

    public void logEnd(Rule rule) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.EndRule, String.format("End Rule\n")));
    }

    public void logStart(EventInfo eventInfo) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.StartEvent, String.format("%s: %s\n", eventInfo.getDataDirection(), eventInfo.getUrl())));
    }

    public void logEnd(EventInfo eventInfo) {
        getRecords().add(new DiagnosticRecord(DiagnosticEntityType.EndEvent, String.format("End %s\n", eventInfo.getDataDirection().toString())));
    }

    public String getLogs() {
        StringBuilder buffer = new StringBuilder();
        if (records != null) {
            int indent = 0;
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
                buffer.append("\t".repeat(indent) + record.getLog());
            }
        }
        return buffer.toString();
    }

    private List<DiagnosticRecord> getRecords() {
        return records != null ? records : (records = new ArrayList<>());
    }

    private String toExistsPhrase(boolean negate) {
        return !negate ? "is present" : "is empty or missing";
    }

    private String toPrefix(boolean isOr) {
        return isOr ? "OR" : "AND";
    }

    private String toValuePhrase(Object... values) {
        return StringUtils.stripEnd(Arrays.stream(values)
                .map(value -> StringUtils.abbreviate(Objects.toString(value, ""), getDiagnosticValueMaxLength()))
                .collect(Collectors.joining(":")), ":");
    }

    private String toMatchPhrase(MatchType matchType, boolean negated) {
        if (!negated) {
            switch (matchType) {
                case Equals:
                    return "equals";
                case Contains:
                    return "contains";
                case EndsWith:
                    return "ends with";
                case BeginsWith:
                    return "begins with";
                case Regex:
                    return "is matched by";
                default:
                    return "does " + matchType.toString().toLowerCase();
            }
        } else {
            switch (matchType) {
                case Equals:
                    return "does not equal";
                case Contains:
                    return "does not contain";
                case EndsWith:
                    return "does not end with";
                case BeginsWith:
                    return "does not begin with";
                case Regex:
                    return "is not matched by";
                default:
                    return "not " + matchType.toString().toLowerCase();
            }
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
