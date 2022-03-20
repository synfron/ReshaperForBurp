package synfron.reshaper.burp.core.rules;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.*;
import synfron.reshaper.burp.core.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RulesRegistry {
    @Getter
    private transient int version;

    private final IEventListener<PropertyChangedArgs> rulePropertyChangedListener = this::onRulePropertyChanged;

    private Rule[] rules = new Rule[0];

    public Rule[] getRules() {
        return rules;
    }

    @Getter
    private final CollectionChangedEvent collectionChangedEvent = new CollectionChangedEvent();

    public synchronized void deleteRule(Rule rule) {
        int index = ArrayUtils.indexOf(rules, rule);
        if (index >= 0) {
            rules = ArrayUtils.remove(rules, index);
            version++;
            collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Remove, index, rule));
        }
    }

    public synchronized void addRule(Rule rule) {
        rules = ArrayUtils.add(rules, rule);
        version++;
        collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Add, rules.length - 1, rule));
        rule.getPropertyChangedEvent().add(rulePropertyChangedListener);
    }

    private void onRulePropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        Rule rule = (Rule)propertyChangedArgs.getSource();
        int index = ArrayUtils.indexOf(rules, rule);
        if (index >= 0) {
            version++;
            collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Update, index, rule));
        }
    }

    public synchronized void movePrevious(Rule rule)
    {
        if (rule != null) {
            int currentIndex = ArrayUtils.indexOf(rules, rule);
            if (currentIndex > 0) {
                rules = CollectionUtils.move(rules, rule, currentIndex, --currentIndex);
                version++;
                collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Move, currentIndex + 1, currentIndex, rule));
            }
        }
    }

    public synchronized void moveNext(Rule rule)
    {
        if (rule != null)
        {
            int currentIndex = ArrayUtils.indexOf(rules, rule);
            if (currentIndex < rules.length - 1)
            {
                rules = CollectionUtils.move(rules, rule, currentIndex, ++currentIndex);
                version++;
                collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Move, currentIndex - 1, currentIndex, rule));
            }
        }
    }

    public void importRules(List<Rule> rules, boolean overwriteDuplicates) {
        rules = ObjectUtils.defaultIfNull(rules, Collections.emptyList());
        Set<String> existingRules = Stream.of(this.rules).map(Rule::toString).collect(Collectors.toSet());
        Set<String> newRules = rules.stream().map(Rule::toString).collect(Collectors.toSet());
        this.rules = Stream.concat(
                Stream.of(this.rules)
                        .filter(rule -> !overwriteDuplicates || !newRules.contains(rule.getName())),
                rules.stream()
                        .filter(rule -> overwriteDuplicates || !existingRules.contains(rule.getName()))
                        .map(rule -> rule.withListener(rulePropertyChangedListener))
        ).toArray(Rule[]::new);
        version++;
        collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Reset));
    }

    public List<Rule> exportRules() {
        return Stream.of(this.rules).filter(rule -> StringUtils.isNotEmpty(rule.getName())).collect(Collectors.toList());
    }
}
