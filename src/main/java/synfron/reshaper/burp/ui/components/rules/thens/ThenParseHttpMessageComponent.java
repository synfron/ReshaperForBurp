package synfron.reshaper.burp.ui.components.rules.thens;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenParseHttpMessage;
import synfron.reshaper.burp.ui.components.rules.thens.parsehttpmessage.MessageValueGetterComponent;
import synfron.reshaper.burp.ui.models.rules.thens.ThenParseHttpMessageModel;
import synfron.reshaper.burp.ui.models.rules.thens.parsehttpmessage.MessageValueGetterModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ThenParseHttpMessageComponent extends ThenComponent<ThenParseHttpMessageModel, ThenParseHttpMessage> {
    private JComboBox<DataDirection> dataDirection;
    private JTextField httpMessage;
    private JPanel messageValueGettersComponent;
    private final IEventListener<PropertyChangedArgs> messageValueGetterChangedListener = this::onMessageValueGetterChanged;

    public ThenParseHttpMessageComponent(ThenParseHttpMessageModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        dataDirection = new JComboBox<>(DataDirection.values());
        httpMessage = createTextField();
        JButton addGetter = new JButton("Add Getter");

        dataDirection.setSelectedItem(model.getDataDirection());
        httpMessage.setText(model.getHttpMessage());

        dataDirection.addActionListener(this::onSetDataDirectionChanged);
        httpMessage.getDocument().addDocumentListener(new DocumentActionListener(this::onHttpMessageChanged));
        addGetter.addActionListener(this::onAddMessageValueGetter);

        mainContainer.add(getLabeledField("Event Direction", dataDirection), "wrap");
        mainContainer.add(getLabeledField("HTTP Message", httpMessage), "wrap");
        mainContainer.add(getLabeledField("Message Value Getters", getMessageValueGetterList()), "wrap");
        mainContainer.add(getPaddedButton(addGetter), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private JPanel getMessageValueGetterList() {
        messageValueGettersComponent = new JPanel(new MigLayout());
        messageValueGettersComponent.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        if (model.getMessageValueGetters().isEmpty()) {
            model.addMessageValueGetter();
        }

        boolean deletableGetter = false;
        for (MessageValueGetterModel messageValueGetterModel : model.getMessageValueGetters()) {
            messageValueGetterModel.withListener(messageValueGetterChangedListener);
            messageValueGettersComponent.add(new MessageValueGetterComponent(messageValueGetterModel, model.getDataDirection(), deletableGetter), "wrap");
            deletableGetter = true;
        }
        return messageValueGettersComponent;
    }

    private void onSetDataDirectionChanged(ActionEvent actionEvent) {
        for (MessageValueGetterModel messageValueGetterModel : new ArrayList<>(model.getMessageValueGetters())) {
            removeMessageValueGetter(messageValueGetterModel);
        }
        model.setDataDirection((DataDirection) dataDirection.getSelectedItem());

        addMessageValueGetter();
    }

    private void onMessageValueGetterChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("deleted") && (boolean)propertyChangedArgs.getValue()) {
            MessageValueGetterModel messageValueGetterModel = (MessageValueGetterModel)propertyChangedArgs.getSource();
            removeMessageValueGetter(messageValueGetterModel);
        }
    }

    private void removeMessageValueGetter(MessageValueGetterModel messageValueGetterModel) {
        int index = model.removeMessageValueGetter(messageValueGetterModel);
        messageValueGettersComponent.remove(index);
        messageValueGettersComponent.revalidate();
        messageValueGettersComponent.repaint();
    }

    private void onAddMessageValueGetter(ActionEvent actionEvent) {
        addMessageValueGetter();
    }

    private void addMessageValueGetter() {
        boolean deletable = !model.getMessageValueGetters().isEmpty();
        MessageValueGetterModel messageValueGetterModel = model.addMessageValueGetter()
                .withListener(messageValueGetterChangedListener);
        messageValueGettersComponent.add(new MessageValueGetterComponent(
                messageValueGetterModel,
                model.getDataDirection(),
                deletable
        ), "wrap");
        messageValueGettersComponent.revalidate();
        messageValueGettersComponent.repaint();
    }

    private void onHttpMessageChanged(ActionEvent actionEvent) {
        model.setHttpMessage(httpMessage.getText());
    }
}
