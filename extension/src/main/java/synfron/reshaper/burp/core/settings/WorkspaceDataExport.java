package synfron.reshaper.burp.core.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.vars.Variable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDataExport implements Serializable {
    private int version = 0;
    private List<Rule> rules = Collections.emptyList();
    private List<Rule> webSocketRules = Collections.emptyList();
    private List<Variable> variables = Collections.emptyList();

    public WorkspaceDataExport(Workspace workspace) {
        version = 1;
        rules = workspace.getHttpRulesRegistry().exportRules();
        webSocketRules = workspace.getWebSocketRulesRegistry().exportRules();
        variables = workspace.getGlobalVariables().exportVariables();
    }

    public WorkspaceDataExport(List<Rule> rules, List<Rule> webSocketRules, List<Variable> variables) {
        this.version = 1;
        this.rules = rules;
        this.webSocketRules = webSocketRules;
        this.variables = variables;
    }

    public void copyTo(Workspace workspace, boolean overwriteDuplicates) {
        workspace.getGlobalVariables().importVariables(variables, overwriteDuplicates);
        workspace.getHttpRulesRegistry().importRules(rules, overwriteDuplicates);
        workspace.getWebSocketRulesRegistry().importRules(webSocketRules, overwriteDuplicates);
    }
}
