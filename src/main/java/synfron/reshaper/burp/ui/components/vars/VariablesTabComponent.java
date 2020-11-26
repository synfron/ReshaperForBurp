package synfron.reshaper.burp.ui.components.vars;

import javax.swing.*;
import java.awt.*;

public class VariablesTabComponent extends JPanel {

    public VariablesTabComponent() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        VariableListComponent variablesList = new VariableListComponent();
        VariableContainerComponent variableContainer = new VariableContainerComponent();
        variablesList.setSelectionContainer(variableContainer);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, variablesList, variableContainer);

        add(splitPane);
    }
}
