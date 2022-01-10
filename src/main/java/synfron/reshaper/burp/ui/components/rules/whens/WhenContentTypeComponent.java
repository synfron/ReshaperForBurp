package synfron.reshaper.burp.ui.components.rules.whens;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.messages.ContentType;
import synfron.reshaper.burp.core.rules.whens.WhenContentType;
import synfron.reshaper.burp.ui.models.rules.whens.WhenContentTypeModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenContentTypeComponent extends WhenComponent<WhenContentTypeModel, WhenContentType> {
    private JCheckBox none;
    private JCheckBox json;
    private JCheckBox xml;
    private JCheckBox urlEncoded;
    private JCheckBox multiPart;
    private JCheckBox amf;
    private JCheckBox unknown;

    public WhenContentTypeComponent(WhenContentTypeModel when) {
        super(when);
        initComponent();
    }

    private void initComponent() {
        JPanel optionsContainer = new JPanel(new MigLayout());
        optionsContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        none = new JCheckBox(ContentType.None.getName());
        json = new JCheckBox(ContentType.Json.getName());
        xml = new JCheckBox(ContentType.Xml.getName());
        urlEncoded = new JCheckBox(ContentType.UrlEncoded.getName());
        multiPart = new JCheckBox(ContentType.MultiPart.getName());
        amf = new JCheckBox(ContentType.Amf.getName());
        unknown = new JCheckBox(ContentType.Unknown.getName());

        none.setSelected(model.getContentType().hasFlags(ContentType.None));
        json.setSelected(model.getContentType().hasFlags(ContentType.Json));
        xml.setSelected(model.getContentType().hasFlags(ContentType.Xml));
        urlEncoded.setSelected(model.getContentType().hasFlags(ContentType.UrlEncoded));
        multiPart.setSelected(model.getContentType().hasFlags(ContentType.MultiPart));
        amf.setSelected(model.getContentType().hasFlags(ContentType.Amf));
        unknown.setSelected(model.getContentType().hasFlags(ContentType.Unknown));

        none.addActionListener(this::onNoneChanged);
        json.addActionListener(this::onJsonChanged);
        xml.addActionListener(this::onXmlChanged);
        urlEncoded.addActionListener(this::onUrlEncodedChanged);
        multiPart.addActionListener(this::onMultiPartChanged);
        amf.addActionListener(this::onAmfChanged);
        unknown.addActionListener(this::onUnknownChanged);

        mainContainer.add(optionsContainer, "wrap");
        optionsContainer.add(new JLabel("Request Content Type:"), "wrap");
        optionsContainer.add(none);
        optionsContainer.add(json, "wrap");
        optionsContainer.add(xml);
        optionsContainer.add(urlEncoded, "wrap");
        optionsContainer.add(multiPart);
        optionsContainer.add(amf, "wrap");
        optionsContainer.add(unknown);

        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
    }

    private void onNoneChanged(ActionEvent actionEvent) {
        model.setContentType(none.isSelected() ?
                model.getContentType().or(ContentType.None) :
                model.getContentType().minus(ContentType.None)
        );
    }

    private void onJsonChanged(ActionEvent actionEvent) {
        model.setContentType(json.isSelected() ?
                model.getContentType().or(ContentType.Json) :
                model.getContentType().minus(ContentType.Json)
        );
    }

    private void onXmlChanged(ActionEvent actionEvent) {
        model.setContentType(xml.isSelected() ?
                model.getContentType().or(ContentType.Xml) :
                model.getContentType().minus(ContentType.Xml)
        );
    }

    private void onUrlEncodedChanged(ActionEvent actionEvent) {
        model.setContentType(urlEncoded.isSelected() ?
                model.getContentType().or(ContentType.UrlEncoded) :
                model.getContentType().minus(ContentType.UrlEncoded)
        );
    }

    private void onMultiPartChanged(ActionEvent actionEvent) {
        model.setContentType(multiPart.isSelected() ?
                model.getContentType().or(ContentType.MultiPart) :
                model.getContentType().minus(ContentType.MultiPart)
        );
    }

    private void onAmfChanged(ActionEvent actionEvent) {
        model.setContentType(amf.isSelected() ?
                model.getContentType().or(ContentType.Amf) :
                model.getContentType().minus(ContentType.Amf)
        );
    }

    private void onUnknownChanged(ActionEvent actionEvent) {
        model.setContentType(unknown.isSelected() ?
                model.getContentType().or(ContentType.Unknown) :
                model.getContentType().minus(ContentType.Unknown)
        );
    }
}
