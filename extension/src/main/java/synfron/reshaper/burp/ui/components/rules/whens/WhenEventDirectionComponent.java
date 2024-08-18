package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.rules.whens.WhenEventDirection;
import synfron.reshaper.burp.ui.models.rules.whens.WhenEventDirectionModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class WhenEventDirectionComponent extends WhenComponent<WhenEventDirectionModel, WhenEventDirection> {
    private JComboBox<HttpDataDirection> dataDirection;
    private final IEventListener<PropertyChangedArgs> whenPropertyChangedListener = this::onWhenPropertyChanged;

    public WhenEventDirectionComponent(ProtocolType protocolType, WhenEventDirectionModel when) {
        super(protocolType, when);
        initComponent();

        when.withListener(whenPropertyChangedListener);
    }

    private void initComponent() {
        dataDirection = createComboBox(HttpDataDirection.values());

        dataDirection.setSelectedItem(model.getDataDirection());

        dataDirection.addActionListener(this::onDataDirectionChanged);

        mainContainer.add(getLabeledField("Event Direction", dataDirection), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
    }

    private void onDataDirectionChanged(ActionEvent actionEvent) {
        model.setDataDirection((HttpDataDirection)dataDirection.getSelectedItem());
    }

    private void onWhenPropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("dataDirection")) {
            if (!Objects.equals(propertyChangedArgs.getValue(), dataDirection.getSelectedItem())) {
                dataDirection.setSelectedItem(model.getDataDirection());
            }
        }
    }
}
