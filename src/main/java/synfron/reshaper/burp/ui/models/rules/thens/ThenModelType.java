package synfron.reshaper.burp.ui.models.rules.thens;

import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.thens.*;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenModelType<P extends ThenModel<P, T>, T extends Then<T>> extends RuleOperationModelType<P, T> {
    public static final ThenModelType<ThenBreakModel, ThenBreak> Break = new ThenModelType<>("Break", ThenBreakModel.class, ThenType.Break);
    public static final ThenModelType<ThenDelayModel, ThenDelay> Delay = new ThenModelType<>("Delay", ThenDelayModel.class, ThenType.Delay);
    public static final ThenModelType<ThenLogModel, ThenLog> Log = new ThenModelType<>("Log", ThenLogModel.class, ThenType.Log);
    public static final ThenModelType<ThenHighlightModel, ThenHighlight> Highlight = new ThenModelType<>("Highlight", ThenHighlightModel.class, ThenType.Highlight);
    public static final ThenModelType<ThenCommentModel, ThenComment> Comment = new ThenModelType<>("Comment", ThenCommentModel.class, ThenType.Comment);
    public static final ThenModelType<ThenPromptModel, ThenPrompt> Prompt = new ThenModelType<>("Prompt", ThenPromptModel.class, ThenType.Prompt);
    public static final ThenModelType<ThenRunRulesModel, ThenRunRules> RunRules = new ThenModelType<>("Run Rules", ThenRunRulesModel.class, ThenType.RunRules);
    public static final ThenModelType<ThenRunScriptModel, ThenRunScript> RunScript = new ThenModelType<>("Run Script", ThenRunScriptModel.class, ThenType.RunScript);
    public static final ThenModelType<ThenSetEventDirectionModel, ThenSetEventDirection> SetEventDirection = new ThenModelType<>("Set Event Direction", ThenSetEventDirectionModel.class, ThenType.SetEventDirection);
    public static final ThenModelType<ThenSetEncodingModel, ThenSetEncoding> SetEncoding = new ThenModelType<>("Set Encoding", ThenSetEncodingModel.class, ThenType.SetEncoding);
    public static final ThenModelType<ThenSetValueModel, ThenSetValue> SetValue = new ThenModelType<>("Set Value", ThenSetValueModel.class, ThenType.SetValue);
    public static final ThenModelType<ThenDeleteValueModel, ThenDeleteValue> DeleteValue = new ThenModelType<>("Delete Value", ThenDeleteValueModel.class, ThenType.DeleteValue);
    public static final ThenModelType<ThenSetVariableModel, ThenSetVariable> SetVariable = new ThenModelType<>("Set Variable", ThenSetVariableModel.class, ThenType.SetVariable);
    public static final ThenModelType<ThenDeleteVariableModel, ThenDeleteVariable> DeleteVariable = new ThenModelType<>("Delete Variable", ThenDeleteVariableModel.class, ThenType.DeleteVariable);
    public static final ThenModelType<ThenSaveFileModel, ThenSaveFile> SaveFile = new ThenModelType<>("Save File", ThenSaveFileModel.class, ThenType.SaveFile);
    public static final ThenModelType<ThenSendToModel, ThenSendTo> SendTo = new ThenModelType<>("Send To", ThenSendToModel.class, ThenType.SendTo);
    public static final ThenModelType<ThenRunProcessModel, ThenRunProcess> RunProcess = new ThenModelType<>("Run Process", ThenRunProcessModel.class, ThenType.RunProcess);
    public static final ThenModelType<ThenBuildHttpMessageModel, ThenBuildHttpMessage> BuildHttpMessage = new ThenModelType<>("Build HTTP Message", ThenBuildHttpMessageModel.class, ThenType.BuildHttpMessage);
    public static final ThenModelType<ThenParseHttpMessageModel, ThenParseHttpMessage> ParseHttpMessage = new ThenModelType<>("Parse HTTP Message", ThenParseHttpMessageModel.class, ThenType.ParseHttpMessage);
    public static final ThenModelType<ThenSendRequestModel, ThenSendRequest> SendRequest = new ThenModelType<>("Send Request", ThenSendRequestModel.class, ThenType.SendRequest);
    public static final ThenModelType<ThenDropModel, ThenDrop> Drop = new ThenModelType<>("Drop", ThenDropModel.class, ThenType.Drop);

    private ThenModelType(String name, Class<P> type, RuleOperationType<T> ruleOperationType) {
        super(name, type, ruleOperationType);
    }

    public static List<ThenModelType<?,?>> getTypes() {
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
                SetEncoding,
                SetValue,
                DeleteValue,
                SetVariable,
                DeleteVariable,
                SaveFile,
                SendTo,
                RunProcess,
                BuildHttpMessage,
                ParseHttpMessage,
                SendRequest,
                Drop
        );
    }
}
