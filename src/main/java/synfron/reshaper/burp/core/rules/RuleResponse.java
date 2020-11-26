package synfron.reshaper.burp.core.rules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class RuleResponse {
    public static final RuleResponse Continue = new RuleResponse("Continue", 1);
    public static final RuleResponse BreakThens = new RuleResponse("Skip Next Thens", 2);
    public static final RuleResponse BreakRules = new RuleResponse("Skip Next Rules", 4);

    private static final List<RuleResponse> values = List.of(
        Continue,
        BreakThens,
        BreakRules
    );

    @Getter
    private final String name;
    private final int flags;

    @JsonCreator
    private RuleResponse(@JsonProperty("name") String name, @JsonProperty("flags") int flags) {
        this.name = name;
        this.flags = flags;
    }

    private RuleResponse(int flags) {
        name = getValues().stream().map(RuleResponse::getName).collect(Collectors.joining(", "));
        this.flags = flags;
    }

    public static List<RuleResponse> getValues() {
        return values;
    }

    public boolean hasFlags(RuleResponse ruleResponse) {
        return hasFlags(ruleResponse.flags);
    }

    private boolean hasFlags(int flags) {
        return (this.flags & flags) == flags;
    }

    public RuleResponse or(RuleResponse ruleResponse) {
        int newFlag = flags | ruleResponse.flags;
        return new RuleResponse(newFlag);
    }

    @Override
    public String toString() {
       return name;
    }
}
