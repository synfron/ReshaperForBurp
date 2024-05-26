package synfron.reshaper.burp.core.vars;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.utils.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class VariableSourceEntry implements Serializable {
    private final VariableSource variableSource;
    private String tag;
    private final List<String> params = new ArrayList<>();

    public VariableSourceEntry(
            @JsonProperty("variableSource") VariableSource variableSource,
            @JsonProperty("name") String name,
            @JsonProperty("params") List<String> params,
            @JsonProperty("tag") String tag
    ) {
        this.variableSource = variableSource;
        this.params.addAll(CollectionUtils.hasAny(params) ? params : VariableTag.parseParams(":" + StringUtils.defaultString(name)));
        this.tag = tag;
    }

    public VariableSourceEntry(VariableSource variableSource, List<String> params, String tag) {
        this(variableSource, null, params, tag);
    }

    public VariableSourceEntry(VariableSource variableSource, List<String> params) {
        this(variableSource, null, params, null);
    }

    @JsonProperty
    public String getTag() {
        if (StringUtils.isEmpty(tag)) {
            tag = VariableTag.getTag(variableSource, false, params.toArray(String[]::new));
        }
        return tag;
    }

}
