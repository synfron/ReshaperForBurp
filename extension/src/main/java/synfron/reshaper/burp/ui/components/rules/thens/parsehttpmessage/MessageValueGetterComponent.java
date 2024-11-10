package synfron.reshaper.burp.ui.components.rules.thens.parsehttpmessage;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.thens.parsehttpmessage.MessageValueGetterModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Stream;

public class MessageValueGetterComponent extends JPanel implements IFormComponent {

    private final MessageValueGetterModel model;
    private final ProtocolType protocolType;
    private final HttpDataDirection dataDirection;
    private final boolean deletable;
    private JComboBox<MessageValue> sourceMessageValue;
    private JTextField sourceIdentifier;
    protected JComboBox<GetItemPlacement> sourceIdentifierPlacement;
    private JComboBox<VariableSource> destinationVariableSource;
    private JTextField destinationVariableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;

    public MessageValueGetterComponent(ProtocolType protocolType, MessageValueGetterModel model, HttpDataDirection dataDirection, boolean deletable) {
        this.protocolType = protocolType;
        this.dataDirection = dataDirection;
        this.deletable = deletable;
        setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Getter"),
                BorderFactory.createEmptyBorder(4,4,4,4))
        );
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        setLayout(new MigLayout());

        sourceMessageValue = createComboBox(
                Stream.of(MessageValue.values())
                        .filter(messageValue -> messageValue.getDataDirection() == dataDirection && messageValue.isInnerLevelGettable(ProtocolType.Http))
                        .toArray(MessageValue[]::new)
        );
        sourceIdentifier = createTextField(true);
        sourceIdentifierPlacement = createComboBox(GetItemPlacement.values());
        destinationVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        destinationVariableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);

        sourceMessageValue.setSelectedItem(model.getSourceMessageValue());
        sourceIdentifier.setText(model.getSourceIdentifier());
        sourceIdentifierPlacement.setSelectedItem(model.getSourceIdentifierPlacement());
        destinationVariableSource.setSelectedItem(model.getDestinationVariableSource());
        destinationVariableName.setText(model.getDestinationVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        sourceMessageValue.addActionListener(this::onSourceMessageValueChanged);
        sourceIdentifier.getDocument().addDocumentListener(new DocumentActionListener(this::onSourceIdentifierChanged));
        sourceIdentifierPlacement.addActionListener(this::onSourceIdentifierPlacementChanged);
        destinationVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onDestinationVariableNameChanged));
        destinationVariableSource.addActionListener(this::onDestinationVariableSourceChanged);
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        add(getLabeledField("Message Value", sourceMessageValue), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier *", sourceIdentifier),
                List.of(sourceMessageValue),
                () -> ((MessageValue) sourceMessageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier Placement", sourceIdentifierPlacement),
                List.of(sourceMessageValue),
                () -> ((MessageValue) sourceMessageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        add(getLabeledField("Destination Variable Source", destinationVariableSource), "wrap");
        add(getLabeledField("Destination Variable Name *", destinationVariableName), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Item Placement", itemPlacement),
                destinationVariableSource,
                () -> ((VariableSource)destinationVariableSource.getSelectedItem()).isList()
        ), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Delimiter *", delimiter),
                itemPlacement,
                () -> ((VariableSource)destinationVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasDelimiterSetter()
        ), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Index *", index),
                itemPlacement,
                () -> ((VariableSource)destinationVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasIndexSetter()
        ), "wrap");
        if (deletable) {
            JButton delete = new JButton("Delete");
            delete.addActionListener(this::onDelete);
            add(getPaddedButton(delete));
        }
    }

    private void onSourceIdentifierChanged(ActionEvent actionEvent) {
        model.setSourceIdentifier(sourceIdentifier.getText());
    }

    private void onSourceIdentifierPlacementChanged(ActionEvent actionEvent) {
        model.setSourceIdentifierPlacement((GetItemPlacement) sourceIdentifierPlacement.getSelectedItem());
    }

    private void onSourceMessageValueChanged(ActionEvent actionEvent) {
        model.setSourceMessageValue((MessageValue) sourceMessageValue.getSelectedItem());
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

    private void onDelete(ActionEvent actionEvent) {
        model.setDeleted(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
