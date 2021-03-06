package synfron.reshaper.burp.core.utils;

import burp.BurpExtender;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@SuppressWarnings("FieldCanBeLocal")
public class Log {
    private String message;
    private String exception;
    private Object payload;

    private Log() {}

    public static Log get() {
        return new Log();
    }

    public Log withMessage(String message) {
        this.message = message;
        return this;
    }

    public Log withPayload(Object payload) {
        this.payload = payload;
        return this;
    }


    public Log withException(Exception exception) {
        this.exception = String.format(
                "%s\n%s",
                StringUtils.defaultString(exception.getMessage(), ""),
                ExceptionUtils.getStackTrace(exception)
        );
        return this;
    }

    public void log() {
        try {
            BurpExtender.getCallbacks().printOutput(Serializer.serialize(this, true));
        } catch (Exception e) {
            payload = "Failed to log with original payload";
            BurpExtender.getCallbacks().printOutput(Serializer.serialize(this, true));
        }
    }

    public void logErr() {
        try {
            BurpExtender.getCallbacks().printError(Serializer.serialize(this, true));
        } catch (Exception e) {
            payload = "Failed to log with original payload";
            BurpExtender.getCallbacks().printOutput(Serializer.serialize(this, true));
        }
    }
}
