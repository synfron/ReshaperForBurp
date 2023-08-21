package synfron.reshaper.burp.core.rules.diagnostics;

import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.rules.whens.When;

import java.io.Serializable;
import java.util.List;

public interface IDiagnostics {
    int size();

    void moveLast(int position);

    void logCompare(When<?> when, List<? extends Pair<String, ? extends Serializable>> properties, MatchType matchType, Object matcher, Object value, boolean result);

    void logHas(When<?> when, Object value, Object subValue, boolean result);

    void logValue(When<?> when, boolean result, Object... values);

    void logProperties(When<?> when, boolean hasError, List<? extends Pair<String, ? extends Serializable>> properties);

    void logValue(Then<?> then, boolean hasError, Object... values);

    void logProperties(Then<?> then, boolean hasError, List<? extends Pair<String, ? extends Serializable>> properties);

    void logStart(Rule rule);

    void logEnd(Rule rule);

    void logGroupContainerStart();

    void logGroupContainerEnd();

    void logGroupStart(String name);

    void logGroupEnd(String name);

    void logStart(EventInfo eventInfo);

    void logEnd(EventInfo eventInfo);

    String getLogs();

    boolean isEnabled();

    boolean isEventEnabled();

    boolean isRuleEnabled();

    boolean hasLogs();

    void setRuleEnabled(boolean enabled);

    void setEventEnabled(boolean enabled);
}
