package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.UuidGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;

@Getter
public class UuidGeneratorModel extends GeneratorModel<UuidGeneratorModel, UuidGenerator> {

    private UuidGenerator.UuidVersion version;

    private String namespace = "";
    private String name = "";

    public UuidGeneratorModel(UuidGenerator generator) {
        super(generator);
        version = generator.getVersion();
        namespace = VariableString.toString(generator.getNamespace(), namespace);
        name = VariableString.toString(generator.getName(), name);
    }

    public void setVersion(UuidGenerator.UuidVersion version) {
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
    public boolean persist() {
        super.persist();
        generator.setVersion(version);
        generator.setNamespace(VariableString.getAsVariableString(namespace));
        generator.setName(VariableString.getAsVariableString(name));
        return true;
    }
}
