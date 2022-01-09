package synfron.reshaper.burp.ui.components.rules.whens;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.messages.MimeType;
import synfron.reshaper.burp.core.rules.whens.WhenMimeType;
import synfron.reshaper.burp.ui.models.rules.whens.WhenMimeTypeModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenMimeTypeComponent extends WhenComponent<WhenMimeTypeModel, WhenMimeType> {
    private JCheckBox html;
    private JCheckBox script;
    private JCheckBox css;
    private JCheckBox json;
    private JCheckBox svg;
    private JCheckBox otherXml;
    private JCheckBox otherText;
    private JCheckBox image;
    private JCheckBox otherBinary;
    private JCheckBox unknown;

    public WhenMimeTypeComponent(WhenMimeTypeModel when) {
        super(when);
        initComponent();
    }

    private void initComponent() {
        JPanel optionsContainer = new JPanel(new MigLayout());
        optionsContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        html = new JCheckBox(MimeType.Html.getName());
        script = new JCheckBox(MimeType.Script.getName());
        css = new JCheckBox(MimeType.Css.getName());
        json = new JCheckBox(MimeType.Json.getName());
        svg = new JCheckBox(MimeType.Svg.getName());
        otherXml = new JCheckBox(MimeType.OtherXml.getName());
        otherText = new JCheckBox(MimeType.OtherText.getName());
        image = new JCheckBox(MimeType.Image.getName());
        otherBinary = new JCheckBox(MimeType.OtherBinary.getName());
        unknown = new JCheckBox(MimeType.Unknown.getName());

        html.setSelected(model.getMimeType().hasFlags(MimeType.Html));
        script.setSelected(model.getMimeType().hasFlags(MimeType.Script));
        css.setSelected(model.getMimeType().hasFlags(MimeType.Css));
        json.setSelected(model.getMimeType().hasFlags(MimeType.Json));
        svg.setSelected(model.getMimeType().hasFlags(MimeType.Svg));
        otherXml.setSelected(model.getMimeType().hasFlags(MimeType.OtherXml));
        otherText.setSelected(model.getMimeType().hasFlags(MimeType.OtherText));
        image.setSelected(model.getMimeType().hasFlags(MimeType.Image));
        otherBinary.setSelected(model.getMimeType().hasFlags(MimeType.OtherBinary));
        unknown.setSelected(model.getMimeType().hasFlags(MimeType.Unknown));

        html.addActionListener(this::onHtmlChanged);
        script.addActionListener(this::onScriptChanged);
        css.addActionListener(this::onCssChanged);
        json.addActionListener(this::onJsonChanged);
        svg.addActionListener(this::onSvgChanged);
        otherXml.addActionListener(this::onOtherXmlChanged);
        otherText.addActionListener(this::onOtherTextChanged);
        image.addActionListener(this::onImageChanged);
        otherBinary.addActionListener(this::onOtherBinaryChanged);
        unknown.addActionListener(this::onUnknownChanged);

        mainContainer.add(optionsContainer, "wrap");
        optionsContainer.add(new JLabel("Response MIME Type:"), "wrap");
        optionsContainer.add(html);
        optionsContainer.add(script, "wrap");
        optionsContainer.add(css);
        optionsContainer.add(json, "wrap");
        optionsContainer.add(svg);
        optionsContainer.add(otherXml, "wrap");
        optionsContainer.add(otherText);
        optionsContainer.add(image, "wrap");
        optionsContainer.add(otherBinary);
        optionsContainer.add(unknown, "wrap");

        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
    }

    private void onHtmlChanged(ActionEvent actionEvent) {
        model.setMimeType(html.isSelected() ?
                model.getMimeType().or(MimeType.Html) :
                model.getMimeType().minus(MimeType.Html)
        );
    }

    private void onScriptChanged(ActionEvent actionEvent) {
        model.setMimeType(script.isSelected() ?
                model.getMimeType().or(MimeType.Script) :
                model.getMimeType().minus(MimeType.Script)
        );
    }

    private void onOtherXmlChanged(ActionEvent actionEvent) {
        model.setMimeType(otherXml.isSelected() ?
                model.getMimeType().or(MimeType.OtherXml) :
                model.getMimeType().minus(MimeType.OtherXml)
        );
    }

    private void onCssChanged(ActionEvent actionEvent) {
        model.setMimeType(css.isSelected() ?
                model.getMimeType().or(MimeType.Css) :
                model.getMimeType().minus(MimeType.Css)
        );
    }

    private void onOtherTextChanged(ActionEvent actionEvent) {
        model.setMimeType(otherText.isSelected() ?
                model.getMimeType().or(MimeType.OtherText) :
                model.getMimeType().minus(MimeType.OtherText)
        );
    }

    private void onImageChanged(ActionEvent actionEvent) {
        model.setMimeType(image.isSelected() ?
                model.getMimeType().or(MimeType.Image) :
                model.getMimeType().minus(MimeType.Image)
        );
    }

    private void onJsonChanged(ActionEvent actionEvent) {
        model.setMimeType(json.isSelected() ?
                model.getMimeType().or(MimeType.Json) :
                model.getMimeType().minus(MimeType.Json)
        );
    }

    private void onSvgChanged(ActionEvent actionEvent) {
        model.setMimeType(svg.isSelected() ?
                model.getMimeType().or(MimeType.Svg) :
                model.getMimeType().minus(MimeType.Svg)
        );
    }

    private void onOtherBinaryChanged(ActionEvent actionEvent) {
        model.setMimeType(otherBinary.isSelected() ?
                model.getMimeType().or(MimeType.OtherBinary) :
                model.getMimeType().minus(MimeType.OtherBinary)
        );
    }

    private void onUnknownChanged(ActionEvent actionEvent) {
        model.setMimeType(unknown.isSelected() ? 
                model.getMimeType().or(MimeType.Unknown) : 
                model.getMimeType().minus(MimeType.Unknown)
        );
    }
}
