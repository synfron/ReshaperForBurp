package synfron.reshaper.burp.ui.components.shared;

import lombok.Getter;

import javax.swing.*;

@Getter
public class PromptTextField extends JTextField {

    private final TextPrompt textPrompt;

    public PromptTextField(String promptText) {
        textPrompt = new TextPrompt(promptText, this);
    }
}
