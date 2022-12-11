package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.ContentType;
import synfron.reshaper.burp.core.rules.whens.WhenContentType;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class WhenContentTypeModel extends WhenModel<WhenContentTypeModel, WhenContentType> {

    @Getter
    private ContentType contentType;

    public WhenContentTypeModel(ProtocolType protocolType, WhenContentType when, Boolean isNew) {
        super(protocolType, when, isNew);
        contentType = when.getContentType();
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
        propertyChanged("contentType", contentType);
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setContentType(contentType);
        return super.persist();
    }

    @Override
    public boolean record() {
        if (validate().size() != 0) {
            return false;
        }
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return contentType.getName();
    }

    @Override
    public RuleOperationModelType<WhenContentTypeModel, WhenContentType> getType() {
        return WhenModelType.ContentType;
    }
}
