package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.MessageAnnotation;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;

import java.util.ArrayList;
import java.util.List;

public class AnnotationVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private MessageAnnotation messageAnnotation = MessageAnnotation.Comment;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setMessageAnnotation(MessageAnnotation messageAnnotation) {
        this.messageAnnotation = messageAnnotation;
        propertyChanged("messageAnnotation", messageAnnotation);
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        return errors;
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.Annotation;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableSourceEntry.getShortTag(
                        VariableSource.Annotation,
                        messageAnnotation.name().toLowerCase()
                ) :
                null;
    }
}
