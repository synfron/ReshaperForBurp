package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.generate.IpAddressGenerator;

@Getter
public class IpAddressGeneratorModel extends GeneratorModel<IpAddressGeneratorModel, IpAddressGenerator> {

    private IpAddressGenerator.IpVersion version;

    public IpAddressGeneratorModel(IpAddressGenerator generator) {
        super(generator);
        version = generator.getVersion();
    }

    public void setVersion(IpAddressGenerator.IpVersion version) {
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
