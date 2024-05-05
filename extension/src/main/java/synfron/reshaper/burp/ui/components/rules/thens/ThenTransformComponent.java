package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenTransform;
import synfron.reshaper.burp.core.rules.thens.entities.transform.TransformOption;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.components.rules.thens.transform.*;
import synfron.reshaper.burp.ui.models.rules.thens.ThenTransformModel;
import synfron.reshaper.burp.ui.models.rules.thens.transform.*;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenTransformComponent extends ThenComponent<ThenTransformModel, ThenTransform> {
    private JComboBox<TransformOption> transformOption;
    private final JPanel transformerContainer = new JPanel();
    private JComboBox<VariableSource> destinationVariableSource;
    private JTextField destinationVariableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;

    public ThenTransformComponent(ProtocolType protocolType, ThenTransformModel then) {
        super(protocolType, then);
        initComponent();
    }
    
    private TransformerComponent<?> getTransformer() {
        return switch (model.getTransformOption()) {
            case Base64 -> new Base64TransformerComponent((Base64TransformerModel) model.getTransformer());
            case Escape -> new EscapeTransformerComponent((EscapeTransformerModel) model.getTransformer());
            case JwtDecode -> new JwtDecodeTransformerComponent((JwtDecodeTransformerModel) model.getTransformer());
            case Case -> new CaseTransformerComponent((CaseTransformerModel) model.getTransformer());
            case Hash -> new HashTransformerComponent((HashTransformerModel) model.getTransformer());
            case Hex -> new HexTransformerComponent((HexTransformerModel) model.getTransformer());
            case Integer -> new IntegerTransformerComponent((IntegerTransformerModel) model.getTransformer());
            case Trim -> new TrimTransformerComponent((TrimTransformerModel) model.getTransformer());
        };
    }

    private void initComponent() {
        transformerContainer.setBorder(BorderFactory.createEmptyBorder(0, -12, 0, 0));
        transformOption = createComboBox(TransformOption.values());
        destinationVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        destinationVariableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);

        transformOption.setSelectedItem(model.getTransformOption());
        destinationVariableSource.setSelectedItem(model.getDestinationVariableSource());
        destinationVariableName.setText(model.getDestinationVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        transformOption.addActionListener(this::onSetTransformOptionChanged);
        destinationVariableSource.addActionListener(this::onDestinationVariableSourceChanged);
        destinationVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onDestinationVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        mainContainer.add(getLabeledField("Transform Option", transformOption), "wrap");
        mainContainer.add(transformerContainer, "wrap");
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

        setTransformer();
    }

    private void setTransformer() {
        TransformerComponent<?> transformer = getTransformer();

        transformerContainer.removeAll();
        transformerContainer.add(transformer);
        revalidate();
        repaint();
    }

    private void onSetTransformOptionChanged(ActionEvent actionEvent) {
        model.setTransformOption((TransformOption) transformOption.getSelectedItem());
        setTransformer();
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
