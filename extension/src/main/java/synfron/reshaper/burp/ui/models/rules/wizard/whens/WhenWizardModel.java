package synfron.reshaper.burp.ui.models.rules.wizard.whens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.*;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.RulesRegistry;
import synfron.reshaper.burp.core.rules.whens.*;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.utils.IPrompterModel;
import synfron.reshaper.burp.ui.utils.ModalPrompter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
public class WhenWizardModel implements IPrompterModel<WhenWizardModel> {
    private final EventInfo eventInfo;
    private String ruleName;
    private final List<WhenWizardItemModel> items = new ArrayList<>();

    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    private boolean invalidated;
    private boolean dismissed;

    @Setter
    private ModalPrompter<WhenWizardModel> modalPrompter;

    public WhenWizardModel(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public void resetPropertyChangedListener() {
        propertyChangedEvent.clearListeners();
        items.forEach(item -> item.getPropertyChangedEvent().clearListeners());
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
        propertyChanged("ruleName", ruleName);
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public WhenWizardModel withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(ruleName)) {
            errors.add("Rule Name is required");
        }
        errors.addAll(items.stream().flatMap(item -> item.validate().stream()).toList());
        return errors;
    }

    public WhenWizardItemModel addItem() {
        WhenWizardItemModel item = new WhenWizardItemModel(MessageValue.Url, eventInfo);
        items.add(item);
        propertyChanged("items", items);
        return item;
    }

    private WhenHasEntity toHasEntity(WhenWizardItemModel item) {
        WhenHasEntity when = new WhenHasEntity();
        when.setMessageValue(item.getMessageValue());
        when.setIdentifier(VariableString.getAsVariableString(item.getIdentifiers().getSelectedOption(), false));
        return when;
    }

    private WhenMatchesText toMatchesText(WhenWizardItemModel item) {
        WhenMatchesText when = new WhenMatchesText();
        when.setMatchType(MatchType.valueOf(item.getMatchType().name()));
        when.setMessageValue(item.getMessageValue());
        when.setIdentifier(VariableString.getAsVariableString(item.getIdentifiers().getSelectedOption(), false));
        when.setIdentifierPlacement(GetItemPlacement.Last);
        when.setUseMessageValue(true);
        when.setMatchText(VariableString.getAsVariableString(item.getText()));
        return when;
    }

    public int removeItem(WhenWizardItemModel whenWizardItemModel) {
        int index = items.indexOf(whenWizardItemModel);
        items.remove(index);
        propertyChanged("items", items);
        return index;
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
        propertyChanged("invalidated", invalidated);
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
        propertyChanged("dismissed", dismissed);
    }

    public boolean createRule() {
        if (validate().isEmpty()) {
            Rule rule = new Rule();
            LinkedList<When<?>> whens = new LinkedList<>();
            boolean requiresResponse = false;
            for (WhenWizardItemModel item : items) {
                requiresResponse |= item.requiresResponse();
                if (item.getMatchType().isMatcher()) {
                    WhenMatchesText when = toMatchesText(item);
                    whens.addLast(when);
                } else {
                    WhenHasEntity when = toHasEntity(item);
                    whens.addLast(when);
                }
            }
            RulesRegistry rulesRegistry = null;
            if (eventInfo instanceof HttpEventInfo) {
                WhenEventDirection direction = new WhenEventDirection();
                direction.setDataDirection(requiresResponse ? HttpDataDirection.Response : HttpDataDirection.Request);
                whens.addFirst(direction);
                rulesRegistry = eventInfo.getWorkspace().getHttpConnector().getRulesEngine().getRulesRegistry();
            } else if (eventInfo instanceof WebSocketEventInfo<?> webSocketEventInfo) {
                WhenWebSocketEventDirection direction = new WhenWebSocketEventDirection();
                direction.setDataDirection(webSocketEventInfo.getDataDirection());
                whens.addFirst(direction);
                rulesRegistry = eventInfo.getWorkspace().getWebSocketConnector().getRulesEngine().getRulesRegistry();
            }

            if (rulesRegistry != null) {
                rule.setName(ruleName);
                rule.setEnabled(false);
                rule.setAutoRun(true);
                rule.setWhens(whens.stream().toList());
                rulesRegistry.addRule(rule);
            }
            setInvalidated(false);
            return true;
        }
        setInvalidated(true);
        return false;
    }
}
