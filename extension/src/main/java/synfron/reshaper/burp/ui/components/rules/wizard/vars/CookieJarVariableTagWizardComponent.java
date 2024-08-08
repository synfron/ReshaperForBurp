package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.CookieJarVariableTagWizardModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CookieJarVariableTagWizardComponent extends JPanel implements IFormComponent {
    private final CookieJarVariableTagWizardModel model;
    private JComboBox<String> domain;
    private JComboBox<String> name;
    private JComboBox<String> path;

    public CookieJarVariableTagWizardComponent(CookieJarVariableTagWizardModel model) {
        this.model = model;
        initComponent();

        model.withListener(this::onModelChanged);
    }

    private void initComponent() {
        setLayout(new MigLayout());

        domain = createComboBox(model.getDomains().getOptions().toArray(new String[0]), true);
        name = createComboBox(model.getNames().getOptions().toArray(new String[0]), true);
        path = createComboBox(model.getPaths().getOptions().toArray(new String[0]), true);

        domain.setSelectedItem(model.getDomains().getSelectedOption());
        name.setSelectedItem(model.getNames().getSelectedOption());
        path.setSelectedItem(model.getPaths().getSelectedOption());

        domain.addActionListener(this::onDomainChanged);
        name.addActionListener(this::onNameChanged);
        path.addActionListener(this::onPathChanged);

        add(getLabeledField("Domain *", domain), "wrap");
        add(getLabeledField("Name *", name), "wrap");
        add(getLabeledField("Path", path));
    }

    private void onModelChanged(PropertyChangedArgs propertyChangedArgs) {
        switch (propertyChangedArgs.getName()) {
            case "names" -> resetNames();
            case "paths" -> resetPaths();
        }
    }

    private void onDomainChanged(ActionEvent actionEvent) {
        model.setDomain((String) domain.getSelectedItem());
    }

    private void onNameChanged(ActionEvent actionEvent) {
        model.setName((String) name.getSelectedItem());
    }

    private void onPathChanged(ActionEvent actionEvent) {
        model.setPath((String) path.getSelectedItem());
    }

    private void resetPaths() {
        path.removeAllItems();
        model.getPaths().getOptions().forEach(option -> path.addItem(option));
        path.setSelectedItem(model.getPaths().getSelectedOption());
    }

    private void resetNames() {
        name.removeAllItems();
        model.getNames().getOptions().forEach(option -> name.addItem(option));
        name.setSelectedItem(model.getNames().getSelectedOption());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
