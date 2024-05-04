package synfron.reshaper.burp.ui.components.rules.thens.transform;

import synfron.reshaper.burp.core.rules.thens.entities.transform.HashType;
import synfron.reshaper.burp.ui.models.rules.thens.transform.HashTransformerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HashTransformerComponent extends TransformerComponent<HashTransformerModel> {

    private JComboBox<HashType> hashType;

    public HashTransformerComponent(HashTransformerModel model) {
        super(model);
    }

    protected void initComponent() {
        hashType = createComboBox(HashType.values());

        hashType.setSelectedItem(model.getHashType());

        hashType.addActionListener(this::onHashTypeChanged);

        add(getLabeledField("Hash Type", hashType), "wrap");
    }

    private void onHashTypeChanged(ActionEvent actionEvent) {
        model.setHashType((HashType)hashType.getSelectedItem());
    }
}
