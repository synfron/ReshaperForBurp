package synfron.reshaper.burp.ui.models.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.thens.*;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThenModelType<P extends ThenModel<P, T>, T extends Then<T>> extends RuleOperationModelType<P, T> {
    public static final ThenModelType<ThenBreakModel, ThenBreak> Break = new ThenModelType<>(ThenBreakModel.class, ThenType.Break);
    public static final ThenModelType<ThenDelayModel, ThenDelay> Delay = new ThenModelType<>(ThenDelayModel.class, ThenType.Delay);
    public static final ThenModelType<ThenLogModel, ThenLog> Log = new ThenModelType<>(ThenLogModel.class, ThenType.Log);
    public static final ThenModelType<ThenHighlightModel, ThenHighlight> Highlight = new ThenModelType<>(ThenHighlightModel.class, ThenType.Highlight);
    public static final ThenModelType<ThenCommentModel, ThenComment> Comment = new ThenModelType<>(ThenCommentModel.class, ThenType.Comment);
    public static final ThenModelType<ThenPromptModel, ThenPrompt> Prompt = new ThenModelType<>(ThenPromptModel.class, ThenType.Prompt);
    public static final ThenModelType<ThenRunRulesModel, ThenRunRules> RunRules = new ThenModelType<>(ThenRunRulesModel.class, ThenType.RunRules);
    public static final ThenModelType<ThenRunScriptModel, ThenRunScript> RunScript = new ThenModelType<>(ThenRunScriptModel.class, ThenType.RunScript);
    public static final ThenModelType<ThenEvaluateModel, ThenEvaluate> Evaluate = new ThenModelType<>(ThenEvaluateModel.class, ThenType.Evaluate);
    public static final ThenModelType<ThenSetEventDirectionModel, ThenSetEventDirection> SetEventDirection = new ThenModelType<>(ThenSetEventDirectionModel.class, ThenType.SetEventDirection);
    public static final ThenModelType<ThenSetEncodingModel, ThenSetEncoding> SetEncoding = new ThenModelType<>(ThenSetEncodingModel.class, ThenType.SetEncoding);
    public static final ThenModelType<ThenSetValueModel, ThenSetValue> SetValue = new ThenModelType<>(ThenSetValueModel.class, ThenType.SetValue);
    public static final ThenModelType<ThenDeleteValueModel, ThenDeleteValue> DeleteValue = new ThenModelType<>(ThenDeleteValueModel.class, ThenType.DeleteValue);
    public static final ThenModelType<ThenSetVariableModel, ThenSetVariable> SetVariable = new ThenModelType<>(ThenSetVariableModel.class, ThenType.SetVariable);
    public static final ThenModelType<ThenDeleteVariableModel, ThenDeleteVariable> DeleteVariable = new ThenModelType<>(ThenDeleteVariableModel.class, ThenType.DeleteVariable);
    public static final ThenModelType<ThenSaveFileModel, ThenSaveFile> SaveFile = new ThenModelType<>(ThenSaveFileModel.class, ThenType.SaveFile);
    public static final ThenModelType<ThenSendToModel, ThenSendTo> SendTo = new ThenModelType<>(ThenSendToModel.class, ThenType.SendTo);
    public static final ThenModelType<ThenRunProcessModel, ThenRunProcess> RunProcess = new ThenModelType<>(ThenRunProcessModel.class, ThenType.RunProcess);
    public static final ThenModelType<ThenBuildHttpMessageModel, ThenBuildHttpMessage> BuildHttpMessage = new ThenModelType<>(ThenBuildHttpMessageModel.class, ThenType.BuildHttpMessage);
    public static final ThenModelType<ThenParseHttpMessageModel, ThenParseHttpMessage> ParseHttpMessage = new ThenModelType<>(ThenParseHttpMessageModel.class, ThenType.ParseHttpMessage);
    public static final ThenModelType<ThenSendRequestModel, ThenSendRequest> SendRequest = new ThenModelType<>(ThenSendRequestModel.class, ThenType.SendRequest);
    public static final ThenModelType<ThenSendMessageModel, ThenSendMessage> SendMessage = new ThenModelType<>(ThenSendMessageModel.class, ThenType.SendMessage);
    public static final ThenModelType<ThenDropModel, ThenDrop> Drop = new ThenModelType<>(ThenDropModel.class, ThenType.Drop);
    public static final ThenModelType<ThenInterceptModel, ThenIntercept> Intercept = new ThenModelType<>(ThenInterceptModel.class, ThenType.Intercept);

    private ThenModelType(Class<P> type, RuleOperationType<T> ruleOperationType) {
        super(type, ruleOperationType);
    }

    public static List<ThenModelType<?,?>> getTypes(ProtocolType protocolType) {
        return Stream.of(
                Break,
                Delay,
                Log,
                Highlight,
                Comment,
                Prompt,
                RunRules,
                RunScript,
                Evaluate,
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
                SendMessage,
                Drop,
                Intercept
        ).filter(type -> protocolType == null || type.hasProtocolType(protocolType)).collect(Collectors.toList());
    }
}
