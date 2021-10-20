package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.thens.entities.buildmessage.MessageSetter;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenBuildResponseMessage extends ThenBuildMessage<ThenBuildResponseMessage> {

    @Override
    protected String buildMessage(EventInfo eventInfo) {
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(BurpExtender.getCallbacks().getHelpers().stringToBytes(
                VariableString.getTextOrDefault(eventInfo, getStarterMessage(), "")
        ));
        for (MessageSetter messageSetter : getMessageSetters()) {
            MessageValueHandler.setResponseValue(eventInfo, httpResponseMessage, messageSetter.getMessageValue(), messageSetter.getIdentifier(), messageSetter.getText().getText(eventInfo));
        }
        return httpResponseMessage.toString();
    }

    @Override
    public RuleOperationType<ThenBuildResponseMessage> getType() {
        return ThenType.BuildResponseMessage;
    }
}
