package synfron.reshaper.burp.core.vars.getters;

import burp.BurpExtender;
import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.HttpRequestResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import synfron.reshaper.burp.core.messages.*;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.utils.PasswordCharacterGroup;
import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.core.vars.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AccessorVariableGetter extends VariableGetter {
    @Override
    public String getText(VariableSourceEntry variable, EventInfo eventInfo) {
        return switch (variable.getVariableSource()) {
            case Message -> getMessageVariable(eventInfo, variable.getParams());
            case File -> getFileText(eventInfo, variable.getParams());
            case Special -> variable.getParams().getFirst();
            case CookieJar -> getCookie(variable.getParams());
            case Annotation -> getAnnotation(eventInfo, variable.getParams());
            case Macro -> getMacro(eventInfo, variable.getParams());
            case Generator -> generate(eventInfo, variable.getParams());
            default -> null;
        };
    }

    private String generate(EventInfo eventInfo, List<String> params) {
        GenerateOption generateOption = EnumUtils.getEnumIgnoreCase(GenerateOption.class, params.getFirst());
        return switch (generateOption) {
            case Uuid -> ValueGenerator.uuid(
                    EnumUtils.getEnumIgnoreCase(ValueGenerator.UuidVersion.class, params.get(1)),
                    CollectionUtils.elementAtOrDefault(params, 2),
                    CollectionUtils.elementAtOrDefault(params, 3)
            );
            case Words -> ValueGenerator.words(
                    EnumUtils.getEnumIgnoreCase(ValueGenerator.WordGeneratorType.class, params.get(1)),
                    Integer.parseInt(CollectionUtils.elementAtOrDefault(params, 2, "1")),
                    CollectionUtils.elementAtOrDefault(params, 3, "\n")
            );
            case Password -> ValueGenerator.password(
                    Integer.parseInt(params.get(1)),
                    Integer.parseInt(params.get(2)),
                    CollectionUtils.defaultIfEmpty(
                            Arrays.stream(CollectionUtils.elementAtOrDefault(params, 3, "").split(","))
                                    .filter(StringUtils::isNotEmpty)
                                    .map(characterGroup -> EnumUtils.getEnumIgnoreCase(PasswordCharacterGroup.class, characterGroup))
                                    .toList(),
                            List.of(PasswordCharacterGroup.values())
                    )
            );
            case Bytes -> ValueGenerator.bytes(Integer.parseInt(params.get(1)), eventInfo.getEncoder());
            case Integer -> ValueGenerator.integer(Long.parseLong(params.get(1)), Long.parseLong(params.get(2)), Integer.parseInt(CollectionUtils.elementAtOrDefault(params, 3, "10")));
            case IpAddress -> ValueGenerator.ipAddress(EnumUtils.getEnumIgnoreCase(ValueGenerator.IpVersion.class, params.get(1)));
            case Timestamp -> ValueGenerator.dateOrNow(CollectionUtils.elementAtOrDefault(params, 1), CollectionUtils.elementAtOrDefault(params, 2), CollectionUtils.elementAtOrDefault(params, 3));
            case UnixTimestamp -> ValueGenerator.timestampOrNow(CollectionUtils.elementAtOrDefault(params, 1), CollectionUtils.elementAtOrDefault(params, 2), CollectionUtils.elementAtOrDefault(params, 3));
        };
    }

    private String getMacro(EventInfo eventInfo, List<String> variableNameParts) {
        if (variableNameParts.size() > 1) {
            int macroItemIndex = NumberUtils.toInt(variableNameParts.getFirst(), 0) - 1;
            MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, variableNameParts.get(1));
            String identifier = CollectionUtils.elementAtOrDefault(variableNameParts, 2, "");
            if (macroItemIndex >= 0 && messageValue != null) {
                HttpRequestResponse requestResponse = CollectionUtils.elementAtOrDefault(eventInfo.getMacros(), macroItemIndex);
                HttpEventInfo macroEventInfo = new HttpEventInfo(
                        HttpDataDirection.Response,
                        eventInfo.getBurpTool(),
                        null,
                        requestResponse.request(),
                        requestResponse.response(),
                        requestResponse.annotations(),
                        new Variables()
                );
                return StringUtils.defaultString(MessageValueHandler.getValue(
                        macroEventInfo,
                        messageValue,
                        VariableString.getAsVariableString(identifier, false), GetItemPlacement.Last
                ));
            }
        }
        return null;
    }

    private String getAnnotation(EventInfo eventInfo, List<String> name) {
        MessageAnnotation annotation = EnumUtils.getEnumIgnoreCase(MessageAnnotation.class, name.getFirst());
        if (annotation != null && eventInfo.getAnnotations() != null) {
            return switch (annotation) {
                case Comment -> eventInfo.getAnnotations().notes();
                case HighlightColor -> StringUtils.capitalize(eventInfo.getAnnotations().highlightColor().name().toLowerCase());
            };
        }
        return null;
    }

    private String getCookie(List<String> parts) {
        try {
            String domain = parts.getFirst();
            String name = parts.get(1);
            String path = CollectionUtils.elementAtOrDefault(parts, 2);
            for (Cookie cookie : BurpExtender.getApi().http().cookieJar().cookies()) {
                if (cookie.domain().equals(domain)
                        && cookie.name().equals(name)
                        && (path == null || StringUtils.defaultString(cookie.path()).equals(path))) {
                    return cookie.value();
                }
            }
        } catch (Exception e) {
            if (BurpExtender.getGeneralSettings().isEnableEventDiagnostics()) {
                Log.get().withMessage(String.format("Invalid use of cookie jar variable tag: %s", VariableTag.getTag(VariableSource.CookieJar, parts.toArray(String[]::new)))).withException(e).logErr();
            }
        }
        return "";
    }

    private String getFileText(EventInfo eventInfo, List<String> variableNameParts) {
        try {
            File file = new File(variableNameParts.get(1));
            String encoding = variableNameParts.getFirst();
            Encoder encoder = new Encoder(encoding);
            if (encoder.isUseDefault() || encoder.isUseAutoDetect()) {
                byte[] fileBytes = FileUtils.readFileToByteArray(file);
                return encoder.decode(fileBytes);
            }
            return FileUtils.readFileToString(file, encoding);
        } catch (Exception e) {
            if (eventInfo.getDiagnostics().isEnabled()) {
                Log.get().withMessage(String.format("Error reading file with variable tag: %s", VariableTag.getTag(VariableSource.Special, variableNameParts.toArray(String[]::new)))).withException(e).logErr();
            }
        }
        return null;
    }

    private String getMessageVariable(EventInfo eventInfo, List<String> variableNameParts) {
        MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, CollectionUtils.elementAtOrDefault(variableNameParts, 0, ""));
        String identifier = CollectionUtils.elementAtOrDefault(variableNameParts, 1, "");
        if (messageValue != null) {
            return StringUtils.defaultString(
                    MessageValueHandler.getValue(eventInfo, messageValue, VariableString.getAsVariableString(identifier, false), GetItemPlacement.Last)
            );
        }
        return null;
    }
}
