package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.savefile.FileExistsAction;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ThenReadFile extends Then<ThenReadFile> implements IHttpRuleOperation, IWebSocketRuleOperation {

    @Getter @Setter
    private VariableString filePath;
    @Getter @Setter
    private VariableString encoding;
    @Getter @Setter
    private boolean breakAfterFailure = true;
    @Getter @Setter
    private boolean captureAfterFailure;
    @Getter @Setter
    private VariableSource captureVariableSource = VariableSource.Global;
    @Getter @Setter
    private VariableString captureVariableName;
    @Getter @Setter
    private SetListItemPlacement itemPlacement = SetListItemPlacement.Index;
    @Getter @Setter
    private VariableString delimiter;
    @Getter @Setter
    private VariableString index;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = false;
        boolean failed = true;
        String captureVariableName = null;
        String filePathValue = null;
        String fileText = "";
        String encodingValue = null;
        try {
            captureVariableName = getVariableName(eventInfo);
            filePathValue = filePath.getText(eventInfo);
            try {
                Path path = Paths.get(filePathValue);
                if (Files.isRegularFile(path) && Files.isReadable(path)) {
                    encodingValue = VariableString.getTextOrDefault(eventInfo, encoding, Charset.defaultCharset().name());
                    Encoder encoder = new Encoder(encodingValue);
                    fileText = encoder.decode(FileUtils.readFileToByteArray(path.toFile()));
                    failed = false;
                }
            } catch (Exception ignored) {
            } finally {
                if (!failed || captureAfterFailure) {
                    setVariable(
                            captureVariableSource,
                            eventInfo,
                            captureVariableName,
                            itemPlacement,
                            VariableString.getTextOrDefault(eventInfo, delimiter, "\n"),
                            VariableString.getIntOrDefault(eventInfo, index, 0),
                            fileText
                    );
                }
            }
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, List.of(
                    Pair.of("filePath", filePathValue),
                    Pair.of("text", fileText),
                    Pair.of("encoding", encodingValue),
                    Pair.of("captureVariableSource", captureVariableSource),
                    Pair.of("captureVariableName", captureVariableName),
                    Pair.of("itemPlacement", captureVariableSource.isList() ? itemPlacement : null),
                    Pair.of("delimiter", captureVariableSource.isList() && itemPlacement.isHasDelimiterSetter() ? VariableString.getTextOrDefault(eventInfo, delimiter, null) : null),
                    Pair.of("index", captureVariableSource.isList() && itemPlacement.isHasIndexSetter() ? VariableString.getTextOrDefault(eventInfo, index, null) : null),
                    Pair.of("failed", failed)
            ));
        }
        return failed && breakAfterFailure ? RuleResponse.BreakRules : RuleResponse.Continue;
    }

    private String getVariableName(EventInfo eventInfo) {
        String captureVariableName;
        if (StringUtils.isEmpty(captureVariableName = this.captureVariableName.getText(eventInfo))) {
            throw new IllegalArgumentException("Invalid variable name");
        }
        return captureVariableName;
    }

    @Override
    public RuleOperationType<ThenReadFile> getType() {
        return ThenType.ReadFile;
    }
}
