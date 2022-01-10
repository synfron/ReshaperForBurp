package synfron.reshaper.burp.core.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class VariableSourceEntry implements Serializable {
    private final VariableSource variableSource;
    private final String name;
    private final String tag;

    private VariableSourceEntry() {
        this(null, null, null);
    }

    public VariableSourceEntry(VariableSource variableSource, String name, String tag) {
        this.variableSource = variableSource;
        this.name = name;
        this.tag = tag;
    }

    public String getTag() {
        return StringUtils.isNotEmpty(tag) ? tag : getTag(variableSource, name);
    }

    public static String getTag(VariableSource variableSource, String name) {
        return String.format("{{%s:%s}}", variableSource.toString().toLowerCase(), name);
    }

    public static String getTag(String source, String name, String identifier) {
        return String.format("{{%s}}", Stream.of(
                StringUtils.defaultString(source),
                StringUtils.defaultString(name),
                StringUtils.defaultIfEmpty(identifier, null)
        ).filter(Objects::nonNull).collect(Collectors.joining(":")));
    }
}
