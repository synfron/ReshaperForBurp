package synfron.reshaper.burp.ui.components.rules.thens.transform;

import synfron.reshaper.burp.core.utils.PhraseCase;
import synfron.reshaper.burp.ui.models.rules.thens.transform.CaseTransformerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CaseTransformerComponent extends TransformerComponent<CaseTransformerModel> {

    private JComboBox<PhraseCase> phraseCase;

    public CaseTransformerComponent(CaseTransformerModel model) {
        super(model);
    }

    protected void initComponent() {
        phraseCase = createComboBox(PhraseCase.values());

        phraseCase.setSelectedItem(model.getPhraseCase());

        phraseCase.addActionListener(this::onPhraseCaseChanged);

        add(getLabeledField("Phrase Case", phraseCase), "wrap");
    }

    private void onPhraseCaseChanged(ActionEvent actionEvent) {
        model.setPhraseCase((PhraseCase)phraseCase.getSelectedItem());
    }
}
