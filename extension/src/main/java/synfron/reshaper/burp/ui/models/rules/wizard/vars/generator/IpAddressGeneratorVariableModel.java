package synfron.reshaper.burp.ui.models.rules.wizard.vars.generator;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IIpAddressGeneratorModel;

@Getter
public class IpAddressGeneratorVariableModel extends GeneratorVariableModel<IpAddressGeneratorVariableModel> implements IIpAddressGeneratorModel {

    private ValueGenerator.IpVersion version = ValueGenerator.IpVersion.V4;

    public void setVersion(ValueGenerator.IpVersion version) {
        this.version = version;
        propertyChanged("version", version);
    }

    @Override
    public String getTagInternal() {
        return VariableTag.getShortTag(VariableSource.Generator, GenerateOption.IpAddress.name().toLowerCase(), version.name().toLowerCase());
    }
}
