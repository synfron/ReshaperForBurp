package synfron.reshaper.burp.core;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.sessions.ActionResult;
import burp.api.montoya.http.sessions.SessionHandlingAction;
import burp.api.montoya.http.sessions.SessionHandlingActionData;
import lombok.Setter;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.core.utils.Log;

import java.util.List;

public class SessionHandlingActionExchange implements SessionHandlingAction {

    private final Workspaces workspaces;

    public SessionHandlingActionExchange(Workspaces workspaces) {
        this.workspaces = workspaces;
    }

    @Override
    public String name() {
        return "Reshaper";
    }

    @Override
    public ActionResult performAction(SessionHandlingActionData sessionHandlingActionData) {
        TransientSessionHandlingActionData actionData = new TransientSessionHandlingActionData(sessionHandlingActionData);
        for (Workspace workspace : workspaces.getWorkspaces()) {
            try {
                ActionResult actionResult = workspace.getHttpConnector().performAction(actionData);
                if (actionResult.request() != null) {
                    actionData.setHttpRequest(actionResult.request());
                }
                if (actionResult.annotations() != null) {
                    actionData.setAnnotations(actionResult.annotations());
                }
            } catch (Exception e) {
                Log.get(workspace).withMessage("Failed processing Session event").withException(e).logErr();
            }
        }
        return ActionResult.actionResult(actionData.httpRequest);
    }

    @Setter
    private static class TransientSessionHandlingActionData implements SessionHandlingActionData {

        private HttpRequest httpRequest;
        private List<HttpRequestResponse> macroRequestResponses;
        private Annotations annotations;

        public TransientSessionHandlingActionData(SessionHandlingActionData sessionHandlingActionData) {
            httpRequest = sessionHandlingActionData.request();
            macroRequestResponses = sessionHandlingActionData.macroRequestResponses();
            annotations = sessionHandlingActionData.annotations();
        }

        @Override
        public HttpRequest request() {
            return httpRequest;
        }

        @Override
        public List<HttpRequestResponse> macroRequestResponses() {
            return macroRequestResponses;
        }

        @Override
        public Annotations annotations() {
            return annotations;
        }
    }
}
