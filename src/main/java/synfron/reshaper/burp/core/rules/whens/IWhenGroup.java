package synfron.reshaper.burp.core.rules.whens;

import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.CollectionUtils;

import java.util.List;

public interface IWhenGroup {

    default boolean isMatch(List<? extends When<?>> whens, EventInfo eventInfo) {
        boolean  isMatch = true;
        boolean  first = true;
        for (int whenIndex = 0; whenIndex < whens.size(); whenIndex++) {
            When<?> when = whens.get(whenIndex);
            if (!isMatch && !when.isUseOrCondition())
            {
                break;
            }
            if (when.isUseOrCondition() && !first)
            {
                isMatch |= isMatch(when, whens, whenIndex, eventInfo) == !when.isNegate();
            }
            else
            {
                isMatch &= isMatch(when, whens, whenIndex, eventInfo) == !when.isNegate();
            }
            first = false;
            whenIndex += when.groupSize();
        }
        return isMatch;
    }

    private static boolean isMatch(When<?> when, List<? extends When<?>> whens, int whenIndex, EventInfo eventInfo) {
        return when.isGroup() && when instanceof IWhenGroup whenGroup ?
                whenGroup.isMatch(
                        CollectionUtils.subList(whens, whenIndex + 1, when.groupSize()),
                        eventInfo
                ) :
                when.isMatch(eventInfo);
    }


}
