package synfron.reshaper.burp.ui.components.vars;

import javax.swing.*;
import java.awt.*;

public class VariablesTabComponent extends JPanel {

    private VariableListComponent variablesList;

    public VariablesTabComponent() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        variablesList = new VariableListComponent();
        VariableContainerComponent variableContainer = new VariableContainerComponent();
        variablesList.setSelectionContainer(variableContainer);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, variablesList, variableContainer);
        splitPane.setDividerSize(3);

        add(splitPane);
    }
}
