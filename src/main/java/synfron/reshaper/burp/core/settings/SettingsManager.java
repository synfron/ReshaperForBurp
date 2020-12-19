package synfron.reshaper.burp.core.settings;

import burp.BurpExtender;
import com.fasterxml.jackson.core.type.TypeReference;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.utils.Serializer;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SettingsManager {
    public void importSettings(File file, boolean overrideDuplicates) {
        try {
            ExportSettings exportSettings = Serializer.deserialize(
                    Files.readString(file.toPath()),
                    new TypeReference<>() {}
            );
            GlobalVariables.get().importVariables(exportSettings.getVariables(), overrideDuplicates);
            BurpExtender.getConnector().getRulesEngine().getRulesRegistry().importRules(exportSettings.getRules(), overrideDuplicates);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public void exportSettings(File file, List<Variable> variables, List<Rule> rules) {
        try {
            ExportSettings exportSettings = new ExportSettings();
            exportSettings.setVariables(variables);
            exportSettings.setRules(rules);
            Files.writeString(file.toPath(), Serializer.serialize(exportSettings));
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }
}