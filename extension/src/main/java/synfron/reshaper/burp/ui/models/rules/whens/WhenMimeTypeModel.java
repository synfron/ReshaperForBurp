package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MimeType;
import synfron.reshaper.burp.core.rules.whens.WhenMimeType;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class WhenMimeTypeModel extends WhenModel<WhenMimeTypeModel, WhenMimeType> {

    @Getter
    private MimeType mimeType;

    public WhenMimeTypeModel(ProtocolType protocolType, WhenMimeType when, Boolean isNew) {
        super(protocolType, when, isNew);
        mimeType = when.getMimeType();
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
        propertyChanged("mimeType", mimeType);
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setMimeType(mimeType);
        return super.persist();
    }

    @Override
    protected String getTargetName() {
        return mimeType.getName();
    }

    @Override
    public RuleOperationModelType<WhenMimeTypeModel, WhenMimeType> getType() {
        return WhenModelType.MimeType;
    }
}
