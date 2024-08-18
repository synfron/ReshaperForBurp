package synfron.reshaper.burp.core.utils;

import burp.BurpExtender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import synfron.reshaper.burp.core.events.MessageArgs;
import synfron.reshaper.burp.core.events.message.LogMessage;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;

@SuppressWarnings("FieldCanBeLocal")
public class Log {
    private String message;
    private String exception;
    private Object payload;
    @JsonIgnore
    private final Workspace workspace;

    private Log(Workspace workspace) {
        this.workspace = workspace;
    }

    private Log() {
        this.workspace = null;
    }

    public static Log get(Workspace workspace) {
        return new Log(workspace);
    }

    public static Log get() {
        return get(Workspaces.get().getCurrentWorkspace());
    }

    public static Log getSystem() {
        return get(null);
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
        if (workspace == null || workspace.getGeneralSettings().isLogInExtenderOutput()) {
            if (isError) {
                BurpExtender.getApi().logging().logToError(text);
            } else {
                BurpExtender.getApi().logging().logToOutput(text);
            }
        }
        if (workspace != null) {
            printToDisplay(text);
        }
    }

    private void printToDisplay(String text) {
        if (workspace != null) {
            workspace.getMessageEvent().invoke(new MessageArgs(this, new LogMessage(text)));
        }
    }
}
