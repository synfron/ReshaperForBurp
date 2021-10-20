package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenBuildMessage;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.components.rules.thens.buildmessage.MessageSetterComponent;
import synfron.reshaper.burp.ui.models.rules.thens.ThenBuildMessageModel;
import synfron.reshaper.burp.ui.models.rules.thens.buildmessage.MessageSetterModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;
import synfron.reshaper.burp.ui.utils.WrapLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class ThenBuildMessageComponent<P extends ThenBuildMessageModel<P, T>, T extends ThenBuildMessage<T>> extends ThenComponent<P, T> {
    private final DataDirection dataDirection;
    private JTextField starterMessage;
    private JComboBox<VariableSource> variableSource;
    private JTextField variableName;
    private JPanel messageSettersComponent;
    private final IEventListener<PropertyChangedArgs> messageSetterChangedListener = this::onMessageSetterChanged;

    public ThenBuildMessageComponent(P then, DataDirection dataDirection) {
        super(then);
        this.dataDirection = dataDirection;
        initComponent();
    }

    private void initComponent() {
        starterMessage = new JTextField();
        variableSource = new JComboBox<>(new VariableSource[] { VariableSource.Event, VariableSource.Global });
        variableName = new JTextField();
        JButton addSetter = new JButton("Add Setter");

        starterMessage.setText(model.getStarterMessage());
        variableSource.setSelectedItem(model.getVariableSource());
        variableName.setText(model.getVariableName());

        starterMessage.getDocument().addDocumentListener(new DocumentActionListener(this::onStarterMessageChanged));
        variableSource.addActionListener(this::onVariableSourceChanged);
        variableName.getDocument().addDocumentListener(new DocumentActionListener(this::onVariableNameChanged));
        addSetter.addActionListener(this::onAddSetter);

        mainContainer.add(getLabeledField("Starter Message", starterMessage), "wrap");
        mainContainer.add(getLabeledField("Destination Variable Source", variableSource), "wrap");
        mainContainer.add(getLabeledField("Destination Variable Name", variableName), "wrap");
        mainContainer.add(getLabeledField("Message Setters", getMessageSetterList()), "wrap");
        mainContainer.add(getPaddedButton(addSetter), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private JPanel getMessageSetterList() {
        messageSettersComponent = new JPanel(new WrapLayout());
        messageSettersComponent.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        if (model.getMessageSetters().isEmpty()) {
            model.addMessageSetter();
        }

        boolean deletableSetter = false;
        for (MessageSetterModel messageSetterModel : model.getMessageSetters()) {
            messageSetterModel.withListener(messageSetterChangedListener);
            messageSettersComponent.add(new MessageSetterComponent(messageSetterModel, dataDirection, deletableSetter));
            deletableSetter = true;
        }
        return messageSettersComponent;
    }

    private void onMessageSetterChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("deleted") && (boolean)propertyChangedArgs.getValue()) {
            MessageSetterModel messageSetterModel = (MessageSetterModel)propertyChangedArgs.getSource();
            int index = model.removeMessageSetter(messageSetterModel);
            messageSettersComponent.remove(index);
            messageSettersComponent.revalidate();
            messageSettersComponent.repaint();
        }
    }

    private void onAddSetter(ActionEvent actionEvent) {
        MessageSetterModel messageSetterModel = model.addMessageSetter();
        messageSettersComponent.add(new MessageSetterComponent(messageSetterModel, dataDirection, true));
        messageSettersComponent.revalidate();
        messageSettersComponent.repaint();
        this.model.markChanged();
    }

    private void onStarterMessageChanged(ActionEvent actionEvent) {
        model.setStarterMessage(starterMessage.getText());
    }

    private void onVariableSourceChanged(ActionEvent actionEvent) {
        model.setVariableSource((VariableSource) variableSource.getSelectedItem());
    }

    private void onVariableNameChanged(ActionEvent actionEvent) {
        model.setVariableName(variableName.getText());
    }
}
