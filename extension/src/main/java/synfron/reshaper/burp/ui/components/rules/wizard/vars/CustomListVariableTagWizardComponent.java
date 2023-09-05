package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import synfron.reshaper.burp.core.vars.GetListItemPlacement;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.CustomListVariableTagWizardModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class CustomListVariableTagWizardComponent<T extends CustomListVariableTagWizardModel> extends CustomVariableTagWizardComponent<T> {
    private JComboBox<GetListItemPlacement> itemPlacement;
    private JTextField index;

    public CustomListVariableTagWizardComponent(T model) {
        super(model);
    }

    @Override
    protected void initComponent() {
        super.initComponent();

        itemPlacement = createComboBox(GetListItemPlacement.values());
        index = createTextField(false);

        itemPlacement.setSelectedItem(model.getItemPlacement());
        index.setText(model.getIndex());

        itemPlacement.addActionListener(this::onItemPlacementChanged);
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        add(getLabeledField("Item Placement", itemPlacement), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Index *", index),
                itemPlacement,
                () -> ((GetListItemPlacement)itemPlacement.getSelectedItem()).isHasIndexSetter()
        ), "wrap");
    }

    private void onItemPlacementChanged(ActionEvent actionEvent) {
        model.setItemPlacement((GetListItemPlacement)itemPlacement.getSelectedItem());
    }

    private void onIndexChanged(ActionEvent actionEvent) {
        model.setIndex(index.getText());
    }
}
