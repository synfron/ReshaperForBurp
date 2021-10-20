package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.thens.entities.buildmessage.MessageSetter;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenBuildRequestMessage extends ThenBuildMessage<ThenBuildRequestMessage> {

    @Override
    protected String buildMessage(EventInfo eventInfo) {
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(BurpExtender.getCallbacks().getHelpers().stringToBytes(
                VariableString.getTextOrDefault(eventInfo, getStarterMessage(), "")
        ));
        for (MessageSetter messageSetter : getMessageSetters()) {
            MessageValueHandler.setRequestValue(eventInfo, httpRequestMessage, messageSetter.getMessageValue(), messageSetter.getIdentifier(), messageSetter.getText().getText(eventInfo));
        }
        return httpRequestMessage.getText();
    }

    @Override
    public RuleOperationType<ThenBuildRequestMessage> getType() {
        return ThenType.BuildRequestMessage;
    }
}
