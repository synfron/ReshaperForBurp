package synfron.reshaper.burp.ui.components.rules.thens;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenBuildHttpMessage;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.components.rules.thens.buildhttpmessage.MessageValueSetterComponent;
import synfron.reshaper.burp.ui.models.rules.thens.ThenBuildHttpMessageModel;
import synfron.reshaper.burp.ui.models.rules.thens.buildhttpmessage.MessageValueSetterModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ThenBuildHttpMessageComponent extends ThenComponent<ThenBuildHttpMessageModel, ThenBuildHttpMessage> {
    private JComboBox<HttpDataDirection> dataDirection;
    private JTextField starterHttpMessage;
    private JComboBox<VariableSource> destinationVariableSource;
    private JTextField destinationVariableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;
    private JPanel messageValueSettersComponent;
    private final IEventListener<PropertyChangedArgs> messageValueSetterChangedListener = this::onMessageValueSetterChanged;

    public ThenBuildHttpMessageComponent(ProtocolType protocolType, ThenBuildHttpMessageModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        dataDirection = createComboBox(HttpDataDirection.values());
        starterHttpMessage = createTextField(true);
        destinationVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        destinationVariableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);
        JButton addSetter = new JButton("Add Setter");

        dataDirection.setSelectedItem(model.getDataDirection());
        starterHttpMessage.setText(model.getStarterHttpMessage());
        destinationVariableSource.setSelectedItem(model.getDestinationVariableSource());
        destinationVariableName.setText(model.getDestinationVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        dataDirection.addActionListener(this::onSetDataDirectionChanged);
        starterHttpMessage.getDocument().addDocumentListener(new DocumentActionListener(this::onStarterHttpMessageChanged));
        destinationVariableSource.addActionListener(this::onDestinationVariableSourceChanged);
        destinationVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onDestinationVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));
        addSetter.addActionListener(this::onAddMessageValueSetter);

        mainContainer.add(getLabeledField("Event Direction", dataDirection), "wrap");
        mainContainer.add(getLabeledField("Starter HTTP Message", starterHttpMessage), "wrap");
        mainContainer.add(getLabeledField("Message Value Setters", getMessageValueSetterList()), "wrap");
        mainContainer.add(getPaddedButton(addSetter), "wrap");
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
        mainContainer.add(getPaddedButton(validate));
    }

    private JPanel getMessageValueSetterList() {
        messageValueSettersComponent = new JPanel(new MigLayout());
        messageValueSettersComponent.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        if (model.getMessageValueSetters().isEmpty()) {
            model.addMessageValueSetter();
        }

        boolean deletableSetter = false;
        for (MessageValueSetterModel messageValueSetterModel : model.getMessageValueSetters()) {
            messageValueSetterModel.withListener(messageValueSetterChangedListener);
            messageValueSettersComponent.add(new MessageValueSetterComponent(messageValueSetterModel, model.getDataDirection(), deletableSetter), "wrap");
            deletableSetter = true;
        }
        return messageValueSettersComponent;
    }

    private void onSetDataDirectionChanged(ActionEvent actionEvent) {
        for (MessageValueSetterModel messageValueSetterModel : new ArrayList<>(model.getMessageValueSetters())) {
            removeMessageValueSetter(messageValueSetterModel);
        }
        model.setDataDirection((HttpDataDirection) dataDirection.getSelectedItem());

        addMessageValueSetter();
    }

    private void onMessageValueSetterChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("deleted") && (boolean)propertyChangedArgs.getValue()) {
            MessageValueSetterModel messageValueSetterModel = (MessageValueSetterModel)propertyChangedArgs.getSource();
            removeMessageValueSetter(messageValueSetterModel);
        }
    }

    private void removeMessageValueSetter(MessageValueSetterModel messageValueSetterModel) {
        int index = model.removeMessageValueSetter(messageValueSetterModel);
        messageValueSettersComponent.remove(index);
        messageValueSettersComponent.revalidate();
        messageValueSettersComponent.repaint();
    }

    private void onAddMessageValueSetter(ActionEvent actionEvent) {
        addMessageValueSetter();
    }

    private void addMessageValueSetter() {
        boolean deletable = !model.getMessageValueSetters().isEmpty();
        MessageValueSetterModel messageValueSetterModel = model.addMessageValueSetter()
                .withListener(messageValueSetterChangedListener);
        messageValueSettersComponent.add(new MessageValueSetterComponent(
                messageValueSetterModel,
                model.getDataDirection(),
                deletable
        ), "wrap");
        messageValueSettersComponent.revalidate();
        messageValueSettersComponent.repaint();
    }

    private void onStarterHttpMessageChanged(ActionEvent actionEvent) {
        model.setStarterHttpMessage(starterHttpMessage.getText());
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
