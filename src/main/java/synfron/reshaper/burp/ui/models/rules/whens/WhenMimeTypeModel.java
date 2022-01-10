package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import synfron.reshaper.burp.core.messages.MimeType;
import synfron.reshaper.burp.core.rules.whens.WhenMimeType;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class WhenMimeTypeModel extends WhenModel<WhenMimeTypeModel, WhenMimeType> {

    @Getter
    private MimeType mimeType;

    public WhenMimeTypeModel(WhenMimeType when, Boolean isNew) {
        super(when, isNew);
        mimeType = when.getMimeType();
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
        propertyChanged("mimeType", mimeType);
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setMimeType(mimeType);
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
    public RuleOperationModelType<WhenMimeTypeModel, WhenMimeType> getType() {
        return WhenModelType.MimeType;
    }
}
