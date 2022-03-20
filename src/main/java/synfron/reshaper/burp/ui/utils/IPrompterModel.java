package synfron.reshaper.burp.ui.utils;

import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;

public interface IPrompterModel<T extends IPrompterModel<T>> {

    T withListener(IEventListener<PropertyChangedArgs> listener);

    void resetPropertyChangedListener();

    void setModalPrompter(ModalPrompter<T> modalPrompter);

    ModalPrompter<T> getModalPrompter();

    boolean isInvalidated();

    boolean isDismissed();

    void setDismissed(boolean dismissed);
}
