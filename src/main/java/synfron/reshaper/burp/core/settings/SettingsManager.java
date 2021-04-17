package synfron.reshaper.burp.core.settings;

import burp.BurpExtender;
import com.fasterxml.jackson.core.type.TypeReference;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.RulesRegistry;
import synfron.reshaper.burp.core.utils.Serializer;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class SettingsManager {

    public void importSettings(File file, boolean overrideDuplicates) {
        try {
            ExportSettings exportSettings = Serializer.deserialize(
                    Files.readString(file.toPath()),
                    new TypeReference<>() {}
            );
            getGlobalVariables().importVariables(exportSettings.getVariables(), overrideDuplicates);
            getRulesRegistry().importRules(exportSettings.getRules(), overrideDuplicates);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public void exportSettings(File file, List<Variable> variables, List<Rule> rules) {
        try {
            ExportSettings exportSettings = new ExportSettings();
            exportSettings.setVariables(variables);
            exportSettings.setRules(rules);
            Files.writeString(file.toPath(), Serializer.serialize(exportSettings, false));
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public void loadSettings() {
        BurpExtender.getGeneralSettings().importSettings(Storage.get("Reshaper.generalSettings", new TypeReference<>() {}));
        getGlobalVariables().importVariables(Storage.get("Reshaper.variables", new TypeReference<>() {}), false);
        getRulesRegistry().importRules(Storage.get("Reshaper.rules", new TypeReference<>() {}), false);
    }

    public void saveSettings() {
        Storage.store("Reshaper.generalSettings", BurpExtender.getGeneralSettings());
        Storage.store("Reshaper.variables", getGlobalVariables().exportVariables());
        Storage.store("Reshaper.rules", getRulesRegistry().exportRules());
    }

    private GlobalVariables getGlobalVariables() {
        return GlobalVariables.get();
    }

    private RulesRegistry getRulesRegistry() {
        return BurpExtender.getConnector().getRulesEngine().getRulesRegistry();
    }
}