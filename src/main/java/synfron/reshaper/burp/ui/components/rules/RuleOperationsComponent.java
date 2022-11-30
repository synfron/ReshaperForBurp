package synfron.reshaper.burp.ui.components.rules;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModel;
import synfron.reshaper.burp.ui.models.rules.RuleModel;

import javax.swing.*;
import java.awt.*;

public abstract class RuleOperationsComponent<T extends RuleOperationModel<?,?>> extends JPanel {

    protected final ProtocolType protocolType;

    protected final RuleModel model;
    protected JPanel ruleOperationContainer;

    public RuleOperationsComponent(ProtocolType protocolType, RuleModel model) {
        this.protocolType = protocolType;
        this.model = model;
        initComponent();
    }

    protected void initComponent() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(getHeader(), BorderLayout.PAGE_START);
        add(getBody(), BorderLayout.CENTER);
    }

    private Component getHeader() {
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel header = new JLabel(getHeaderText());

        container.add(header);
        return container;
    }

    private Component getBody() {
        JPanel container = new JPanel(new MigLayout());

        ruleOperationContainer = new JPanel(new BorderLayout());


        RuleOperationListComponent<T> operationListComponent = getOperationList();

        ruleOperationContainer.add(operationListComponent.getRuleOperationContainer());

        container.add(operationListComponent, "width 100%, height 30%, wrap");
        container.add(ruleOperationContainer, "width 100%, height 70%");

        return container;
    }

    protected abstract RuleOperationListComponent<T> getOperationList();

    protected abstract String getHeaderText();
}
