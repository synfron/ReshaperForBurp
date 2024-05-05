package synfron.reshaper.burp.core.vars;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum VariableSource {
    Event("e", false, ProtocolType.Any, false),
    Global("g", false, ProtocolType.Any, false),
    Session("sn", false, ProtocolType.Any, false),
    EventList("Event List", "el", false, ProtocolType.Any, true),
    GlobalList("Global List", "gl", false, ProtocolType.Any, true),
    SessionList("Session List", "sl", false, ProtocolType.Any, true),
    Annotation("a", true, ProtocolType.Any, false),
    CookieJar("Cookie Jar", "cj", true, ProtocolType.Any, false),
    File("f", true, ProtocolType.Any, false),
    Generator("gr", true, ProtocolType.Any, false),
    Macro("mc", true, ProtocolType.Http, false),
    Message("m", true, ProtocolType.Any, false),
    Special("s", true, ProtocolType.Any, false);

    private final String displayName;
    private final String shortName;
    private final boolean accessor;
    private final ProtocolType protocolType;
    private final boolean isList;

    VariableSource(String displayName, String shortName, boolean accessor, ProtocolType protocolType, boolean isList) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.accessor = accessor;
        this.protocolType = protocolType;
        this.isList = isList;
    }

    VariableSource(String shortName, boolean accessor, ProtocolType protocolType, boolean isList) {
        this.displayName = this.name();
        this.shortName = shortName;
        this.accessor = accessor;
        this.protocolType = protocolType;
        this.isList = isList;
    }

    public static List<String> getSupportedNames() {
        return Stream.concat(
                Arrays.stream(VariableSource.values()).map(source -> source.name().toLowerCase()),
                Arrays.stream(VariableSource.values()).map(VariableSource::getShortName)
        ).collect(Collectors.toList());
    }

    public static VariableSource[] getAllSettables(ProtocolType protocolType) {
        return Arrays.stream(values()).filter(value -> value.protocolType.accepts(protocolType) && !value.accessor).toArray(VariableSource[]::new);
    }

    public static VariableSource[] getAll(ProtocolType protocolType) {
        return Arrays.stream(values()).filter(value -> value.protocolType.accepts(protocolType)).toArray(VariableSource[]::new);
    }

    public static VariableSource get(String name) {
        return Arrays.stream(VariableSource.values())
                .filter(source -> source.name().equalsIgnoreCase(name) || source.shortName.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }


    @Override
    public String toString() {
        return displayName;
    }
}
