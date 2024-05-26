package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.transform.EntityType;
import synfron.reshaper.burp.core.rules.thens.entities.transform.EscapeTransform;
import synfron.reshaper.burp.core.rules.thens.entities.transform.EscapeTransformer;

import java.util.List;

@Getter
public class EscapeTransformerModel extends TransformerModel<EscapeTransformerModel, EscapeTransformer> {

    private EntityType entityType;
    private EscapeTransform action;

    public EscapeTransformerModel(EscapeTransformer transformer) {
        super(transformer);
        entityType = transformer.getEntityType();
        action = transformer.getAction();
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
        propertyChanged("entityType", entityType);
    }

    public void setAction(EscapeTransform action) {
        this.action = action;
        propertyChanged("action", action);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        return errors;
    }

    @Override
    public boolean persist() {
        super.persist();
        transformer.setEntityType(entityType);
        transformer.setAction(action);
        return true;
    }
}
