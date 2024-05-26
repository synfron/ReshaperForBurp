package synfron.reshaper.burp.ui.models.rules.wizard.vars.generator;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IUuidGeneratorModel;

import java.util.List;

@Getter
public class UuidGeneratorVariableModel extends GeneratorVariableModel<UuidGeneratorVariableModel> implements IUuidGeneratorModel {

    private ValueGenerator.UuidVersion version = ValueGenerator.UuidVersion.V4;

    private String namespace = "";
    private String name = "";

    public void setVersion(ValueGenerator.UuidVersion version) {
        this.version = version;
        propertyChanged("version", version);
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
        propertyChanged("namespace", namespace);
    }

    public void setName(String name) {
        this.name = name;
        propertyChanged("name", name);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (version.isHasInputs()) {
            if (StringUtils.isEmpty(namespace)) {
                errors.add("Namespace is required");
            }
            if (StringUtils.isEmpty(name)) {
                errors.add("Name is required");
            }
        }
        return errors;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                getTagInternal() :
                null;
    }

    @Override
    public String getTagInternal() {
        return version.isHasInputs() ?
                VariableTag.getShortTag(VariableSource.Generator, GenerateOption.Uuid.name().toLowerCase(), version.name().toLowerCase(), namespace, name) :
                VariableTag.getShortTag(VariableSource.Generator, GenerateOption.Uuid.name().toLowerCase(), version.name().toLowerCase());
    }
}
