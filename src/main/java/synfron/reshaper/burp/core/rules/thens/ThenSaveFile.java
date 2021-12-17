package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.savefile.FileExistsAction;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThenSaveFile extends Then<ThenSaveFile> {
    @Getter @Setter
    private VariableString filePath;
    @Getter @Setter
    private VariableString text;
    @Getter @Setter
    private VariableString encoding;
    @Getter @Setter
    private FileExistsAction fileExistsAction = FileExistsAction.Overwrite;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = true;
        String filePathValue = null;
        String textValue = null;
        String encodingValue = null;
        boolean fileExists = false;
        try {
            filePathValue = filePath.getText(eventInfo);
            Path path = Paths.get(filePathValue);
            fileExists = Files.isRegularFile(path);
            if ((fileExists && Files.isWritable(path)) || (!Files.isDirectory(path) && Files.notExists(path))) {
                if (!fileExists || fileExistsAction != FileExistsAction.None) {
                    textValue = VariableString.getTextOrDefault(eventInfo, text, "");
                    encodingValue = VariableString.getTextOrDefault(eventInfo, encoding, Charset.defaultCharset().name());
                    FileUtils.write(path.toFile(), textValue, encodingValue, fileExistsAction == FileExistsAction.Append);
                }
                hasError = false;
            }
        } catch (IOException e) {
            throw new WrappedException(e);
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, List.of(
                    Pair.of("filePath", filePathValue),
                    Pair.of("text", textValue),
                    Pair.of("encoding", encodingValue),
                    Pair.of("fileExists", fileExists),
                    Pair.of("fileExistsAction", fileExistsAction)
            ));
        }
        return !hasError ? RuleResponse.Continue : RuleResponse.BreakRules;
    }

    @Override
    public RuleOperationType<ThenSaveFile> getType() {
        return ThenType.SaveFile;
    }
}
