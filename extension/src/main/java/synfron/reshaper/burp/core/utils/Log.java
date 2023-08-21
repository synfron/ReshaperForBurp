package synfron.reshaper.burp.core.utils;

import burp.BurpExtender;
import burp.api.montoya.core.ByteArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.nio.charset.StandardCharsets;

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
        exception.printStackTrace();
        return this;
    }

    public void logRaw() {
        printOutput(message, false);
    }

    public void log() {
        try {
            printOutput(Serializer.serialize(this, true), false);
        } catch (Exception e) {
            payload = "Failed to log with original payload";
            printOutput(Serializer.serialize(this, true), false);
        }
    }

    public void logErr() {
        try {
            printOutput(Serializer.serialize(this, true), true);
        } catch (Exception e) {
            payload = "Failed to log with original payload";
            printOutput(Serializer.serialize(this, true), true);
        }
    }

    private void printOutput(String text, boolean isError) {
        if (BurpExtender.getGeneralSettings().isLogInExtenderOutput()) {
            if (isError) {
                BurpExtender.getApi().logging().logToError(text);
            } else {
                BurpExtender.getApi().logging().logToOutput(text);
            }
        }
        printToDisplay(text);
    }

    private void printToDisplay(String text) {
        if (BurpExtender.getLogTextEditor() != null) {
            BurpExtender.getLogTextEditor().setContents(BurpUtils.current.toByteArray(
                    TextUtils.bufferAppend(
                            new String(BurpExtender.getLogTextEditor().getContents().getBytes(), StandardCharsets.UTF_8),
                            text,
                            "\n",
                            BurpExtender.getGeneralSettings().getLogTabCharacterLimit()
                    ).getBytes(StandardCharsets.UTF_8)
            ));
        }
    }
}
