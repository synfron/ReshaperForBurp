package synfron.reshaper.burp.ui.models.rules.thens.generate;

import synfron.reshaper.burp.core.utils.ValueGenerator;

public interface IUuidGeneratorModel extends IGeneratorModel<IUuidGeneratorModel> {
    void setVersion(ValueGenerator.UuidVersion version);

    void setNamespace(String namespace);

    void setName(String name);

    ValueGenerator.UuidVersion getVersion();

    String getNamespace();

    String getName();
}
