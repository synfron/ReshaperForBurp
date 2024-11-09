package synfron.reshaper.burp.ui.components.rules.wizard.matchreplace;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.components.shared.PromptTextField;
import synfron.reshaper.burp.ui.models.rules.wizard.matchreplace.MatchAndReplaceWizardModel;
import synfron.reshaper.burp.ui.models.rules.wizard.matchreplace.MatchType;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MatchAndReplaceWizardComponent extends JPanel implements IFormComponent {

    private final MatchAndReplaceWizardModel model;
    private JComboBox<MatchType> matchType;
    private JTextField identifier;
    private PromptTextField match;
    private JLabel matchLabel;
    private PromptTextField replace;
    private JCheckBox regexMatch;
    private final IEventListener<PropertyChangedArgs> modelChangedListener = this::onModelChanged;

    public MatchAndReplaceWizardComponent(MatchAndReplaceWizardModel model) {
        this.model = model;
        initComponent();

        model.withListener(modelChangedListener);
    }

    private void onModelChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("invalidated") && model.isInvalidated()) {
            JOptionPane.showMessageDialog(this,
                    String.join("\n", model.validate()),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponent() {
        add(getBody(), BorderLayout.CENTER);
    }

    private Component getBody() {
        JPanel container = new JPanel(new MigLayout());

        matchType = createComboBox(MatchType.values());
        identifier = createTextField(true);
        matchLabel = new JLabel(model.getMatchType().isAllowEmptyMatch() ?
                "Match" :
                "Match *"
        );
        match = createPromptTextField(getMatchPlaceHolderText(model.getMatchType()), true);
        replace = createPromptTextField(getReplacePlaceHolderText(model.getMatchType()),true);
        regexMatch = new JCheckBox("Regex match");

        matchType.setSelectedItem(model.getMatchType());
        identifier.setText(model.getIdentifier());
        match.setText(model.getMatch());
        replace.setText(model.getReplace());
        regexMatch.setSelected(model.isRegexMatch());

        matchType.addActionListener(this::onMatchTypeChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));
        match.getDocument().addDocumentListener(new DocumentActionListener(this::onMatchChanged));
        replace.getDocument().addDocumentListener(new DocumentActionListener(this::onReplaceChanged));
        regexMatch.addActionListener(this::onRegexMatchChanged);

        container.add(getLabeledField("Type", matchType), "wrap");
        container.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier *", identifier),
                matchType,
                () -> ((MatchType)matchType.getSelectedItem()).hasCustomIdentifier()
        ), "wrap");
        container.add(getLabeledField(matchLabel, match), "wrap");
        container.add(getLabeledField("Replace", replace), "wrap");
        container.add(regexMatch, "wrap");

        return container;
    }

    private String getMatchPlaceHolderText(MatchType matchType) {
        return switch (matchType) {
            case RequestHeaderLine, ResponseHeaderLine -> "Leave blank to add a new header";
            default -> "Regex / text to match";
        };
    }

    private String getReplacePlaceHolderText(MatchType matchType) {
        return switch (matchType) {
            case RequestHeaderLine, ResponseHeaderLine -> "Leave blank to remove the header";
            default -> "Literal string to replace";
        };
    }

    private void onMatchTypeChanged(ActionEvent actionEvent) {
        MatchType matchType = (MatchType) this.matchType.getSelectedItem();
        model.setMatchType(matchType);
        matchLabel.setText(model.getMatchType().isAllowEmptyMatch() ?
                "Match" :
                "Match *"
        );
        match.getTextPrompt().setText(getMatchPlaceHolderText(model.getMatchType()));
        replace.getTextPrompt().setText(getReplacePlaceHolderText(model.getMatchType()));
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier(identifier.getText());
    }

    private void onMatchChanged(ActionEvent actionEvent) {
        model.setMatch(match.getText());
    }

    private void onReplaceChanged(ActionEvent actionEvent) {
        model.setReplace(replace.getText());
    }

    private void onRegexMatchChanged(ActionEvent actionEvent) {
        model.setRegexMatch(regexMatch.isSelected());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
