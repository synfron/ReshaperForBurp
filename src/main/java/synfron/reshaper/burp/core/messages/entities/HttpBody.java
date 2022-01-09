package synfron.reshaper.burp.core.messages.entities;

import synfron.reshaper.burp.core.messages.Encoder;

public class HttpBody extends HttpEntity {

    private String text;
    private final byte[] rawBytes;
    private final Encoder encoder;

    public HttpBody(byte[] rawBytes, Encoder encoder) {
        this.rawBytes = rawBytes;
        this.encoder = encoder;
    }

    public byte[] getValue() {
        return rawBytes;
    }

    public boolean hasValue() {
        return rawBytes.length > 0;
    }

    public String getText() {
        if (text == null) {
            text = encoder.decode(rawBytes);
        }
        return text;
    }

    @Override
    public boolean isChanged() {
        return false;
    }
}
