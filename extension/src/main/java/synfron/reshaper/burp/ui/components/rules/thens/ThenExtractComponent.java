package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.DeleteItemPlacement;
import synfron.reshaper.burp.core.rules.thens.ThenExtract;
import synfron.reshaper.burp.core.rules.thens.entities.extract.ExtractorType;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.SetListItemsPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenExtractModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

public class ThenExtractComponent extends ThenComponent<ThenExtractModel, ThenExtract> {
    private JTextField text;
    private JComboBox<ExtractorType> extractorType;
    private JTextField extractor;
    private JComboBox<VariableSource> listVariableSource;
    private JTextField listVariableName;
    private JTextField delimiter;
    private JComboBox<SetListItemsPlacement> itemsPlacement;
    private JPanel extractorField;

    public ThenExtractComponent(ProtocolType protocolType, ThenExtractModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        text = createTextField(true);
        extractorType = createComboBox(ExtractorType.values());
        extractor = createTextField(true);
        listVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        listVariableName = createTextField(true);
        delimiter = createTextField(true);
        itemsPlacement = createComboBox(SetListItemsPlacement.values());

        text.setText(model.getText());
        extractorType.setSelectedItem(model.getExtractorType());
        extractor.setText(model.getExtractor());
        listVariableSource.setSelectedItem(model.getListVariableSource());
        listVariableName.setText(model.getListVariableName());
        delimiter.setText(model.getDelimiter());
        itemsPlacement.setSelectedItem(model.getItemsPlacement());

        text.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));
        extractorType.addActionListener(this::onExtractorTypeChanged);
        extractor.getDocument().addDocumentListener(new DocumentActionListener(this::onExtractorChanged));
        listVariableSource.addActionListener(this::onListVariableSourceChanged);
        listVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onListVariableNameChanged));
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        itemsPlacement.addActionListener(this::onItemsPlacementChanged);

        extractorField = getLabeledField( model.getExtractorType() + " *", extractor);

        mainContainer.add(getLabeledField("Text *", text), "wrap");
        mainContainer.add(getLabeledField("Extractor Type", extractorType), "wrap");
        mainContainer.add(getLabeledField( model.getExtractorType() + " *", extractor), "wrap");
        mainContainer.add(getLabeledField("List Variable Source", listVariableSource), "wrap");
        mainContainer.add(getLabeledField("List Variable Name *", listVariableName), "wrap");
        mainContainer.add(getLabeledField("Delimiter *", delimiter), "wrap");
        mainContainer.add(getLabeledField("Items Placement", itemsPlacement), "wrap");
    }

    private void onTextChanged(ActionEvent actionEvent) {
        model.setText(text.getText());
    }

    private void onExtractorTypeChanged(ActionEvent actionEvent) {
        model.setExtractorType((ExtractorType) extractorType.getSelectedItem());
        ((JLabel)extractorField.getComponents()[0]).setText(model.getExtractorType().getExtractorType());
    }

    private void onExtractorChanged(ActionEvent actionEvent) {
        model.setExtractor(extractor.getText());
    }

    private void onListVariableSourceChanged(ActionEvent actionEvent) {
        model.setListVariableSource((VariableSource) listVariableSource.getSelectedItem());
    }

    private void onListVariableNameChanged(ActionEvent actionEvent) {
        model.setListVariableName(listVariableName.getText());
    }

    private void onDelimiterChanged(ActionEvent actionEvent) {
        model.setDelimiter(delimiter.getText());
    }

    private void onItemsPlacementChanged(ActionEvent actionEvent) {
        model.setItemsPlacement((SetListItemsPlacement) itemsPlacement.getSelectedItem());
    }
}
