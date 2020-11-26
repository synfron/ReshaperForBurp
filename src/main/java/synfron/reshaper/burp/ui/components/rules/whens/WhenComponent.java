package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.rules.whens.When;
import synfron.reshaper.burp.ui.components.rules.RuleOperationComponent;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public abstract class WhenComponent<P extends WhenModel<P, T>, T extends When<T>> extends RuleOperationComponent<P, T> {
    protected JCheckBox useOrCondition;
    protected JCheckBox negate;

    public WhenComponent(P model) {
        super(model);
        setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(String.format("When %s", model.getType().getName())),
                BorderFactory.createEmptyBorder(4,4,4,4))
        );
    }

    protected List<Component> getDefaultComponents() {
        useOrCondition = new JCheckBox("Use OR Condition");
        negate = new JCheckBox("Negate Result");

        useOrCondition.setSelected(model.isUseOrCondition());
        negate.setSelected(model.isNegate());

        useOrCondition.addActionListener(this::onUseOrConditionChanged);
        negate.addActionListener(this::onNegateChanged);

        return List.of(
                useOrCondition,
                negate
        );
    }

    private void onNegateChanged(ActionEvent actionEvent) {
        model.setNegate(negate.isSelected());
    }

    private void onUseOrConditionChanged(ActionEvent actionEvent) {
        model.setUseOrCondition(useOrCondition.isSelected());
    }
}
