package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenGenerate;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.components.rules.thens.generate.*;
import synfron.reshaper.burp.ui.models.rules.thens.ThenGenerateModel;
import synfron.reshaper.burp.ui.models.rules.thens.generate.*;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenGenerateComponent extends ThenComponent<ThenGenerateModel, ThenGenerate> {
    private JComboBox<GenerateOption> generateOption;
    private final JPanel generatorContainer = new JPanel();
    private JComboBox<VariableSource> destinationVariableSource;
    private JTextField destinationVariableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;

    public ThenGenerateComponent(ProtocolType protocolType, ThenGenerateModel then) {
        super(protocolType, then);
        initComponent();
    }
    
    private GeneratorComponent<?> getGenerator() {
        return switch (model.getGenerateOption()) {
            case Uuid -> new UuidGeneratorComponent((UuidGeneratorModel) model.getGenerator(), true);
            case Words -> new WordGeneratorComponent((WordGeneratorModel) model.getGenerator(), true);
            case Bytes -> new BytesGeneratorComponent((BytesGeneratorModel) model.getGenerator(), true);
            case Integer -> new IntegerGeneratorComponent((IntegerGeneratorModel) model.getGenerator(), true);
            case IpAddress -> new IpAddressGeneratorComponent((IpAddressGeneratorModel) model.getGenerator(), true);
            case Timestamp -> new TimestampGeneratorComponent((TimestampGeneratorModel) model.getGenerator(), true);
            case UnixTimestamp -> new UnixTimestampGeneratorComponent((UnixTimestampGeneratorModel) model.getGenerator(), true);
            case Password -> new PasswordGeneratorComponent((PasswordGeneratorModel) model.getGenerator(), true);
        };
    }

    private void initComponent() {
        generatorContainer.setBorder(BorderFactory.createEmptyBorder(0, -12, 0, 0));
        generateOption = createComboBox(GenerateOption.values());
        destinationVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        destinationVariableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);

        generateOption.setSelectedItem(model.getGenerateOption());
        destinationVariableSource.setSelectedItem(model.getDestinationVariableSource());
        destinationVariableName.setText(model.getDestinationVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        generateOption.addActionListener(this::onSetGenerateOptionChanged);
        destinationVariableSource.addActionListener(this::onDestinationVariableSourceChanged);
        destinationVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onDestinationVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        mainContainer.add(getLabeledField("Generate Option", generateOption), "wrap");
        mainContainer.add(generatorContainer, "wrap");
        mainContainer.add(getLabeledField("Destination Variable Source", destinationVariableSource), "wrap");
        mainContainer.add(getLabeledField("Destination Variable Name *", destinationVariableName), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Item Placement", itemPlacement),
                destinationVariableSource,
                () -> ((VariableSource)destinationVariableSource.getSelectedItem()).isList()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Delimiter *", delimiter),
                List.of(destinationVariableSource, itemPlacement),
                () -> ((VariableSource)destinationVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasDelimiterSetter()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Index *", index),
                List.of(destinationVariableSource, itemPlacement),
                () -> ((VariableSource)destinationVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasIndexSetter()
        ), "wrap");

        setGenerator();
    }

    private void setGenerator() {
        GeneratorComponent<?> generator = getGenerator();

        generatorContainer.removeAll();
        generatorContainer.add(generator);
        revalidate();
        repaint();
    }

    private void onSetGenerateOptionChanged(ActionEvent actionEvent) {
        model.setGenerateOption((GenerateOption) generateOption.getSelectedItem());
        setGenerator();
    }

    private void onDestinationVariableSourceChanged(ActionEvent actionEvent) {
        model.setDestinationVariableSource((VariableSource) destinationVariableSource.getSelectedItem());
    }

    private void onDestinationVariableNameChanged(ActionEvent actionEvent) {
        model.setDestinationVariableName(destinationVariableName.getText());
    }

    private void onItemPlacementChanged(ActionEvent actionEvent) {
        model.setItemPlacement((SetListItemPlacement)itemPlacement.getSelectedItem());
    }

    private void onDelimiterChanged(ActionEvent actionEvent) {
        model.setDelimiter(delimiter.getText());
    }

    private void onIndexChanged(ActionEvent actionEvent) {
        model.setIndex(index.getText());
    }
}
