package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IWordGeneratorModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WordGeneratorComponent extends GeneratorComponent<IWordGeneratorModel> {
    
    private JComboBox<ValueGenerator.WordGeneratorType> generatorType;
    private JTextField count;
    private JTextField separator;

    public WordGeneratorComponent(IWordGeneratorModel model, boolean allowVariableTags) {
        super(model, allowVariableTags);
    }

    protected void initComponent() {
        generatorType = createComboBox(ValueGenerator.WordGeneratorType.values());
        count = createTextField(allowVariableTags);
        separator = createTextField(allowVariableTags);

        generatorType.setSelectedItem(model.getGeneratorType());
        count.setText(model.getCount());
        separator.setText(model.getSeparator());

        generatorType.addActionListener(this::onGeneratorTypeChanged);
        count.getDocument().addDocumentListener(new DocumentActionListener(this::onCountChanged));
        separator.getDocument().addDocumentListener(new DocumentActionListener(this::onSeparatorChanged));

        add(getLabeledField("Generator Type", generatorType), "wrap");
        add(getLabeledField("Count *", count), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Separator", separator),
                count,
                () -> !"1".equals(count.getText())
        ), "wrap");
    }

    private void onGeneratorTypeChanged(ActionEvent actionEvent) {
        model.setGeneratorType((ValueGenerator.WordGeneratorType)generatorType.getSelectedItem());
    }

    private void onCountChanged(ActionEvent actionEvent) {
        model.setCount(count.getText());
    }

    private void onSeparatorChanged(ActionEvent actionEvent) {
        model.setSeparator(separator.getText());
    }
}
