package synfron.reshaper.burp.ui.components.rules.wizard.matchreplace;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.components.shared.PromptTextField;
import synfron.reshaper.burp.ui.models.rules.wizard.matchreplace.MatchAndReplaceWizardModel;
import synfron.reshaper.burp.ui.models.rules.wizard.matchreplace.MatchType;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.util.Objects;

public class MatchAndReplaceWizardOptionPane extends JOptionPane implements IFormComponent {

    private final JPanel container;
    private final MatchAndReplaceWizardModel model;
    private JComboBox<MatchType> matchType;
    private JTextField identifier;
    private PromptTextField match;
    private JLabel matchLabel;
    private PromptTextField replace;
    private JCheckBox regexMatch;

    private MatchAndReplaceWizardOptionPane(MatchAndReplaceWizardModel model) {
        super(new JPanel(new BorderLayout()), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new Object[]{ "OK", "Cancel" }, "OK");
        container = (JPanel)message;
        this.model = model;
        addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, this::onPropertyChanged);
        initComponent();
    }

    private void onPropertyChanged(PropertyChangeEvent event) {
        if (Objects.equals(getValue(), "OK")) {
            if (!model.updateRule()) {
                JOptionPane.showMessageDialog(this,
                        String.join("\n", model.validate()),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            model.setDismissed(true);
        }
    }

    public static void showDialog(MatchAndReplaceWizardModel model) {
        MatchAndReplaceWizardOptionPane optionPane = new MatchAndReplaceWizardOptionPane(model);
        JDialog dialog = optionPane.createDialog("Match & Replace");
        dialog.setResizable(true);

        dialog.setModal(false);
        dialog.setVisible(true);

        optionPane.container.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                dialog.pack();
            }
        });
    }

    private void initComponent() {
        container.add(getBody(), BorderLayout.CENTER);
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
