package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.transform.IntegerTransformer;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;

@Getter
public class IntegerTransformerModel extends TransformerModel<IntegerTransformerModel, IntegerTransformer> {

    private String sourceBase = "10";
    private String targetBase = "16";

    public IntegerTransformerModel(IntegerTransformer transformer) {
        super(transformer);
        sourceBase = VariableString.toString(transformer.getSourceBase(), sourceBase);
        targetBase = VariableString.toString(transformer.getTargetBase(), targetBase);
    }

    public void setSourceBase(String sourceBase) {
        this.sourceBase = sourceBase;
        propertyChanged("sourceBase", sourceBase);
    }

    public void setTargetBase(String targetBase) {
        this.targetBase = targetBase;
        propertyChanged("targetBase", targetBase);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (!StringUtils.isEmpty(sourceBase) && !VariableString.isPotentialInt(sourceBase)) {
            errors.add("Source Base must be an integer");
        }
        if (!StringUtils.isEmpty(targetBase) && !VariableString.isPotentialInt(targetBase)) {
            errors.add("Target Base must be an integer");
        }
        return errors;
    }

    @Override
    public boolean persist() {
        super.persist();
        transformer.setSourceBase(VariableString.getAsVariableString(sourceBase));
        transformer.setTargetBase(VariableString.getAsVariableString(targetBase));
        return true;
    }
}
