package synfron.reshaper.burp.core.settings;

import com.fasterxml.jackson.core.type.TypeReference;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.rules.RulesRegistry;
import synfron.reshaper.burp.core.utils.Serializer;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variables;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class SettingsManager {

    public static void importSettings(Workspace workspace, File file, boolean overwriteDuplicates) {
        try {
            importSettings(workspace, Files.readString(file.toPath()), overwriteDuplicates);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static void importSettings(Workspace workspace, String settingsJson, boolean overwriteDuplicates) {
        WorkspaceDataExport workspaceExport = Serializer.deserialize(
                settingsJson,
                new TypeReference<>() {}
        );
        workspaceExport.copyTo(workspace, overwriteDuplicates);
    }

    public static void exportWorkspaceData(Workspace workspace, File file, WorkspaceDataExport workspaceExport) {
        try {
            String data = switch (workspace.getGeneralSettings().getExportMethod()) {
                case Json -> Serializer.serialize(workspaceExport, false);
                case Yaml -> Serializer.serializeYaml(workspaceExport, false);
            };
            Files.writeString(file.toPath(), data);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static Workspaces loadPersistentWorkspaces() {
        WorkspacesExport workspacesExport = Storage.get("Reshaper.workspaces", new TypeReference<>() {});
        if (workspacesExport != null) {
            return workspacesExport.toWorkspaces();
        }

        Workspaces workspaces = new Workspaces();
        workspaces.initialize();
        loadLegacySettings(workspaces.getWorkspaces().getFirst());
        return workspaces;
    }

    private static void loadLegacySettings(Workspace workspace) {
        workspace.getGeneralSettings().importSettings(Storage.get("Reshaper.generalSettings", new TypeReference<>() {}));
        workspace.getGlobalVariables().importVariables(Storage.get("Reshaper.variables", new TypeReference<>() {}), false);
        workspace.getHttpRulesRegistry().importRules(Storage.get("Reshaper.rules", new TypeReference<>() {}), false);
        workspace.getWebSocketRulesRegistry().importRules(Storage.get("Reshaper.webSocketRules", new TypeReference<>() {}), false);
    }

    public static void savePersistentWorkspaces(WorkspacesExport workspacesExport) {
        Storage.store("Reshaper.workspaces", workspacesExport);
    }

    public static void resetData(Workspace workspace) {
        RulesRegistry httpRulesRegistry = workspace.getHttpRulesRegistry();
        RulesRegistry webSocketRulesRegistry = workspace.getWebSocketRulesRegistry();
        GlobalVariables globalVariables = workspace.getGlobalVariables();
        Arrays.stream(httpRulesRegistry.getRules()).forEach(httpRulesRegistry::deleteRule);
        Arrays.stream(webSocketRulesRegistry.getRules()).forEach(webSocketRulesRegistry::deleteRule);
        globalVariables.getValues().forEach(variable -> globalVariables.remove(Variables.asKey(variable.getName(), variable.isList())));
    }
}