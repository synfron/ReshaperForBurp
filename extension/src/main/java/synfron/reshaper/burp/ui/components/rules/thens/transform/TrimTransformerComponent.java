package synfron.reshaper.burp.ui.components.rules.thens.transform;

import synfron.reshaper.burp.core.rules.thens.entities.transform.TrimOption;
import synfron.reshaper.burp.ui.models.rules.thens.transform.TrimTransformerModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TrimTransformerComponent extends TransformerComponent<TrimTransformerModel> {

    private JComboBox<TrimOption> trimOption;
    private JTextField characters;

    public TrimTransformerComponent(TrimTransformerModel model) {
        super(model);
    }

    protected void initComponent() {
        trimOption = createComboBox(TrimOption.values());
        characters = createTextField(true);

        trimOption.setSelectedItem(model.getTrimOption());
        characters.setText(model.getCharacters());

        trimOption.addActionListener(this::onTrimOptionChanged);
        characters.getDocument().addDocumentListener(new DocumentActionListener(this::onCharactersChanged));

        add(getLabeledField("Trim Option", trimOption), "wrap");
        add(getLabeledField("Trim Characters", characters), "wrap");
    }

    private void onTrimOptionChanged(ActionEvent actionEvent) {
        model.setTrimOption((TrimOption)trimOption.getSelectedItem());
    }

    private void onCharactersChanged(ActionEvent actionEvent) {
        model.setCharacters(characters.getText());
    }
}
