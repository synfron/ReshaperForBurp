package synfron.reshaper.burp.ui.models.rules.thens.generate;

import synfron.reshaper.burp.core.utils.ValueGenerator;

public interface IIpAddressGeneratorModel extends IGeneratorModel<IIpAddressGeneratorModel> {
    void setVersion(ValueGenerator.IpVersion version);

    ValueGenerator.IpVersion getVersion();
}
