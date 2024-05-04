package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.core.rules.thens.entities.generate.WordGeneratorType;
import synfron.reshaper.burp.ui.models.rules.thens.generate.WordGeneratorModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WordGeneratorComponent extends GeneratorComponent<WordGeneratorModel> {
    
    private JComboBox<WordGeneratorType> generatorType;
    private JTextField count;
    private JTextField separator;

    public WordGeneratorComponent(WordGeneratorModel model) {
        super(model);
    }

    protected void initComponent() {
        generatorType = createComboBox(WordGeneratorType.values());
        count = createTextField(true);
        separator = createTextField(true);

        generatorType.setSelectedItem(model.getGeneratorType());
        count.setText(model.getCount());
        separator.setText(model.getSeparator());

        generatorType.addActionListener(this::onGeneratorTypeChanged);
        count.getDocument().addDocumentListener(new DocumentActionListener(this::onCountChanged));
        separator.getDocument().addDocumentListener(new DocumentActionListener(this::onSeparatorChanged));

        add(getLabeledField("Generator Type", generatorType), "wrap");
        add(getLabeledField("Count *", count), "wrap");
        add(getLabeledField("Separator", separator), "wrap");
    }

    private void onGeneratorTypeChanged(ActionEvent actionEvent) {
        model.setGeneratorType((WordGeneratorType)generatorType.getSelectedItem());
    }

    private void onCountChanged(ActionEvent actionEvent) {
        model.setCount(count.getText());
    }

    private void onSeparatorChanged(ActionEvent actionEvent) {
        model.setSeparator(separator.getText());
    }
}
