package synfron.reshaper.burp.core.vars;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum VariableSource {
    Event("e", false, ProtocolType.Any),
    Global("g", false, ProtocolType.Any),
    Session("sn", false, ProtocolType.WebSocket),
    Message("m", true, ProtocolType.Any),
    Annotation("a", true, ProtocolType.Http),
    File("f", true, ProtocolType.Any),
    Special("s", true, ProtocolType.Any),
    CookieJar("Cookie Jar", "cj", true, ProtocolType.Any);

    private final String displayName;
    private final String shortName;
    private final boolean accessor;
    private final ProtocolType protocolType;

    VariableSource(String displayName, String shortName, boolean accessor, ProtocolType protocolType) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.accessor = accessor;
        this.protocolType = protocolType;
    }

    VariableSource(String shortName, boolean accessor, ProtocolType protocolType) {
        this.displayName = this.name();
        this.shortName = shortName;
        this.accessor = accessor;
        this.protocolType = protocolType;
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
