package synfron.reshaper.burp.core.rules.thens;

import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.CollectionUtils;

import java.util.List;

public interface IThenGroup {

    default RuleResponse perform(List<? extends Then<?>> thens, EventInfo eventInfo) {
        RuleResponse thenResult = RuleResponse.Continue;
        for (int thenIndex = 0; thenIndex < thens.size(); thenIndex++) {
            Then<?> then = thens.get(thenIndex);
            RuleResponse result = then.isGroup() && then instanceof IThenGroup thenGroup ?
                    thenGroup.perform(
                            CollectionUtils.subList(thens, thenIndex + 1, then.groupSize()),
                            eventInfo
                    ) :
                    then.perform(eventInfo);
            thenResult = thenResult.or(result);
            if (result.hasFlags(RuleResponse.BreakThens) || result.hasFlags(RuleResponse.BreakRules))
            {
                break;
            }
            thenIndex += then.groupSize();
        }
        return thenResult;
    }

}
