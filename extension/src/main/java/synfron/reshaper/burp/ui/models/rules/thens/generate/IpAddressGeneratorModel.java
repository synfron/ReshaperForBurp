package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.generate.IpAddressGenerator;
import synfron.reshaper.burp.core.utils.ValueGenerator;

@Getter
public class IpAddressGeneratorModel extends GeneratorModel<IpAddressGeneratorModel, IpAddressGenerator> implements IIpAddressGeneratorModel {

    private ValueGenerator.IpVersion version;

    public IpAddressGeneratorModel(IpAddressGenerator generator) {
        super(generator);
        version = generator.getVersion();
    }

    @Override
    public void setVersion(ValueGenerator.IpVersion version) {
        this.version = version;
        propertyChanged("version", version);
    }

    @Override
    public boolean persist() {
        super.persist();
        generator.setVersion(version);
        return true;
    }
}
