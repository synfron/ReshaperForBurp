package synfron.reshaper.burp.core.utils;

import burp.BurpExtender;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.PrintWriter;

@SuppressWarnings("FieldCanBeLocal")
public class Log {

    private static PrintWriter errWriter;
    private static PrintWriter outWriter;
    private static final Gson gson = new Gson();
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
        BurpExtender.getCallbacks().printOutput(gson.toJson(this));
    }

    public void logErr() {
        BurpExtender.getCallbacks().printError(gson.toJson(this));
    }
}
