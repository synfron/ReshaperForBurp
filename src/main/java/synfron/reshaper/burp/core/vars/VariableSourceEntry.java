package synfron.reshaper.burp.core.vars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class VariableSourceEntry implements Serializable {
    private final VariableSource variableSource;
    private final String name;

    @JsonCreator
    public VariableSourceEntry(@JsonProperty("variableSource") VariableSource variableSource, @JsonProperty("name") String name) {
        this.variableSource = variableSource;
        this.name = name;
    }
}
