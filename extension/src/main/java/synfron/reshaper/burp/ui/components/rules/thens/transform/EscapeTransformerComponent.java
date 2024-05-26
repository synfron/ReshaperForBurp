package synfron.reshaper.burp.ui.components.rules.thens.transform;

import synfron.reshaper.burp.core.rules.thens.entities.transform.EntityType;
import synfron.reshaper.burp.core.rules.thens.entities.transform.EscapeTransform;
import synfron.reshaper.burp.ui.models.rules.thens.transform.EscapeTransformerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EscapeTransformerComponent extends TransformerComponent<EscapeTransformerModel> {

    private JComboBox<EntityType> entityType;
    private JComboBox<EscapeTransform> action;

    public EscapeTransformerComponent(EscapeTransformerModel model) {
        super(model);
    }

    protected void initComponent() {
        action = createComboBox(EscapeTransform.values());
        entityType = createComboBox(EntityType.values());

        entityType.setSelectedItem(model.getEntityType());
        action.setSelectedItem(model.getAction());

        entityType.addActionListener(this::onEntityTypeChanged);
        action.addActionListener(this::onActionChanged);

        add(getLabeledField("Entity Type", entityType), "wrap");
        add(getLabeledField("Action", action), "wrap");
    }

    private void onActionChanged(ActionEvent actionEvent) {
        model.setAction((EscapeTransform)action.getSelectedItem());
    }

    private void onEntityTypeChanged(ActionEvent actionEvent) {
        model.setEntityType((EntityType)entityType.getSelectedItem());
    }
}
