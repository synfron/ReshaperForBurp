package synfron.reshaper.burp.core.messages.entities;

import burp.BurpExtender;
import lombok.Getter;

public class HttpBody extends HttpEntity {

    private String text;
    private final byte[] rawBytes;
    @Getter
    private boolean changed;

    public HttpBody(byte[] rawBytes) {
        this.rawBytes = rawBytes;
    }

    public byte[] getValue() {
        return !isChanged() ? rawBytes : BurpExtender.getCallbacks().getHelpers().stringToBytes(text);
    }

    public boolean hasValue() {
        return !isChanged() ? rawBytes.length > 0 : text.length() > 0;
    }

    public String getText() {
        if (text == null) {
            text = BurpExtender.getCallbacks().getHelpers().bytesToString(rawBytes);
        }
        return text;
    }
}
