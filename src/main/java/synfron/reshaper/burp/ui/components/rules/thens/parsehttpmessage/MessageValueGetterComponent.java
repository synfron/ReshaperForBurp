package synfron.reshaper.burp.ui.components.rules.thens.parsehttpmessage;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.core.utils.SetItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.components.IFormComponent;
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
    private final DataDirection dataDirection;
    private final boolean deletable;
    private JComboBox<MessageValue> sourceMessageValue;
    private JTextField sourceIdentifier;
    protected JComboBox<GetItemPlacement> sourceIdentifierPlacement;
    private JComboBox<VariableSource> destinationVariableSource;
    private JTextField destinationVariableName;

    public MessageValueGetterComponent(MessageValueGetterModel model, DataDirection dataDirection, boolean deletable) {
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

        sourceMessageValue = new JComboBox<>(
                Stream.of(MessageValue.values())
                        .filter(messageValue -> messageValue.getDataDirection() == dataDirection && messageValue.isMessageGettable())
                        .toArray(MessageValue[]::new)
        );
        sourceIdentifier = createTextField();
        sourceIdentifierPlacement = new JComboBox<>(GetItemPlacement.values());
        destinationVariableSource = new JComboBox<>(new VariableSource[] { VariableSource.Event, VariableSource.Global });
        destinationVariableName = createTextField();

        sourceMessageValue.setSelectedItem(model.getSourceMessageValue());
        sourceIdentifier.setText(model.getSourceIdentifier());
        sourceIdentifierPlacement.setSelectedItem(model.getSourceIdentifierPlacement());
        destinationVariableSource.setSelectedItem(model.getDestinationVariableSource());
        destinationVariableName.setText(model.getDestinationVariableName());

        sourceMessageValue.addActionListener(this::onSourceMessageValueChanged);
        sourceIdentifier.getDocument().addDocumentListener(new DocumentActionListener(this::onSourceIdentifierChanged));
        sourceIdentifierPlacement.addActionListener(this::onSourceIdentifierPlacementChanged);
        destinationVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onDestinationVariableNameChanged));
        destinationVariableSource.addActionListener(this::onDestinationVariableSourceChanged);

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

    private void onDelete(ActionEvent actionEvent) {
        model.setDeleted(true);
    }

    protected Component getPaddedButton(JButton button) {
        JPanel outerContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outerContainer.setAlignmentX(LEFT_ALIGNMENT);
        outerContainer.setAlignmentY(TOP_ALIGNMENT);
        outerContainer.add(button);
        return outerContainer;
    }
}
