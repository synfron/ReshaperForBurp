package synfron.reshaper.burp.core.settings;

import burp.BurpExtender;
import com.fasterxml.jackson.core.type.TypeReference;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.RulesRegistry;
import synfron.reshaper.burp.core.utils.Serializer;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class SettingsManager {

    public static void importSettings(File file, boolean overwriteDuplicates) {
        try {
            importSettings(Files.readString(file.toPath()), overwriteDuplicates);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static void importSettings(String settingsJson, boolean overwriteDuplicates) {
        ExportSettings exportSettings = Serializer.deserialize(
                settingsJson,
                new TypeReference<>() {}
        );
        getGlobalVariables().importVariables(exportSettings.getVariables(), overwriteDuplicates);
        getHttpRulesRegistry().importRules(exportSettings.getRules(), overwriteDuplicates);
        getWebSocketRulesRegistry().importRules(exportSettings.getWebSocketRules(), overwriteDuplicates);
    }

    public static void exportSettings(File file, List<Variable> variables, List<Rule> httpRules, List<Rule> webSocketRules) {
        try {
            ExportSettings exportSettings = new ExportSettings();
            exportSettings.setVariables(variables);
            exportSettings.setRules(httpRules);
            exportSettings.setWebSocketRules(webSocketRules);
            String data = switch (BurpExtender.getGeneralSettings().getExportMethod()) {
                case Json -> Serializer.serialize(exportSettings, false);
                case Yaml -> Serializer.serializeYaml(exportSettings, false);
            };
            Files.writeString(file.toPath(), data);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static void loadSettings() {
        BurpExtender.getGeneralSettings().importSettings(Storage.get("Reshaper.generalSettings", new TypeReference<>() {}));
        getGlobalVariables().importVariables(Storage.get("Reshaper.variables", new TypeReference<>() {}), false);
        getHttpRulesRegistry().importRules(Storage.get("Reshaper.rules", new TypeReference<>() {}), false);
        getWebSocketRulesRegistry().importRules(Storage.get("Reshaper.webSocketRules", new TypeReference<>() {}), false);
    }

    public static void saveSettings() {
        Storage.store("Reshaper.generalSettings", BurpExtender.getGeneralSettings());
        Storage.store("Reshaper.variables", getGlobalVariables().exportVariables());
        Storage.store("Reshaper.rules", getHttpRulesRegistry().exportRules());
        Storage.store("Reshaper.webSocketRules", getWebSocketRulesRegistry().exportRules());
    }

    private static GlobalVariables getGlobalVariables() {
        return GlobalVariables.get();
    }

    private static RulesRegistry getHttpRulesRegistry() {
        return BurpExtender.getRulesRegistry(ProtocolType.Http);
    }

    private static RulesRegistry getWebSocketRulesRegistry() {
        return BurpExtender.getRulesRegistry(ProtocolType.WebSocket);
    }

    public static void resetData() {
        Arrays.stream(getHttpRulesRegistry().getRules()).forEach(rule -> getHttpRulesRegistry().deleteRule(rule));
        Arrays.stream(getWebSocketRulesRegistry().getRules()).forEach(rule -> getWebSocketRulesRegistry().deleteRule(rule));
        GlobalVariables.get().getValues().forEach(variable -> GlobalVariables.get().remove(variable.getName()));
    }
}