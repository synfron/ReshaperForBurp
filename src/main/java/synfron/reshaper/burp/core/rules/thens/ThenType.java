package synfron.reshaper.burp.core.rules.thens;

import lombok.EqualsAndHashCode;
import synfron.reshaper.burp.core.rules.RuleOperationType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class ThenType<T extends Then<T>> extends RuleOperationType<T> {
    public static final ThenType<ThenBreak> Break = new ThenType<>("Break", ThenBreak.class);
    public static final ThenType<ThenDelay> Delay = new ThenType<>("Delay", ThenDelay.class);
    public static final ThenType<ThenLog> Log = new ThenType<>("Log", ThenLog.class);
    public static final ThenType<ThenHighlight> Highlight = new ThenType<>("Highlight", ThenHighlight.class);
    public static final ThenType<ThenComment> Comment = new ThenType<>("Comment", ThenComment.class);
    public static final ThenType<ThenPrompt> Prompt = new ThenType<>("Prompt", ThenPrompt.class);
    public static final ThenType<ThenRunRules> RunRules = new ThenType<>("Run Rules", ThenRunRules.class);
    public static final ThenType<ThenRunScript> RunScript = new ThenType<>("Run Script", ThenRunScript.class);
    public static final ThenType<ThenSetEventDirection> SetEventDirection = new ThenType<>("Set Event Direction", ThenSetEventDirection.class);
    public static final ThenType<ThenSetValue> SetValue = new ThenType<>("Set Value", ThenSetValue.class);
    public static final ThenType<ThenDeleteValue> DeleteValue = new ThenType<>("Delete Value", ThenDeleteValue.class);
    public static final ThenType<ThenSetVariable> SetVariable = new ThenType<>("Set Variable", ThenSetVariable.class);
    public static final ThenType<ThenDeleteVariable> DeleteVariable = new ThenType<>("Delete Variable", ThenDeleteVariable.class);
    public static final ThenType<ThenSaveFile> SaveFile = new ThenType<>("Save File", ThenSaveFile.class);
    public static final ThenType<ThenSendTo> SendTo = new ThenType<>("Send To", ThenSendTo.class);
    public static final ThenType<ThenRunProcess> RunProcess = new ThenType<>("Run Process", ThenRunProcess.class);
    public static final ThenType<ThenBuildHttpMessage> BuildHttpMessage = new ThenType<>("Build HTTP Message", ThenBuildHttpMessage.class);
    public static final ThenType<ThenParseHttpMessage> ParseHttpMessage = new ThenType<>("Parse HTTP Message", ThenParseHttpMessage.class);
    public static final ThenType<ThenSendRequest> SendRequest = new ThenType<>("Send Request", ThenSendRequest.class);
    public static final ThenType<ThenDrop> Drop = new ThenType<>("Drop", ThenDrop.class);

    private ThenType() {
        this(null, null);
    }

    private ThenType(String name, Class<T> type) {
        super(name, type);
    }

    public static List<ThenType<?>> getTypes() {
        return List.of(
                Break,
                Delay,
                Log,
                Highlight,
                Comment,
                Prompt,
                RunRules,
                RunScript,
                SetEventDirection,
                SetValue,
                DeleteValue,
                SetVariable,
                DeleteVariable,
                SaveFile,
                SendTo,
                RunProcess,
                ParseHttpMessage,
                BuildHttpMessage,
                SendRequest,
                Drop
        );
    }
}
