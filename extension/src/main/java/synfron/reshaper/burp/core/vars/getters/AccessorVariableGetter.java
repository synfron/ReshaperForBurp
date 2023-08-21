package synfron.reshaper.burp.core.vars.getters;

import burp.BurpExtender;
import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.HttpRequestResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import synfron.reshaper.burp.core.messages.*;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.core.vars.Variables;

import java.io.File;
import java.util.Arrays;

public class AccessorVariableGetter extends VariableGetter {
    @Override
    public String getText(VariableSourceEntry variable, EventInfo eventInfo) {
        return switch (variable.getVariableSource()) {
            case Message -> getMessageVariable(eventInfo, variable.getName());
            case File -> getFileText(eventInfo, variable.getName());
            case Special -> variable.getName();
            case CookieJar -> getCookie(variable.getName());
            case Annotation -> getAnnotation(eventInfo, variable.getName());
            case Macro -> getMacro(eventInfo, variable.getName());
            default -> null;
        };
    }

    private String getMacro(EventInfo eventInfo, String locator) {
        String[] variableNameParts = locator.split(":", 3);
        if (variableNameParts.length > 1) {
            int macroItemIndex = NumberUtils.toInt(variableNameParts[0], 0) - 1;
            MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, variableNameParts[1]);
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

    private String getAnnotation(EventInfo eventInfo, String name) {
        MessageAnnotation annotation = EnumUtils.getEnumIgnoreCase(MessageAnnotation.class, name);
        if (annotation != null && eventInfo.getAnnotations() != null) {
            return switch (annotation) {
                case Comment -> eventInfo.getAnnotations().notes();
                case HighlightColor -> StringUtils.capitalize(eventInfo.getAnnotations().highlightColor().name().toLowerCase());
            };
        }
        return null;
    }

    private String getCookie(String locator) {
        try {
            String[] parts = locator.split(":", 3);
            String domain = parts[0];
            String name = parts[1];
            String path = CollectionUtils.elementAtOrDefault(parts, 2);
            if (Arrays.stream(parts).anyMatch(part -> part.startsWith("\""))) {
                try (CSVParser csvParser = CSVParser.parse(locator, getParamFormat())) {
                    CSVRecord record = csvParser.getRecords().get(0);
                    if (record.size() == 2) {
                        domain = record.get(0);
                        name = record.get(1);
                        path = null;
                    } else if (record.size() == 3) {
                        domain = record.get(0);
                        name = record.get(1);
                        path = record.get(2);
                    }
                }
            }
            for (Cookie cookie : BurpExtender.getApi().http().cookieJar().cookies()) {
                if (cookie.domain().equals(domain)
                        && cookie.name().equals(name)
                        && (path == null || StringUtils.defaultString(cookie.path()).equals(path))) {
                    return cookie.value();
                }
            }
        } catch (Exception e) {
            if (BurpExtender.getGeneralSettings().isEnableEventDiagnostics()) {
                Log.get().withMessage(String.format("Invalid use of cookie jar variable tag: %s", VariableSourceEntry.getTag(VariableSource.CookieJar, locator))).withException(e).logErr();
            }
        }
        return "";
    }

    private String getFileText(EventInfo eventInfo, String locator) {
        try {
            String[] variableNameParts = locator.split(":", 2);
            File file = new File(variableNameParts[1]);
            String encoding = variableNameParts[0];
            Encoder encoder = new Encoder(encoding);
            if (encoder.isUseDefault() || encoder.isUseAutoDetect()) {
                byte[] fileBytes = FileUtils.readFileToByteArray(file);
                return encoder.decode(fileBytes);
            }
            return FileUtils.readFileToString(file, encoding);
        } catch (Exception e) {
            if (eventInfo.getDiagnostics().isEnabled()) {
                Log.get().withMessage(String.format("Error reading file with variable tag: %s", VariableSourceEntry.getTag(VariableSource.Special, locator))).withException(e).logErr();
            }
        }
        return null;
    }

    private String getMessageVariable(EventInfo eventInfo, String locator) {
        String[] variableNameParts = locator.split(":", 2);
        MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, CollectionUtils.elementAtOrDefault(variableNameParts, 0, ""));
        String identifier = CollectionUtils.elementAtOrDefault(variableNameParts, 1, "");
        if (messageValue != null) {
            return StringUtils.defaultString(
                    MessageValueHandler.getValue(eventInfo, messageValue, VariableString.getAsVariableString(identifier, false), GetItemPlacement.Last)
            );
        }
        return null;
    }

    private CSVFormat getParamFormat() {
        return CSVFormat.DEFAULT.builder().setDelimiter(':')
                .setAllowMissingColumnNames(true)
                .setEscape('\\')
                .build();
    }
}
