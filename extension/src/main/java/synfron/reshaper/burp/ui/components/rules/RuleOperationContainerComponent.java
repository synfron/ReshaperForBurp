package synfron.reshaper.burp.ui.components.rules;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.utils.ObjectUtils;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModel;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public abstract class RuleOperationContainerComponent extends JPanel {

    private final ProtocolType protocolType;

    public RuleOperationContainerComponent(ProtocolType protocolType) {
        this.protocolType = protocolType;
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());
    }

    public void setModel(RuleOperationModel<?,?> model) {
        removeAll();
        if (model != null) {
            add(getComponent(model));
        }
        revalidate();
        repaint();
    }

    private Component getComponent(RuleOperationModel<?,?> model) {
        Class<?> componentClass = getComponentMap().get(model.getType());
        return (Component)ObjectUtils.construct(componentClass, protocolType, model);
    }
    
    protected abstract Map<RuleOperationModelType<?,?>, Class<?>> getComponentMap();
}
